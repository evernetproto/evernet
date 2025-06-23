package admin

import (
	"github.com/evernetproto/evernet/internal/pkg/api"
	"github.com/gin-gonic/gin"
	"net/http"
)

type ApiHandler struct {
	router       *gin.Engine
	adminService *Service
}

func NewApiHandler(router *gin.Engine, adminService *Service) *ApiHandler {
	return &ApiHandler{router: router, adminService: adminService}
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
}
