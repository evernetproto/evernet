package vertex

import (
	"github.com/gin-gonic/gin"
	"net/http"
)

type HealthCheckApiHandler struct {
	router *gin.Engine
}

func NewHealthCheckApiHandler(router *gin.Engine) *HealthCheckApiHandler {
	return &HealthCheckApiHandler{router: router}
}

func (a *HealthCheckApiHandler) Register() {

	a.router.GET("/health", func(c *gin.Context) {
		c.JSON(http.StatusOK, &ShallowHealthCheckResponse{Status: "ok"})
	})
}
