package user

import (
	"github.com/evernetproto/evernet/internal/pkg/api"
	"github.com/gin-gonic/gin"
	"net/http"
)

type ApiHandler struct {
	router      *gin.Engine
	userService *Service
}

func NewApiHandler(router *gin.Engine, userService *Service) *ApiHandler {
	return &ApiHandler{router: router, userService: userService}
}

func (a *ApiHandler) Register() {

	a.router.POST("/api/v1/nodes/:nodeIdentifier/users/signup", func(c *gin.Context) {
		var req SignUpRequest

		if err := c.ShouldBindJSON(&req); err != nil {
			api.Error(c, http.StatusBadRequest, err)
			return
		}

		nodeIdentifier := c.Param("nodeIdentifier")

		user, err := a.userService.SignUp(&req, nodeIdentifier)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusCreated, user)
	})

	a.router.POST("/api/v1/nodes/:nodeIdentifier/users/token", func(c *gin.Context) {
		var req TokenRequest
		if err := c.ShouldBindJSON(&req); err != nil {
			api.Error(c, http.StatusBadRequest, err)
			return
		}

		nodeIdentifier := c.Param("nodeIdentifier")

		token, err := a.userService.GetToken(&req, nodeIdentifier)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, token)
	})
}
