package admin

import (
	"encoding/json"
	"errors"
	"fmt"
	"github.com/dgraph-io/badger/v4"
	"github.com/evernetproto/evernet/internal/app/evernet/vertex"
	"github.com/evernetproto/evernet/internal/pkg/auth"
	"github.com/evernetproto/evernet/internal/pkg/secret"
	"golang.org/x/crypto/bcrypt"
	"time"
)

type Service struct {
	db                  *badger.DB
	vertexConfigService *vertex.ConfigService
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

	token, err := auth.GenerateAdminToken(admin.Identifier, vertexEndpoint, jwtSigningKey)

	if err != nil {
		return nil, err
	}

	return &TokenResponse{Token: token}, nil
}

func (s *Service) Get(identifier string) (*Admin, error) {
	var admin *Admin

	err := s.db.View(func(txn *badger.Txn) error {
		item, err := txn.Get([]byte(fmt.Sprintf("%s%s", KeyPrefix, identifier)))

		if errors.Is(err, badger.ErrKeyNotFound) {
			return fmt.Errorf("admin %s not found", identifier)
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

	return admin, nil
}

func (s *Service) ChangePassword(identifier string, request *PasswordChangeRequest) (*Admin, error) {
	var admin *Admin

	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(request.Password), bcrypt.DefaultCost)

	if err != nil {
		return nil, err
	}

	err = s.db.Update(func(txn *badger.Txn) error {
		item, err := txn.Get([]byte(fmt.Sprintf("%s%s", KeyPrefix, identifier)))

		if errors.Is(err, badger.ErrKeyNotFound) {
			return fmt.Errorf("admin %s not found", identifier)
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

		admin.Password = string(hashedPassword)
		admin.UpdatedAt = time.Now()

		adminString, err := json.Marshal(admin)

		if err != nil {
			return err
		}

		err = txn.Set([]byte(fmt.Sprintf("%s%s", KeyPrefix, identifier)), adminString)

		if err != nil {
			return err
		}

		return nil
	})

	if err != nil {
		return nil, err
	}

	return admin, nil
}

func (s *Service) Add(request *AdditionRequest, creator string) (*PasswordResponse, error) {
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
		CreatedAt:  time.Now(),
		UpdatedAt:  time.Now(),
	}

	err = s.db.Update(func(txn *badger.Txn) error {
		_, err := txn.Get([]byte(fmt.Sprintf("%s%s", KeyPrefix, request.Identifier)))

		if err == nil {
			return fmt.Errorf("admin %s already exists", request.Identifier)
		}

		if !errors.Is(err, badger.ErrKeyNotFound) {
			return err
		}

		adminString, err := json.Marshal(admin)

		if err != nil {
			return err
		}

		return txn.Set([]byte(fmt.Sprintf("%s%s", KeyPrefix, request.Identifier)), adminString)
	})

	if err != nil {
		return nil, err
	}

	return &PasswordResponse{
		Password: newPassword,
		Admin:    admin,
	}, nil
}

func (s *Service) List() ([]*Admin, error) {
	prefix := []byte(KeyPrefix)
	admins := make([]*Admin, 0)

	err := s.db.View(func(txn *badger.Txn) error {
		it := txn.NewIterator(badger.DefaultIteratorOptions)
		defer it.Close()

		for it.Seek(prefix); it.ValidForPrefix(prefix); it.Next() {
			item := it.Item()

			adminString, err := item.ValueCopy(nil)

			if err != nil {
				return err
			}

			var admin Admin

			err = json.Unmarshal(adminString, &admin)

			if err != nil {
				return err
			}

			admin.Password = ""
			admins = append(admins, &admin)
		}

		return nil
	})

	if err != nil {
		return nil, err
	}

	return admins, nil
}

func (s *Service) ResetPassword(identifier string) (*PasswordResponse, error) {
	newPassword, err := secret.GenerateSecret(16)

	if err != nil {
		return nil, err
	}

	admin, err := s.ChangePassword(identifier, &PasswordChangeRequest{Password: newPassword})

	if err != nil {
		return nil, err
	}

	return &PasswordResponse{
		Password: newPassword,
		Admin:    admin,
	}, nil
}

func (s *Service) Delete() {

}
