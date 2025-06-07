package admin

import (
	"github.com/evernetproto/evernet/internal/app/evernet/vertex"
	"github.com/evernetproto/evernet/internal/pkg/auth"
	"github.com/gin-gonic/gin"
)

type Authenticator struct {
	vertexConfigService *vertex.ConfigService
}

func NewAuthenticator(vertexConfigService *vertex.ConfigService) *Authenticator {
	return &Authenticator{vertexConfigService: vertexConfigService}
}

func (a *Authenticator) ValidateAdminContext(c *gin.Context) (*auth.AuthenticatedAdmin, error) {
	jwtSigningKey, err := a.vertexConfigService.GetJwtSigningKey()
	if err != nil {
		return nil, err
	}

	vertexEndpoint, err := a.vertexConfigService.GetVertexEndpoint()
	if err != nil {
		return nil, err
	}

	return auth.ValidateAdminContext(c, jwtSigningKey, vertexEndpoint)
}
