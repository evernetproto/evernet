package admin

import (
	"encoding/json"
	"errors"
	"fmt"
	"github.com/dgraph-io/badger/v4"
	"github.com/evernetproto/evernet/internal/app/evernet/vertex"
	"github.com/evernetproto/evernet/internal/pkg/auth"
	"golang.org/x/crypto/bcrypt"
	"time"
)

type Service struct {
	db                  *badger.DB
	vertexConfigService *vertex.ConfigService
	authenticator       *auth.Authenticator
}

const (
	KeyPrefix = "admin:"
)

func NewService(db *badger.DB, vertexConfigService *vertex.ConfigService) *Service {
	return &Service{db: db, vertexConfigService: vertexConfigService}
}

func (s *Service) Init(request *InitRequest) (*Admin, error) {
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(request.Password), bcrypt.DefaultCost)

	if err != nil {
		return nil, err
	}

	admin := &Admin{
		Identifier: request.Identifier,
		Password:   string(hashedPassword),
		Creator:    request.Identifier,
		CreatedAt:  time.Now(),
		UpdatedAt:  time.Now(),
	}

	err = s.db.Update(func(txn *badger.Txn) error {
		opts := badger.DefaultIteratorOptions
		opts.PrefetchValues = false // We're only checking keys
		opts.Prefix = []byte(KeyPrefix)

		it := txn.NewIterator(opts)
		defer it.Close()

		it.Rewind()

		if it.Valid() {
			return fmt.Errorf("you are not allowed to perform this operation")
		} else {
			adminString, err := json.Marshal(admin)

			if err != nil {
				return err
			}

			err = txn.Set([]byte(fmt.Sprintf("%s%s", KeyPrefix, request.Identifier)), adminString)

			if err != nil {
				return err
			}
		}

		return nil
	})

	if err != nil {
		return nil, err
	}

	err = s.vertexConfigService.SetVertexName(request.VertexName)

	if err != nil {
		return nil, err
	}

	err = s.vertexConfigService.SetVertexDescription(request.VertexDescription)

	if err != nil {
		return nil, err
	}

	err = s.vertexConfigService.SetVertexEndpoint(request.VertexEndpoint)

	if err != nil {
		return nil, err
	}

	return admin, nil
}

func (s *Service) GetToken(request *TokenRequest) (*TokenResponse, error) {
	var admin *Admin

	err := s.db.View(func(txn *badger.Txn) error {
		item, err := txn.Get([]byte(KeyPrefix + request.Identifier))

		if errors.Is(err, badger.ErrKeyNotFound) {
			return fmt.Errorf("invalid identifier and password combination")
		}

		if err != nil {
			return err
		}

		val, err := item.ValueCopy(nil)

		if err != nil {
			return err
		}

		err = json.Unmarshal(val, &admin)
		if err != nil {
			return err
		}

		return nil
	})

	if err != nil {
		return nil, err
	}

	err = bcrypt.CompareHashAndPassword([]byte(admin.Password), []byte(request.Password))

	if err != nil {
		return nil, fmt.Errorf("invalid identifier and password combination")
	}

	jwtSigningKey, err := s.vertexConfigService.GetJwtSigningKey()
	if err != nil {
		return nil, err
	}

	vertexEndpoint, err := s.vertexConfigService.GetVertexEndpoint()
	if err != nil {
		return nil, err
	}

	token, err := s.authenticator.GenerateAdminToken(admin.Identifier, vertexEndpoint, jwtSigningKey)

	if err != nil {
		return nil, err
	}

	return &TokenResponse{Token: token}, nil
}
