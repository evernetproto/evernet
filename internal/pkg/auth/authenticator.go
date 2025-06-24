package auth

import (
	"crypto/ed25519"
	"fmt"
	"github.com/evernetproto/evernet/internal/pkg/address"
	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt/v5"
	"strings"
	"time"
)

const (
	BearerToken    = "Bearer"
	TokenTypeAdmin = "admin"
	TokenTypeUser  = "user"
)

func GenerateAdminToken(identifier string, vertexEndpoint string, jwtSigningKey string) (string, error) {
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"sub":  identifier,
		"iss":  vertexEndpoint,
		"aud":  vertexEndpoint,
		"type": TokenTypeAdmin,
		"iat":  int(time.Now().Unix()),
		"exp":  int(time.Now().Add(time.Hour * 24).Unix()),
	})

	tokenString, err := token.SignedString([]byte(jwtSigningKey))

	if err != nil {
		return "", err
	}

	return tokenString, nil
}

func GenerateUserToken(identifier string, nodeIdentifier string, vertexEndpoint string, signingPrivateKey ed25519.PrivateKey, targetNodeAddress string) (string, error) {
	nodeAddress := address.NodeAddress{
		Identifier:     nodeIdentifier,
		VertexEndpoint: vertexEndpoint,
	}

	targetNode, err := address.NodeAddressFromString(targetNodeAddress)

	if err != nil {
		return "", err
	}

	token := jwt.NewWithClaims(jwt.SigningMethodEdDSA, jwt.MapClaims{
		"sub":  identifier,
		"iss":  nodeAddress.ToString(),
		"aud":  targetNode.ToString(),
		"type": TokenTypeUser,
		"iat":  int(time.Now().Unix()),
		"exp":  int(time.Now().Add(time.Hour * 24).Unix()),
	})

	token.Header["kid"] = nodeAddress.ToString()

	tokenString, err := token.SignedString(signingPrivateKey)

	if err != nil {
		return "", err
	}

	return tokenString, nil
}

func ValidateAdminContext(c *gin.Context, jwtSigningKey string, vertexEndpoint string) (*AuthenticatedAdmin, error) {
	tokenType, token, err := extractToken(c)

	if err != nil {
		return nil, err
	}

	switch tokenType {
	case BearerToken:
		return validateAdminToken(token, jwtSigningKey, vertexEndpoint)
	default:
		return nil, fmt.Errorf("invalid token type")
	}
}

func validateAdminToken(tokenString string, jwtSigningKey string, vertexEndpoint string) (*AuthenticatedAdmin, error) {
	token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
		return []byte(jwtSigningKey), nil
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

		audience, ok := claims["aud"]

		if !ok {
			return nil, fmt.Errorf("invalid token audience")
		}

		audienceString, ok := audience.(string)

		if !ok {
			return nil, fmt.Errorf("invalid token audience")
		}

		if audienceString != vertexEndpoint {
			return nil, fmt.Errorf("invalid token audience")
		}

		issuer, ok := claims["iss"]

		if !ok {
			return nil, fmt.Errorf("invalid token issuer")
		}

		issuerString, ok := issuer.(string)

		if !ok {
			return nil, fmt.Errorf("invalid token issuer")
		}

		if issuerString != vertexEndpoint {
			return nil, fmt.Errorf("invalid token issuer")
		}

		identifier, ok := claims["sub"]

		if !ok {
			return nil, fmt.Errorf("invalid access token")
		}

		identifierString, ok := identifier.(string)

		if !ok {
			return nil, fmt.Errorf("invalid access token")
		}

		return &AuthenticatedAdmin{
			Identifier: identifierString,
		}, nil
	} else {
		return nil, fmt.Errorf("invalid access token")
	}
}

func extractToken(c *gin.Context) (string, string, error) {
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
