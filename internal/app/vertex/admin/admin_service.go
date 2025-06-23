package admin

import (
	"errors"
	"fmt"
	"github.com/evernetproto/evernet/internal/app/vertex/config"
	"github.com/evernetproto/evernet/internal/pkg/auth"
	"golang.org/x/crypto/bcrypt"
	"gorm.io/gorm"
)

type Service struct {
	db            *gorm.DB
	configService *config.Service
}

func NewService(db *gorm.DB, configService *config.Service) *Service {
	return &Service{db: db, configService: configService}
}

func (s *Service) Init(request *InitRequest) (*Admin, error) {
	var count int64
	result := s.db.Model(&Admin{}).Count(&count)

	if result.Error != nil {
		return nil, result.Error
	}

	if count != 0 {
		return nil, fmt.Errorf("you are not allowed to perform this action")
	}

	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(request.Password), bcrypt.DefaultCost)

	if err != nil {
		return nil, err
	}

	admin := &Admin{
		Identifier: request.Identifier,
		Password:   string(hashedPassword),
		Creator:    request.Identifier,
	}

	result = s.db.Create(admin)

	if result.Error != nil {
		return nil, result.Error
	}

	err = s.configService.SetVertexEndpoint(request.VertexEndpoint)

	if err != nil {
		return nil, err
	}

	err = s.configService.SetVertexIdentifier(request.VertexIdentifier)

	if err != nil {
		return nil, err
	}

	err = s.configService.SetVertexDisplayName(request.VertexDisplayName)

	if err != nil {
		return nil, err
	}

	err = s.configService.SetVertexDescription(request.VertexDescription)

	if err != nil {
		return nil, err
	}

	return admin, nil
}

func (s *Service) GetToken(request *TokenRequest) (*TokenResponse, error) {
	admin := &Admin{}

	result := s.db.Where(&Admin{Identifier: request.Identifier}).First(admin)

	if result.Error != nil {
		if errors.Is(result.Error, gorm.ErrRecordNotFound) {
			return nil, fmt.Errorf("invalid identifier and password combination")
		}

		return nil, result.Error
	}

	err := bcrypt.CompareHashAndPassword([]byte(admin.Password), []byte(request.Password))

	if err != nil {
		return nil, fmt.Errorf("invalid identifier and password combination")
	}

	vertexEndpoint, err := s.configService.GetVertexEndpoint()

	if err != nil {
		return nil, err
	}

	jwtSigningKey, err := s.configService.GetJwtSigningKey()

	if err != nil {
		return nil, err
	}

	token, err := auth.GenerateAdminToken(admin.Identifier, vertexEndpoint, jwtSigningKey)

	if err != nil {
		return nil, err
	}

	return &TokenResponse{Token: token}, nil
}
