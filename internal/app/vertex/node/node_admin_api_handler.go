package node

import (
	"github.com/evernetproto/evernet/internal/app/vertex/admin"
	"github.com/evernetproto/evernet/internal/pkg/api"
	"github.com/gin-gonic/gin"
	"net/http"
)

type AdminApiHandler struct {
	router             *gin.Engine
	adminAuthenticator *admin.Authenticator
	nodeService        *Service
}

func NewAdminApiHandler(router *gin.Engine, adminAuthenticator *admin.Authenticator, nodeService *Service) *AdminApiHandler {
	return &AdminApiHandler{router: router, adminAuthenticator: adminAuthenticator, nodeService: nodeService}
}

func (a *AdminApiHandler) Register() {

	a.router.POST("/api/v1/admins/nodes", func(c *gin.Context) {
		aa, err := a.adminAuthenticator.ValidateAdminContext(c)

		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		var req CreationRequest
		if err := c.ShouldBindJSON(&req); err != nil {
			api.Error(c, http.StatusBadRequest, err)
			return
		}

		node, err := a.nodeService.Create(&req, aa.Identifier)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusCreated, node)
	})

	a.router.GET("/api/v1/admins/nodes", func(c *gin.Context) {
		_, err := a.adminAuthenticator.ValidateAdminContext(c)

		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		page, size := api.Page(c)

		nodes, err := a.nodeService.List(page, size)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, nodes)
	})

	a.router.GET("/api/v1/admins/nodes/:nodeIdentifier", func(c *gin.Context) {
		_, err := a.adminAuthenticator.ValidateAdminContext(c)
		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		nodeIdentifier := c.Param("nodeIdentifier")

		node, err := a.nodeService.Get(nodeIdentifier)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, node)
	})

	a.router.PUT("/api/v1/admins/nodes/:nodeIdentifier", func(c *gin.Context) {
		_, err := a.adminAuthenticator.ValidateAdminContext(c)
		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		nodeIdentifier := c.Param("nodeIdentifier")

		var req UpdateRequest
		if err := c.ShouldBindJSON(&req); err != nil {
			api.Error(c, http.StatusBadRequest, err)
			return
		}

		node, err := a.nodeService.Update(nodeIdentifier, &req)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, node)
	})

	a.router.PUT("/api/v1/admins/nodes/:nodeIdentifier/open", func(c *gin.Context) {
		_, err := a.adminAuthenticator.ValidateAdminContext(c)
		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		nodeIdentifier := c.Param("nodeIdentifier")
		var req OpenUpdateRequest
		if err := c.ShouldBindJSON(&req); err != nil {
			api.Error(c, http.StatusBadRequest, err)
			return
		}

		node, err := a.nodeService.UpdateOpen(nodeIdentifier, &req)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, node)
	})

	a.router.DELETE("/api/v1/admins/nodes/:nodeIdentifier", func(c *gin.Context) {
		_, err := a.adminAuthenticator.ValidateAdminContext(c)
		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		nodeIdentifier := c.Param("nodeIdentifier")
		node, err := a.nodeService.Delete(nodeIdentifier)
		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, node)
	})

	a.router.PUT("/api/v1/admins/nodes/:nodeIdentifier/signing-keys", func(c *gin.Context) {
		_, err := a.adminAuthenticator.ValidateAdminContext(c)
		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		nodeIdentifier := c.Param("nodeIdentifier")

		node, err := a.nodeService.ResetSigningKeys(nodeIdentifier)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, node)
	})
}
