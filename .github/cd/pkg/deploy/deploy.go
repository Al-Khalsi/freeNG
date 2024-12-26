package deploy

import (
	"context"
	"fmt"
	"github.com/seyedali-dev/cd/pkg/docker"
	"github.com/seyedali-dev/cd/pkg/model"
	"github.com/seyedali-dev/cd/pkg/util"
	"log"
)

func DeployService(ctx context.Context, cfg model.Config, serviceName, image, tag string) error {
	loc := util.GetFileAndMethod()

	if tag == "" {
		tag = "latest"
	}
	fullImage := fmt.Sprintf("%s:%s", image, tag)

	// Login to docker for private repositories
	if err := docker.LoginToDockerHub(ctx, cfg); err != nil {
		log.Printf("%s: Docker login failed:\n                    Error: %v", loc, err)
		return fmt.Errorf("docker login failed: %w", err)
	}

	// Pull the specified tag
	fullImage, err := docker.PullSpecifiedTag(ctx, fullImage, tag, image)
	if err != nil {
		log.Printf("%s: Failed to pull image:\n                    Error: %v", loc, err)
		return fmt.Errorf("failed to pull image: %w", err)
	}

	// Stop and Remove old container
	if err := docker.ComposeDownContainers(ctx, cfg, serviceName); err != nil {
		log.Printf("%s: Failed to stop containers:\n                    Error: %v", loc, err)
		return fmt.Errorf("failed to stop containers: %w", err)
	}

	// Start service with docker-compose
	if err := docker.ComposeUpContainer(ctx, cfg, serviceName); err != nil {
		log.Printf("%s: Failed to start containers:\n                    Error: %v", loc, err)
		return fmt.Errorf("failed to start containers: %w", err)
	}

	// Prune dangling images
	if err := docker.PruneDanglingImages(ctx, fullImage); err != nil {
		log.Printf("%s: Warning: failed to prune images:\n                    Error: %v", loc, err)
		// Continue even if pruning fails
	}

	return nil
}
