package node

import (
	"github.com/evernetproto/evernet/internal/app/evernet/admin"
	"github.com/evernetproto/evernet/internal/pkg/api"
	"github.com/gin-gonic/gin"
	"net/http"
)

type ApiHandler struct {
	router             *gin.Engine
	service            *Service
	adminAuthenticator *admin.Authenticator
}

func NewApiHandler(router *gin.Engine, service *Service, adminAuthenticator *admin.Authenticator) *ApiHandler {
	return &ApiHandler{router: router, service: service, adminAuthenticator: adminAuthenticator}
}

func (a *ApiHandler) Register() {

	a.router.POST("/api/v1/admins/nodes", func(c *gin.Context) {
		var req CreationRequest
		if err := c.ShouldBindJSON(&req); err != nil {
			api.Error(c, http.StatusBadRequest, err)
			return
		}

		ad, err := a.adminAuthenticator.ValidateAdminContext(c)

		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		node, err := a.service.Create(&req, ad.Identifier)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		node.SigningPrivateKey = ""

		c.JSON(http.StatusCreated, node)
	})

	a.router.GET("/api/v1/admins/nodes", func(c *gin.Context) {
		_, err := a.adminAuthenticator.ValidateAdminContext(c)
		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		nodes, err := a.service.List()

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, nodes)
	})

	a.router.GET("/api/v1/admins/nodes/:identifier", func(c *gin.Context) {
		_, err := a.adminAuthenticator.ValidateAdminContext(c)
		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		identifier := c.Param("identifier")
		node, err := a.service.Get(identifier)
		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, node)
	})
}
