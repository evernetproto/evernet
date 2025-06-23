package admin

import (
	"errors"
	"fmt"
	"github.com/evernetproto/evernet/internal/app/vertex/config"
	"github.com/evernetproto/evernet/internal/pkg/auth"
	"github.com/evernetproto/evernet/internal/pkg/secret"
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

func (s *Service) Get(identifier string) (*Admin, error) {
	admin := &Admin{}

	result := s.db.Where(&Admin{Identifier: identifier}).First(admin)

	if result.Error != nil {
		if errors.Is(result.Error, gorm.ErrRecordNotFound) {
			return nil, fmt.Errorf("admin %s not found", identifier)
		}

		return nil, result.Error
	}

	return admin, nil
}

func (s *Service) ChangePassword(identifier string, request *PasswordChangeRequest) (*Admin, error) {
	admin, err := s.Get(identifier)

	if err != nil {
		return nil, err
	}

	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(request.Password), bcrypt.DefaultCost)

	if err != nil {
		return nil, err
	}

	admin.Password = string(hashedPassword)

	result := s.db.Save(admin)

	if result.Error != nil {
		return nil, result.Error
	}

	return admin, nil
}

func (s *Service) Add(request *AdditionRequest, creator string) (*PasswordResponse, error) {
	var count int64

	result := s.db.Model(&Admin{}).Where(Admin{Identifier: request.Identifier}).Count(&count)

	if result.Error != nil {
		return nil, result.Error
	}

	if count != 0 {
		return nil, fmt.Errorf("admin %s already exists", request.Identifier)
	}

	newPassword, err := secret.GenerateSecret(16)

	if err != nil {
		return nil, err
	}

	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(newPassword), bcrypt.DefaultCost)

	if err != nil {
		return nil, err
	}

	admin := &Admin{
		Identifier: request.Identifier,
		Password:   string(hashedPassword),
		Creator:    creator,
	}

	result = s.db.Create(admin)

	if result.Error != nil {
		return nil, result.Error
	}

	return &PasswordResponse{Password: newPassword, Admin: admin}, nil
}

func (s *Service) List(page int, size int) ([]*Admin, error) {
	admins := make([]*Admin, 0)

	result := s.db.Limit(size).Offset(page * size).Find(&admins)

	if result.Error != nil {
		return nil, result.Error
	}

	return admins, nil
}

func (s *Service) Delete(identifier string) (*Admin, error) {
	admin, err := s.Get(identifier)

	if err != nil {
		return nil, err
	}

	result := s.db.Where(&Admin{Identifier: identifier}).Delete(&Admin{})

	if result.Error != nil {
		return nil, result.Error
	}

	return admin, nil
}

func (s *Service) ResetPassword(identifier string) (*PasswordResponse, error) {
	admin, err := s.Get(identifier)

	if err != nil {
		return nil, err
	}

	newPassword, err := secret.GenerateSecret(16)

	if err != nil {
		return nil, err
	}

	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(newPassword), bcrypt.DefaultCost)

	if err != nil {
		return nil, err
	}

	admin.Password = string(hashedPassword)

	result := s.db.Save(admin)

	if result.Error != nil {
		return nil, result.Error
	}

	return &PasswordResponse{Password: newPassword, Admin: admin}, nil
}
