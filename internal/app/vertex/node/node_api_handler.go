package node

import (
	"github.com/evernetproto/evernet/internal/pkg/api"
	"github.com/gin-gonic/gin"
	"net/http"
)

type ApiHandler struct {
	router      *gin.Engine
	nodeService *Service
}

func NewApiHandler(router *gin.Engine, nodeService *Service) *ApiHandler {
	return &ApiHandler{router: router, nodeService: nodeService}
}

func (a *ApiHandler) Register() {

	a.router.GET("/api/v1/nodes", func(c *gin.Context) {
		page, size := api.Page(c)
		nodes, err := a.nodeService.ListOpen(page, size)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, nodes)
	})

	a.router.GET("/api/v1/nodes/:nodeIdentifier", func(c *gin.Context) {
		nodeIdentifier := c.Param("nodeIdentifier")

		node, err := a.nodeService.Get(nodeIdentifier)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, node)
	})
}
