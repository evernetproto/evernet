package node

import (
	"crypto/ed25519"
	"github.com/evernetproto/evernet/internal/pkg/keys"
	"gorm.io/gorm"
)

type Node struct {
	gorm.Model
	Identifier        string `gorm:"unique" json:"identifier"`
	DisplayName       string `json:"display_name"`
	Description       string `json:"description"`
	SigningPrivateKey string `json:"-"`
	SigningPublicKey  string `json:"signing_public_key"`
	Open              bool   `json:"open"`
	Creator           string `json:"creator"`
}

func (n *Node) GetSigningPrivateKey() (ed25519.PrivateKey, error) {
	return keys.StringToPrivateKey(n.SigningPrivateKey)
}

func (n *Node) GetSigningPublicKey() (ed25519.PublicKey, error) {
	return keys.StringToPublicKey(n.SigningPublicKey)
}
