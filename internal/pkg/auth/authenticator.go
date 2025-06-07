package auth

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt"
	"strings"
	"time"
)

type Authenticator struct {
}

func NewAuthenticator() *Authenticator {
	return &Authenticator{}
}

const (
	TokenTypeAdmin = "Admin"
	BearerToken    = "Bearer"
)

func (a *Authenticator) GenerateAdminToken(identifier string, vertexEndpoint string, jwtSigningKey string) (string, error) {
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"sub":  identifier,
		"iss":  vertexEndpoint,
		"aud":  vertexEndpoint,
		"type": TokenTypeAdmin,
		"iat":  int(time.Now().Unix()),
		"exp":  int(time.Now().Add(time.Hour * 24).Unix()),
	})

	tokenString, err := token.SignedString(jwtSigningKey)

	if err != nil {
		return "", err
	}

	return tokenString, nil
}

func (a *Authenticator) ValidateAdminContext(c *gin.Context, jwtSigningKey string, vertexEndpoint string) (*AuthenticatedAdmin, error) {
	tokenType, token, err := a.extractToken(c)

	if err != nil {
		return nil, err
	}

	switch tokenType {
	case BearerToken:
		return a.validateAdminToken(token, jwtSigningKey, vertexEndpoint)
	default:
		return nil, fmt.Errorf("invalid token type")
	}
}

func (a *Authenticator) validateAdminToken(tokenString string, jwtSigningKey string, vertexEndpoint string) (*AuthenticatedAdmin, error) {
	token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
		return jwtSigningKey, nil
	})

	if err != nil {
		return nil, err
	}

	if claims, ok := token.Claims.(jwt.MapClaims); ok && token.Valid {
		tokenType, ok := claims["type"]
		if !ok {
			return nil, fmt.Errorf("invalid token type")
		}

		tokenTypeString, ok := tokenType.(string)
		if !ok {
			return nil, fmt.Errorf("invalid token type")
		}

		if tokenTypeString != TokenTypeAdmin {
			return nil, fmt.Errorf("invalid token type")
		}

		identifier, ok := claims["sub"]

		if !ok {
			return nil, fmt.Errorf("invalid access token")
		}

		identifierString, ok := identifier.(string)

		if !ok {
			return nil, fmt.Errorf("invalid access token")
		}

		issuer, ok := claims["iss"]

		if !ok {
			return nil, fmt.Errorf("invalid access token")
		}

		issuerString, ok := issuer.(string)

		if !ok {
			return nil, fmt.Errorf("invalid access token")
		}

		audience, ok := claims["aud"]

		if !ok {
			return nil, fmt.Errorf("invalid access token")
		}

		audienceString, ok := audience.(string)

		if !ok {
			return nil, fmt.Errorf("invalid access token")
		}

		if issuerString != vertexEndpoint || audienceString != vertexEndpoint {
			return nil, fmt.Errorf("invalid access token")
		}

		return &AuthenticatedAdmin{
			Identifier: identifierString,
		}, nil
	} else {
		return nil, fmt.Errorf("invalid access token")
	}
}

func (a *Authenticator) extractToken(c *gin.Context) (string, string, error) {
	authorizationHeader := c.GetHeader("Authorization")

	if len(authorizationHeader) == 0 {
		return "", "", fmt.Errorf("authorization header is not set")
	}

	components := strings.Split(authorizationHeader, " ")

	if len(components) != 2 {
		return "", "", fmt.Errorf("invalid access token")
	}

	return components[0], components[1], nil
}
