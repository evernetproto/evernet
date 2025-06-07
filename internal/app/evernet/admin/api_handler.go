package admin

import (
	"github.com/evernetproto/evernet/internal/pkg/api"
	"github.com/gin-gonic/gin"
	"net/http"
)

type ApiHandler struct {
	router        *gin.Engine
	service       *Service
	authenticator *Authenticator
}

func NewApiHandler(router *gin.Engine, service *Service, authenticator *Authenticator) *ApiHandler {
	return &ApiHandler{router: router, service: service, authenticator: authenticator}
}

func (a *ApiHandler) Register() {

	a.router.POST("/api/v1/admins/init", func(c *gin.Context) {
		var req InitRequest
		if err := c.ShouldBindJSON(&req); err != nil {
			api.Error(c, http.StatusBadRequest, err)
			return
		}

		admin, err := a.service.Init(&req)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		admin.Password = ""
		c.JSON(http.StatusCreated, admin)
	})

	a.router.POST("/api/v1/admins/token", func(c *gin.Context) {
		var req TokenRequest
		if err := c.ShouldBindJSON(&req); err != nil {
			api.Error(c, http.StatusBadRequest, err)
			return
		}

		token, err := a.service.GetToken(&req)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, token)
	})

	a.router.GET("/api/v1/admins/current", func(c *gin.Context) {
		ad, err := a.authenticator.ValidateAdminContext(c)

		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		admin, err := a.service.Get(ad.Identifier)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		admin.Password = ""
		c.JSON(http.StatusOK, admin)
	})
}
