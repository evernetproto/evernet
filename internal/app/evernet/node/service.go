package node

import (
	"encoding/json"
	"errors"
	"fmt"
	"github.com/dgraph-io/badger/v4"
	"github.com/evernetproto/evernet/internal/pkg/key"
	"time"
)

type Service struct {
	db *badger.DB
}

func NewService(db *badger.DB) *Service {
	return &Service{db: db}
}

const (
	KeyPrefix = "node:"
)

func (s *Service) Create(request *CreationRequest, creator string) (*Node, error) {

	signingPublicKey, signingPrivateKey, err := key.GenerateEd25519Keys()

	if err != nil {
		return nil, err
	}

	signingPrivateKeyString := key.KeyToBase64(signingPrivateKey)
	signingPublicKeyString := key.KeyToBase64(signingPublicKey)

	node := &Node{
		Identifier:        request.Identifier,
		Name:              request.Name,
		Description:       request.Description,
		SigningPrivateKey: signingPrivateKeyString,
		SigningPublicKey:  signingPublicKeyString,
		Open:              request.Open,
		Creator:           creator,
		CreatedAt:         time.Now(),
		UpdatedAt:         time.Now(),
	}

	err = s.db.Update(func(txn *badger.Txn) error {
		_, err = txn.Get([]byte(fmt.Sprintf("%s%s", KeyPrefix, node.Identifier)))

		if err == nil {
			return fmt.Errorf("node %s already exists", node.Identifier)
		}

		if !errors.Is(badger.ErrKeyNotFound, err) {
			return err
		}

		nodeString, err := json.Marshal(node)

		if err != nil {
			return err
		}

		return txn.Set([]byte(fmt.Sprintf("%s%s", KeyPrefix, node.Identifier)), nodeString)
	})

	if err != nil {
		return nil, err
	}

	return node, nil
}

func (s *Service) List() ([]*Node, error) {
	nodes := make([]*Node, 0)

	err := s.db.View(func(txn *badger.Txn) error {
		it := txn.NewIterator(badger.DefaultIteratorOptions)
		defer it.Close()

		prefix := []byte(KeyPrefix)
		for it.Seek(prefix); it.ValidForPrefix(prefix); it.Next() {
			item := it.Item()
			nodeString, err := item.ValueCopy(nil)

			if err != nil {
				return err
			}

			var node Node

			err = json.Unmarshal(nodeString, &node)

			if err != nil {
				return err
			}

			node.SigningPrivateKey = ""

			nodes = append(nodes, &node)
		}

		return nil
	})

	if err != nil {
		return nil, err
	}

	return nodes, nil
}
