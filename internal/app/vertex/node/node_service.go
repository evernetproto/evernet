package node

import (
	"fmt"
	"github.com/evernetproto/evernet/internal/pkg/keys"
	"gorm.io/gorm"
)

type Service struct {
	db *gorm.DB
}

func NewService(db *gorm.DB) *Service {
	return &Service{db: db}
}

func (s *Service) Create(request *CreationRequest, creator string) (*Node, error) {
	var count int64

	err := s.db.Model(&Node{}).Where(Node{Identifier: request.Identifier}).Count(&count).Error

	if err != nil {
		return nil, err
	}

	if count != 0 {
		return nil, fmt.Errorf("node %s already exists", request.Identifier)
	}

	signingPublicKey, signingPrivateKey, err := keys.GenerateKeyPair()

	if err != nil {
		return nil, err
	}

	signingPrivateKeyString := keys.PrivateKeyToString(signingPrivateKey)
	signingPublicKeyString := keys.PublicKeyToString(signingPublicKey)

	node := &Node{
		Identifier:        request.Identifier,
		DisplayName:       request.DisplayName,
		Description:       request.Description,
		SigningPublicKey:  signingPublicKeyString,
		SigningPrivateKey: signingPrivateKeyString,
		Open:              request.Open,
		Creator:           creator,
	}

	err = s.db.Create(node).Error

	if err != nil {
		return nil, err
	}

	return node, nil
}

func (s *Service) List(page int, size int) ([]*Node, error) {
	nodes := make([]*Node, 0)

	err := s.db.Limit(size).Offset(page * size).Find(&nodes).Error

	if err != nil {
		return nil, err
	}

	return nodes, nil
}

func (s *Service) ListOpen(page int, size int) ([]*Node, error) {
	nodes := make([]*Node, 0)

	err := s.db.Limit(size).Offset(page * size).Where(Node{Open: true}).Find(&nodes).Error

	if err != nil {
		return nil, err
	}

	return nodes, nil
}

func (s *Service) Get(identifier string) (*Node, error) {
	node := &Node{}

	err := s.db.Where(Node{Identifier: identifier}).First(node).Error

	if err != nil {
		return nil, err
	}

	return node, nil
}

func (s *Service) Update(identifier string, request *UpdateRequest) (*Node, error) {
	node, err := s.Get(identifier)

	if err != nil {
		return nil, err
	}

	if request.DisplayName != "" {
		node.DisplayName = request.DisplayName
	}

	node.Description = request.Description

	err = s.db.Save(node).Error

	if err != nil {
		return nil, err
	}

	return node, nil
}

func (s *Service) UpdateOpen(identifier string, request *OpenUpdateRequest) (*Node, error) {
	node, err := s.Get(identifier)

	if err != nil {
		return nil, err
	}

	node.Open = request.Open

	err = s.db.Save(node).Error

	if err != nil {
		return nil, err
	}

	return node, nil
}

func (s *Service) Delete(identifier string) (*Node, error) {
	node, err := s.Get(identifier)

	if err != nil {
		return nil, err
	}

	err = s.db.Delete(node).Error

	if err != nil {
		return nil, err
	}

	return node, nil
}

func (s *Service) ResetSigningKeys(identifier string) (*Node, error) {
	node, err := s.Get(identifier)

	if err != nil {
		return nil, err
	}

	signingPublicKey, signingPrivateKey, err := keys.GenerateKeyPair()

	if err != nil {
		return nil, err
	}

	signingPrivateKeyString := keys.PrivateKeyToString(signingPrivateKey)
	signingPublicKeyString := keys.PublicKeyToString(signingPublicKey)

	node.SigningPublicKey = signingPublicKeyString
	node.SigningPrivateKey = signingPrivateKeyString

	err = s.db.Save(node).Error

	if err != nil {
		return nil, err
	}

	return node, err
}
