package util

import (
	"fmt"
	"os"
	"path/filepath"
	"runtime"
)

func GetEnvOrDefault(key, defaultValue string) string {
	if value := os.Getenv(key); value != "" {
		return value
	}
	return defaultValue
}

func GetFileAndMethod() string {
	pc, file, _, _ := runtime.Caller(1)
	return fmt.Sprintf("[%s:%s]", filepath.Base(file), runtime.FuncForPC(pc).Name())
}
