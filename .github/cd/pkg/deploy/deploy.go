package deploy

import (
	"context"
	"fmt"
	"github.com/seyedali-dev/cd/pkg/docker"
	"github.com/seyedali-dev/cd/pkg/model"
	"github.com/seyedali-dev/cd/pkg/util"
	"log"
)

func DeployService(ctx context.Context, cfg model.Config, serviceName, image, tag string, recipients []string, emailService *util.EmailService) error {
	loc := util.GetFileAndMethod()

	if tag == "" {
		tag = "latest"
	}
	fullImage := fmt.Sprintf("%s:%s", image, tag)

	log.Printf("%s: INFO: Starting deployment process for service '%s'.\n\tImage: %s", loc, serviceName, fullImage)

	// Login to Docker Hub
	if output, err := docker.LoginToDockerHub(ctx, cfg); err != nil {
		errorMessage := fmt.Sprintf("%s: ERROR: Docker login failed.\n\tError: %v", loc, err)
		log.Println(errorMessage)
		emailService.SendErrorEmail(recipients, serviceName, string(output), image, tag, errorMessage)
		return fmt.Errorf("docker login failed: %w", err)
	}

	// Pull the specified tag
	output, fullImage, err := docker.PullSpecifiedTag(ctx, fullImage)
	if err != nil {
		errorMessage := fmt.Sprintf("%s: ERROR: Failed to pull image '%s'.\n\tError: %v", loc, fullImage, err)
		log.Println(errorMessage)
		emailService.SendErrorEmail(recipients, serviceName, string(output), image, tag, errorMessage)
		return fmt.Errorf("failed to pull image: %w", err)
	}

	// Stop and remove old containers
	if output, err := docker.ComposeDownContainers(ctx, cfg, serviceName); err != nil {
		errorMessage := fmt.Sprintf("%s: ERROR: Failed to stop containers for service '%s'.\n\tError: %v", loc, serviceName, err)
		log.Printf(errorMessage)
		emailService.SendErrorEmail(recipients, serviceName, string(output), image, tag, errorMessage)
		return fmt.Errorf("failed to stop containers: %w", err)
	}

	// Prune dangling images
	if _, err := docker.PruneDanglingImages(ctx, fullImage); err != nil {
		log.Printf("%s: WARNING: Failed to prune dangling images.\n\tError: %v", loc, err)
		// Continue even if pruning fails
	}

	// Start new containers
	if output, err := docker.ComposeUpContainer(ctx, cfg, serviceName); err != nil {
		errorMessage := fmt.Sprintf("%s: ERROR: Failed to start containers for service '%s'.\n\tError: %v", loc, serviceName, err)
		log.Println(errorMessage)
		emailService.SendErrorEmail(recipients, serviceName, string(output), image, tag, errorMessage)
		return fmt.Errorf("failed to start containers: %w", err)
	}

	// Prune dangling images again if any remain
	if _, err := docker.PruneDanglingImages(ctx, fullImage); err != nil {
		log.Printf("%s: WARNING: Failed to prune dangling images.\n\tError: %v", loc, err)
		// Continue even if pruning fails
	}

	log.Printf("%s: INFO: Successfully deployed service '%s'.\n\tImage: %s", loc, serviceName, fullImage)
	emailService.SendSuccessEmail(recipients, serviceName, image, tag)

	return nil
}
