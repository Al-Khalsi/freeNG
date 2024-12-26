package deploy

import (
	"context"
	"fmt"
	"github.com/seyedali-dev/cd/pkg/docker"
	"github.com/seyedali-dev/cd/pkg/model"
	"github.com/seyedali-dev/cd/pkg/util"
	"log"
	"time"
)

func DeployService(ctx context.Context, cfg model.Config, serviceName, image, tag string, emailService *util.EmailService) error {
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
	fullImage, err := docker.PullSpecifiedTag(ctx, fullImage)
	if err != nil {
		log.Printf("%s: ERROR: Failed to pull image '%s'.\n\tError: %v", loc, fullImage, err)
		return fmt.Errorf("failed to pull image: %w", err)
	}

	// Prune dangling images
	if err := docker.PruneDanglingImages(ctx, fullImage); err != nil {
		log.Printf("%s: WARNING: Failed to prune dangling images.\n\tError: %v", loc, err)
		// Continue even if pruning fails
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

	// Prune dangling images again if any remain
	if err := docker.PruneDanglingImages(ctx, fullImage); err != nil {
		log.Printf("%s: WARNING: Failed to prune dangling images.\n\tError: %v", loc, err)
		// Continue even if pruning fails
	}

	log.Printf("%s: INFO: Successfully deployed service '%s'.\n\tImage: %s", loc, serviceName, fullImage)

	// Prepare email details
	subject := fmt.Sprintf("Deployment Successful: %s", serviceName)
	body := fmt.Sprintf("Service %s has been successfully deployed with image %s.\n\nDeployment Time: %s\nTag: %s\nDescription: Deployed the latest version of the service.", serviceName, fullImage, time.Now().Format(time.RFC1123), tag)

	// Send email notification
	recipients := []string{"seyed.ali.devl@gmail.com", "mohammad.hassan.alkhalsi@gmail.com"}
	if err := emailService.SendEmail(recipients, subject, body); err != nil {
		log.Printf("%s: ERROR: Failed to send email notification: %v", loc, err)
	}
	return nil
}
