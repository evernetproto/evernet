package evernet

import (
	"fmt"
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
	router := gin.Default()

	vertex.NewHealthCheckApiHandler(router).Register()

	err := router.Run(fmt.Sprintf("%s:%s", s.config.Host, s.config.Port))

	if err != nil {
		log.Fatal("error starting evernet server: ", err)
	}
}
