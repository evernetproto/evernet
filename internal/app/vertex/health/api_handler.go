package health

import (
	"github.com/gin-gonic/gin"
	"net/http"
)

type ApiHandler struct {
	router *gin.Engine
}

func NewApiHandler(router *gin.Engine) *ApiHandler {
	return &ApiHandler{router: router}
}

func (a *ApiHandler) Register() {

	a.router.GET("/health/shallow", func(c *gin.Context) {
		c.JSON(http.StatusOK, &ShallowCheckResponse{
			Status: "ok",
		})
	})
}
