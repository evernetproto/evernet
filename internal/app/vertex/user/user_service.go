package user

import (
	"errors"
	"fmt"
	"github.com/evernetproto/evernet/internal/app/vertex/config"
	"github.com/evernetproto/evernet/internal/app/vertex/node"
	"github.com/evernetproto/evernet/internal/pkg/address"
	"github.com/evernetproto/evernet/internal/pkg/auth"
	"golang.org/x/crypto/bcrypt"
	"gorm.io/gorm"
)

type Service struct {
	db            *gorm.DB
	nodeService   *node.Service
	configService *config.Service
}

func NewService(db *gorm.DB, nodeService *node.Service, configService *config.Service) *Service {
	return &Service{db: db, nodeService: nodeService, configService: configService}
}

func (s *Service) SignUp(request *SignUpRequest, nodeIdentifier string) (*User, error) {
	nodeData, err := s.nodeService.Get(nodeIdentifier)

	if err != nil {
		return nil, err
	}

	if !nodeData.Open {
		return nil, fmt.Errorf("you are not allowed to perform this action")
	}

	var count int64

	err = s.db.Model(&User{}).Where(User{Identifier: request.Identifier, NodeIdentifier: nodeIdentifier}).Count(&count).Error

	if err != nil {
		return nil, err
	}

	if count > 0 {
		return nil, fmt.Errorf("user %s already exists on node %s", request.Identifier, nodeIdentifier)
	}

	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(request.Password), bcrypt.DefaultCost)
	if err != nil {
		return nil, err
	}

	user := &User{
		NodeIdentifier: nodeIdentifier,
		Identifier:     request.Identifier,
		DisplayName:    request.DisplayName,
		Password:       string(hashedPassword),
		Creator:        "",
	}

	err = s.db.Create(user).Error

	if err != nil {
		return nil, err
	}

	return user, nil
}

func (s *Service) GetToken(request *TokenRequest, nodeIdentifier string) (*TokenResponse, error) {
	nodeData, err := s.nodeService.Get(nodeIdentifier)

	if err != nil {
		return nil, err
	}

	user := &User{}

	err = s.db.Where(User{Identifier: request.Identifier, NodeIdentifier: nodeIdentifier}).First(user).Error

	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, fmt.Errorf("invalid identifier and password combination")
		}
		return nil, err
	}

	err = bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(request.Password))

	if err != nil {
		return nil, fmt.Errorf("invalid identifier and password combination")
	}

	vertexEndpoint, err := s.configService.GetVertexEndpoint()

	if err != nil {
		return nil, err
	}

	targetNodeAddress := request.TargetNodeAddress

	if targetNodeAddress == "" {
		targetNode := &address.NodeAddress{
			Identifier:     nodeIdentifier,
			VertexEndpoint: vertexEndpoint,
		}

		targetNodeAddress = targetNode.ToString()
	}

	signingPrivateKey, err := nodeData.GetSigningPrivateKey()

	if err != nil {
		return nil, err
	}

	token, err := auth.GenerateUserToken(user.Identifier, user.NodeIdentifier, vertexEndpoint, signingPrivateKey, targetNodeAddress)

	if err != nil {
		return nil, err
	}

	return &TokenResponse{Token: token}, nil
}
