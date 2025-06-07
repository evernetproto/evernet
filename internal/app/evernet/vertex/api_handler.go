package vertex

import (
	"github.com/evernetproto/evernet/internal/pkg/api"
	"github.com/gin-gonic/gin"
	"net/http"
)

type ApiHandler struct {
	router        *gin.Engine
	configService *ConfigService
}

func NewApiHandler(router *gin.Engine, configService *ConfigService) *ApiHandler {
	return &ApiHandler{router: router, configService: configService}
}

func (a *ApiHandler) Register() {

	a.router.GET("/api/v1/vertex", func(c *gin.Context) {
		endpoint, err := a.configService.GetVertexEndpoint()
		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		name, err := a.configService.GetVertexName()
		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		description, err := a.configService.GetVertexDescription()
		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, &InfoResponse{
			Endpoint:    endpoint,
			Name:        name,
			Description: description,
		})
	})
}
