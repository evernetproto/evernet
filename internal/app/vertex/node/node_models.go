package node

import "gorm.io/gorm"

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
