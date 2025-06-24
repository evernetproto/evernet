package vertex

import (
	"fmt"
	"github.com/evernetproto/evernet/internal/app/vertex/admin"
	"github.com/evernetproto/evernet/internal/app/vertex/config"
	"github.com/evernetproto/evernet/internal/app/vertex/health"
	"github.com/evernetproto/evernet/internal/app/vertex/node"
	"github.com/evernetproto/evernet/internal/app/vertex/user"
	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
	"log"
	"os"
	"path"
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
	err := os.MkdirAll(s.config.DataPath, os.ModePerm)

	if err != nil {
		log.Fatal("error initializing data directory: ", err)
	}

	db, err := gorm.Open(sqlite.Open(path.Join(s.config.DataPath, "vertex.sqlite3")), &gorm.Config{})

	if err != nil {
		log.Fatal("error initializing data database: ", err)
	}

	err = db.AutoMigrate(&config.Config{}, &admin.Admin{}, &node.Node{}, &user.User{})

	if err != nil {
		log.Fatal("error initializing database schema: ", err)
	}

	router := gin.Default()

	router.Use(cors.New(cors.Config{
		AllowOrigins:     []string{"*"},
		AllowMethods:     []string{"*"},
		AllowHeaders:     []string{"*"},
		ExposeHeaders:    []string{"*"},
		AllowCredentials: true,
	}))

	configService := config.NewService(db)
	err = configService.Init()

	if err != nil {
		log.Fatal("error initializing config service: ", err)
	}

	adminAuthenticator := admin.NewAuthenticator(configService)
	adminService := admin.NewService(db, configService)
	nodeService := node.NewService(db)
	userService := user.NewService(db, nodeService, configService)

	health.NewApiHandler(router).Register()
	admin.NewApiHandler(router, adminAuthenticator, adminService).Register()
	node.NewAdminApiHandler(router, adminAuthenticator, nodeService).Register()
	node.NewApiHandler(router, nodeService).Register()
	user.NewApiHandler(router, userService).Register()

	log.Print("starting vertex server")
	err = router.Run(fmt.Sprintf("%s:%s", s.config.Host, s.config.Port))

	if err != nil {
		log.Fatal("error starting server", err)
	}
}
