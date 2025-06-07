package env

import "os"

func GetOrDefault(key string, defaultValue string) string {
	v := os.Getenv(key)

	if v == "" {
		return defaultValue
	}

	return v
}
