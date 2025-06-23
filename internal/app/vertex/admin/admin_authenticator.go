package admin

import (
	"github.com/evernetproto/evernet/internal/app/vertex/config"
	"github.com/evernetproto/evernet/internal/pkg/auth"
	"github.com/gin-gonic/gin"
)

type Authenticator struct {
	configService *config.Service
}

func (a *Authenticator) ValidateAdminContext(c *gin.Context) (*auth.AuthenticatedAdmin, error) {
	vertexEndpoint, err := a.configService.GetVertexEndpoint()

	if err != nil {
		return nil, err
	}

	jwtSigningKey, err := a.configService.GetJwtSigningKey()

	if err != nil {
		return nil, err
	}

	return auth.ValidateAdminContext(c, jwtSigningKey, vertexEndpoint)
}
