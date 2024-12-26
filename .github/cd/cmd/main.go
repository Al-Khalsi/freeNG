package main

import (
	"context"
	"errors"
	"github.com/seyedali-dev/cd/pkg/handler"
	"github.com/seyedali-dev/cd/pkg/model"
	"github.com/seyedali-dev/cd/pkg/util"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"
)

func getConfig() model.Config {
	return model.Config{
		DockerUsername: util.GetEnvOrDefault("DOCKER_USERNAME", "dukeofjava"),
		//ComposeFilePath:   utils.GetEnvOrDefault("COMPOSE_FILE_PATH", "/home/simi/DockGE/pixel/docker-compose.yaml"),
		ComposeFilePath:   util.GetEnvOrDefault("COMPOSE_FILE_PATH", "D:\\Skill-Development\\Spring-boot-Tutorial\\source-code\\Resume-Projects\\PixelFreebies\\backend\\pixelfreebies-service\\docker-compose\\compose.yaml"),
		DockerAccessToken: os.Getenv("DOCKER_ACCESS_TOKEN"),
	}
}

func main() {
	loc := util.GetFileAndMethod()
	cfg := getConfig()

	srv := &http.Server{
		Addr:         ":5002",
		ReadTimeout:  10 * time.Second,
		WriteTimeout: 30 * time.Second,
		IdleTimeout:  120 * time.Second,
	}

	mux := http.NewServeMux()
	mux.HandleFunc("/webhook", handler.WebhookHandler(cfg))
	mux.HandleFunc("/health", handler.HealthCheckHandler)
	srv.Handler = mux

	go func() {
		sigChan := make(chan os.Signal, 1)
		signal.Notify(sigChan, syscall.SIGINT, syscall.SIGTERM)
		<-sigChan

		log.Printf("%s: INFO: Shutting down server...", loc)

		shutdownCtx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
		defer cancel()

		if err := srv.Shutdown(shutdownCtx); err != nil {
			log.Printf("%s: ERROR: HTTP server shutdown failed: %v", loc, err)
		}
	}()

	log.Printf("%s: INFO: Go webhook server started successfully", loc)
	log.Printf("%s: INFO: Listening on %s", loc, srv.Addr)

	if err := srv.ListenAndServe(); !errors.Is(err, http.ErrServerClosed) {
		log.Printf("%s: ERROR: HTTP server error: %v", loc, err)
		os.Exit(1)
	}
}
