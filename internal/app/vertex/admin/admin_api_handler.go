package admin

import (
	"github.com/evernetproto/evernet/internal/pkg/api"
	"github.com/gin-gonic/gin"
	"net/http"
)

type ApiHandler struct {
	router        *gin.Engine
	authenticator *Authenticator
	adminService  *Service
}

func NewApiHandler(router *gin.Engine, authenticator *Authenticator, adminService *Service) *ApiHandler {
	return &ApiHandler{router: router, authenticator: authenticator, adminService: adminService}
}

func (a *ApiHandler) Register() {

	a.router.POST("/api/v1/admins/init", func(c *gin.Context) {
		var req InitRequest
		if err := c.ShouldBindJSON(&req); err != nil {
			api.Error(c, http.StatusBadRequest, err)
			return
		}

		admin, err := a.adminService.Init(&req)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusCreated, admin)
	})

	a.router.POST("/api/v1/admins/token", func(c *gin.Context) {
		var req TokenRequest
		if err := c.ShouldBindJSON(&req); err != nil {
			api.Error(c, http.StatusBadRequest, err)
			return
		}

		token, err := a.adminService.GetToken(&req)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, token)
	})

	a.router.GET("/api/v1/admins/current", func(c *gin.Context) {
		aa, err := a.authenticator.ValidateAdminContext(c)

		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		admin, err := a.adminService.Get(aa.Identifier)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, admin)
	})

	a.router.PUT("/api/v1/admins/current/password", func(c *gin.Context) {
		aa, err := a.authenticator.ValidateAdminContext(c)

		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		var req PasswordChangeRequest

		if err := c.ShouldBindJSON(&req); err != nil {
			api.Error(c, http.StatusBadRequest, err)
			return
		}

		admin, err := a.adminService.ChangePassword(aa.Identifier, &req)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, admin)
	})

	a.router.POST("/api/v1/admins", func(c *gin.Context) {
		aa, err := a.authenticator.ValidateAdminContext(c)

		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		var req AdditionRequest
		if err := c.ShouldBindJSON(&req); err != nil {
			api.Error(c, http.StatusBadRequest, err)
			return
		}

		admin, err := a.adminService.Add(&req, aa.Identifier)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusCreated, admin)
	})

	a.router.GET("/api/v1/admins", func(c *gin.Context) {
		_, err := a.authenticator.ValidateAdminContext(c)

		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		page, size := api.Page(c)

		admins, err := a.adminService.List(page, size)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, admins)
	})

	a.router.GET("/api/v1/admins/:adminIdentifier", func(c *gin.Context) {
		_, err := a.authenticator.ValidateAdminContext(c)
		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		adminIdentifier := c.Param("adminIdentifier")

		admin, err := a.adminService.Get(adminIdentifier)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, admin)
	})

	a.router.DELETE("/api/v1/admins/:adminIdentifier", func(c *gin.Context) {
		_, err := a.authenticator.ValidateAdminContext(c)

		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		adminIdentifier := c.Param("adminIdentifier")

		admin, err := a.adminService.Delete(adminIdentifier)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, admin)
	})

	a.router.PUT("/api/v1/admins/:adminIdentifier/password", func(c *gin.Context) {
		_, err := a.authenticator.ValidateAdminContext(c)
		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		adminIdentifier := c.Param("adminIdentifier")
		admin, err := a.adminService.ResetPassword(adminIdentifier)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, admin)
	})
}
