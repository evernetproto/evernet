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

	a.router.PUT("/api/v1/admins/current/password", func(c *gin.Context) {
		var req PasswordChangeRequest
		if err := c.ShouldBindJSON(&req); err != nil {
			api.Error(c, http.StatusBadRequest, err)
			return
		}

		ad, err := a.authenticator.ValidateAdminContext(c)
		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		admin, err := a.service.ChangePassword(ad.Identifier, &req)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		admin.Password = ""

		c.JSON(http.StatusOK, admin)
	})

	a.router.POST("/api/v1/admins", func(c *gin.Context) {
		var req AdditionRequest
		if err := c.ShouldBindJSON(&req); err != nil {
			api.Error(c, http.StatusBadRequest, err)
			return
		}

		ad, err := a.authenticator.ValidateAdminContext(c)

		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		admin, err := a.service.Add(&req, ad.Identifier)

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		admin.Admin.Password = ""

		c.JSON(http.StatusCreated, admin)
	})

	a.router.GET("/api/v1/admins", func(c *gin.Context) {
		_, err := a.authenticator.ValidateAdminContext(c)

		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		admins, err := a.service.List()

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, admins)
	})

	a.router.GET("/api/v1/admins/:identifier", func(c *gin.Context) {
		_, err := a.authenticator.ValidateAdminContext(c)
		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		admin, err := a.service.Get(c.Param("identifier"))

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, admin)
	})

	a.router.PUT("/api/v1/admins/:identifier/password", func(c *gin.Context) {
		_, err := a.authenticator.ValidateAdminContext(c)
		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		admin, err := a.service.ResetPassword(c.Param("identifier"))

		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		admin.Admin.Password = ""
		c.JSON(http.StatusOK, admin)
	})

	a.router.DELETE("/api/v1/admins/:identifier", func(c *gin.Context) {
		_, err := a.authenticator.ValidateAdminContext(c)
		if err != nil {
			api.Error(c, http.StatusUnauthorized, err)
			return
		}

		admin, err := a.service.Delete(c.Param("identifier"))
		if err != nil {
			api.Error(c, http.StatusInternalServerError, err)
			return
		}

		c.JSON(http.StatusOK, admin)
	})
}
