package node

import "time"

type Node struct {
	Identifier        string    `json:"identifier"`
	Name              string    `json:"name"`
	Description       string    `json:"description"`
	SigningPrivateKey string    `json:"signing_private_key"`
	SigningPublicKey  string    `json:"signing_public_key"`
	Open              bool      `json:"open"`
	Creator           string    `json:"creator"`
	CreatedAt         time.Time `json:"created_at"`
	UpdatedAt         time.Time `json:"updated_at"`
}
