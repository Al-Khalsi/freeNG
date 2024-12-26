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

	log.Printf("%s: INFO: Starting deployment process for service '%s'.\n\tImage: %s", loc, serviceName, fullImage)

	// Login to Docker Hub
	if err := docker.LoginToDockerHub(ctx, cfg); err != nil {
		log.Printf("%s: ERROR: Docker login failed.\n\tError: %v", loc, err)
		return fmt.Errorf("docker login failed: %w", err)
	}

	// Pull the specified tag
	fullImage, err := docker.PullSpecifiedTag(ctx, fullImage, tag, image)
	if err != nil {
		log.Printf("%s: ERROR: Failed to pull image '%s'.\n\tError: %v", loc, fullImage, err)
		return fmt.Errorf("failed to pull image: %w", err)
	}

	// Stop and remove old containers
	if err := docker.ComposeDownContainers(ctx, cfg, serviceName); err != nil {
		log.Printf("%s: ERROR: Failed to stop containers for service '%s'.\n\tError: %v", loc, serviceName, err)
		return fmt.Errorf("failed to stop containers: %w", err)
	}

	// Start new containers
	if err := docker.ComposeUpContainer(ctx, cfg, serviceName); err != nil {
		log.Printf("%s: ERROR: Failed to start containers for service '%s'.\n\tError: %v", loc, serviceName, err)
		return fmt.Errorf("failed to start containers: %w", err)
	}

	// Prune dangling images
	if err := docker.PruneDanglingImages(ctx, fullImage); err != nil {
		log.Printf("%s: WARNING: Failed to prune dangling images.\n\tError: %v", loc, err)
		// Continue even if pruning fails
	}

	log.Printf("%s: INFO: Successfully deployed service '%s'.\n\tImage: %s", loc, serviceName, fullImage)
	return nil
}
