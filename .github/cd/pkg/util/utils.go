package util

import (
	"fmt"
	"os"
	"path/filepath"
	"runtime"
)

// GetEnvOrDefault fetches an environment variable or returns a default value
func GetEnvOrDefault(key, defaultValue string) string {
	value := os.Getenv(key)
	if value == "" {
		return defaultValue
	}
	return value
}

// GetFileAndMethod returns the file name and method name for logging purposes
func GetFileAndMethod() string {
	pc, file, _, _ := runtime.Caller(1)
	return fmt.Sprintf("[%s:%s]", filepath.Base(file), runtime.FuncForPC(pc).Name())
}
