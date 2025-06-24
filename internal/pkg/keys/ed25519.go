package keys

import (
	"crypto/ed25519"
	"crypto/rand"
	"encoding/base64"
	"errors"
)

func GenerateKeyPair() (ed25519.PublicKey, ed25519.PrivateKey, error) {
	publicKey, privateKey, err := ed25519.GenerateKey(rand.Reader)
	return publicKey, privateKey, err
}

func PublicKeyToString(publicKey ed25519.PublicKey) string {
	return base64.StdEncoding.EncodeToString(publicKey)
}

func PrivateKeyToString(privateKey ed25519.PrivateKey) string {
	return base64.StdEncoding.EncodeToString(privateKey)
}

func StringToPublicKey(publicKeyString string) (ed25519.PublicKey, error) {
	bytes, err := base64.StdEncoding.DecodeString(publicKeyString)
	if err != nil {
		return nil, err
	}
	if len(bytes) != ed25519.PublicKeySize {
		return nil, errors.New("invalid public key size")
	}
	return bytes, nil
}

func StringToPrivateKey(privateKeyString string) (ed25519.PrivateKey, error) {
	bytes, err := base64.StdEncoding.DecodeString(privateKeyString)
	if err != nil {
		return nil, err
	}
	if len(bytes) != ed25519.PrivateKeySize {
		return nil, errors.New("invalid private key size")
	}
	return bytes, nil
}
