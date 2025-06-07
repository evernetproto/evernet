package key

import (
	"crypto/ed25519"
	"crypto/rand"
	"encoding/base64"
	"fmt"
)

func GenerateEd25519Keys() (ed25519.PublicKey, ed25519.PrivateKey, error) {
	pubKey, privateKey, err := ed25519.GenerateKey(rand.Reader)
	return pubKey, privateKey, err
}

func KeyToBase64(key []byte) string {
	return base64.StdEncoding.EncodeToString(key)
}

func Base64ToPublicKey(encoded string) (ed25519.PublicKey, error) {
	bytes, err := base64.StdEncoding.DecodeString(encoded)
	if err != nil {
		return nil, err
	}
	if len(bytes) != ed25519.PublicKeySize {
		return nil, fmt.Errorf("invalid public key size")
	}
	return bytes, nil
}

func Base64ToPrivateKey(encoded string) (ed25519.PrivateKey, error) {
	bytes, err := base64.StdEncoding.DecodeString(encoded)
	if err != nil {
		return nil, err
	}
	if len(bytes) != ed25519.PrivateKeySize {
		return nil, fmt.Errorf("invalid private key size")
	}
	return bytes, nil
}
