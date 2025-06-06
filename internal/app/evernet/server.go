package evernet

import (
	"fmt"
	"github.com/dgraph-io/badger/v4"
	"github.com/evernetproto/evernet/internal/app/evernet/admin"
	"github.com/evernetproto/evernet/internal/app/evernet/node"
	"github.com/evernetproto/evernet/internal/app/evernet/vertex"
	"github.com/gin-gonic/gin"
	"log"
)

type Server struct {
	config *ServerConfig
}

func NewServer(config *ServerConfig) *Server {
	return &Server{config: config}
}

type ServerConfig struct {
	Host     string
	Port     string
	DataPath string
}

func (s *Server) Start() {
	db, err := badger.Open(badger.DefaultOptions(s.config.DataPath))
	if err != nil {
		log.Fatal(err)
	}

	defer func(db *badger.DB) {
		err := db.Close()
		if err != nil {
			log.Fatal(err)
		}
	}(db)

	router := gin.Default()

	configService := vertex.NewConfigService(db)
	configService.Init()
	adminAuthenticator := admin.NewAuthenticator(configService)
	adminService := admin.NewService(db, configService)
	nodeService := node.NewService(db)

	vertex.NewHealthCheckApiHandler(router).Register()
	admin.NewApiHandler(router, adminService, adminAuthenticator).Register()
	vertex.NewApiHandler(router, configService).Register()
	node.NewApiHandler(router, nodeService, adminAuthenticator).Register()

	err = router.Run(fmt.Sprintf("%s:%s", s.config.Host, s.config.Port))

	if err != nil {
		log.Fatal("error starting evernet server: ", err)
	}
}
