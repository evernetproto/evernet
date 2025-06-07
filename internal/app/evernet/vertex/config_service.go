package vertex

import (
	"errors"
	"fmt"
	"github.com/dgraph-io/badger/v4"
	"github.com/evernetproto/evernet/internal/pkg/secret"
	"log"
)

type ConfigService struct {
	db *badger.DB
}

const (
	KeyPrefix      = "config:"
	JwtSigningKey  = "jwt_signing_key"
	EndpointKey    = "vertex_endpoint"
	NameKey        = "vertex_name"
	DescriptionKey = "vertex_description"
)

func NewConfigService(db *badger.DB) *ConfigService {
	return &ConfigService{db: db}
}

func (s *ConfigService) getJwtSigningKeyKey() string {
	return fmt.Sprintf("%s%s", KeyPrefix, JwtSigningKey)
}

func (s *ConfigService) Init() {

	err := s.db.Update(func(txn *badger.Txn) error {
		_, err := txn.Get([]byte(s.getJwtSigningKeyKey()))

		if errors.Is(err, badger.ErrKeyNotFound) {
			jwtSigningKey, err := secret.GenerateSecret(64)

			if err != nil {
				return err
			}

			err = txn.Set([]byte(s.getJwtSigningKeyKey()), []byte(jwtSigningKey))

			if err != nil {
				return err
			}
		} else if err != nil {
			return err
		}

		return nil
	})

	if err != nil {
		log.Fatal("Failed to initialize config: ", err)
	}
}

func (s *ConfigService) Set(key string, value []byte) error {
	return s.db.Update(func(txn *badger.Txn) error {
		return txn.Set([]byte(fmt.Sprintf("%s%s", KeyPrefix, key)), value)
	})
}

func (s *ConfigService) SetVertexEndpoint(endpoint string) error {
	return s.Set(EndpointKey, []byte(endpoint))
}

func (s *ConfigService) SetVertexName(name string) error {
	return s.Set(NameKey, []byte(name))
}

func (s *ConfigService) SetVertexDescription(description string) error {
	return s.Set(DescriptionKey, []byte(description))
}

func (s *ConfigService) Get(key string) (string, error) {
	var result []byte

	err := s.db.View(func(txn *badger.Txn) error {
		item, err := txn.Get([]byte(fmt.Sprintf("%s%s", KeyPrefix, key)))

		if err != nil {
			return err
		}

		result, err = item.ValueCopy(nil)
		return err
	})

	if err != nil {
		return "", err
	}

	return string(result), nil
}

func (s *ConfigService) GetJwtSigningKey() (string, error) {
	return s.Get(JwtSigningKey)
}
