package config

import (
	"errors"
	"github.com/evernetproto/evernet/internal/pkg/secret"
	"gorm.io/gorm"
	"gorm.io/gorm/clause"
)

type Service struct {
	db *gorm.DB
}

func NewService(db *gorm.DB) *Service {
	return &Service{db: db}
}

const (
	KeyJwtSigningKey     = "jwt_signing_key"
	KeyVertexEndpoint    = "vertex_endpoint"
	KeyVertexIdentifier  = "vertex_identifier"
	KeyVertexDisplayName = "vertex_display_name"
	KeyVertexDescription = "vertex_description"
)

func (s *Service) Init() error {
	jwtSigningKey, err := secret.GenerateSecret(64)

	if err != nil {
		return err
	}

	result := s.db.Where(Config{Key: KeyJwtSigningKey}).FirstOrCreate(&Config{
		Key:   KeyJwtSigningKey,
		Value: jwtSigningKey,
	})

	if result.Error != nil {
		return result.Error
	}

	return nil
}

func (s *Service) Get(key string, defaultValue string) (string, error) {
	config := &Config{}

	result := s.db.Where(Config{Key: key}).First(config)

	if result.Error != nil {
		if errors.Is(result.Error, gorm.ErrRecordNotFound) {
			return defaultValue, nil
		}

		return "", result.Error
	}

	return config.Value, nil
}

func (s *Service) Set(key string, value string) error {
	cfg := Config{
		Key:   key,
		Value: value,
	}

	return s.db.Clauses(clause.OnConflict{
		Columns:   []clause.Column{{Name: "key"}},
		DoUpdates: clause.AssignmentColumns([]string{"value"}),
	}).Create(&cfg).Error
}

func (s *Service) GetJwtSigningKey() (string, error) {
	return s.Get(KeyJwtSigningKey, "secret")
}

func (s *Service) SetVertexEndpoint(vertexEndpoint string) error {
	return s.Set(KeyVertexEndpoint, vertexEndpoint)
}

func (s *Service) GetVertexEndpoint() (string, error) {
	return s.Get(KeyVertexEndpoint, "")
}

func (s *Service) SetVertexIdentifier(vertexIdentifier string) error {
	return s.Set(KeyVertexIdentifier, vertexIdentifier)
}

func (s *Service) GetVertexIdentifier() (string, error) {
	return s.Get(KeyVertexIdentifier, "")
}

func (s *Service) SetVertexDisplayName(displayName string) error {
	return s.Set(KeyVertexDisplayName, displayName)
}

func (s *Service) GetVertexDisplayName() (string, error) {
	return s.Get(KeyVertexDisplayName, "")
}

func (s *Service) SetVertexDescription(description string) error {
	return s.Set(KeyVertexDescription, description)
}

func (s *Service) GetVertexDescription() (string, error) {
	return s.Get(KeyVertexDescription, "")
}
