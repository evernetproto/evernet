package main

import (
	"github.com/evernetproto/evernet/internal/app/vertex"
	"github.com/evernetproto/evernet/internal/pkg/env"
)

func main() {
	vertex.NewServer(&vertex.ServerConfig{
		Host:     env.GetOrDefault("HOST", "0.0.0.0"),
		Port:     env.GetOrDefault("PORT", "5000"),
		DataPath: env.GetOrDefault("DATA_PATH", "data"),
	}).Start()
}
