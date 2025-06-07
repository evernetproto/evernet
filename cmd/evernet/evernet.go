package main

import (
	"github.com/evernetproto/evernet/internal/app/evernet"
	"github.com/evernetproto/evernet/internal/pkg/env"
)

func main() {

	evernet.NewServer(&evernet.ServerConfig{
		Host:     env.GetOrDefault("HOST", "0.0.0.0"),
		Port:     env.GetOrDefault("PORT", "3000"),
		DataPath: env.GetOrDefault("DATA_PATH", "data"),
	}).Start()
}
