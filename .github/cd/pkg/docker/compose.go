package docker

import (
	"context"
	"fmt"
	"github.com/seyedali-dev/cd/pkg/model"
	"github.com/seyedali-dev/cd/pkg/util"
	"log"
	"os/exec"
)

func LoginToDockerHub(ctx context.Context, cfg model.Config) error {
	loc := util.GetFileAndMethod()
	log.Printf("%s: INFO: Attempting to log in to Docker Hub.", loc)

	if cfg.DockerAccessToken == "" {
		log.Printf("%s: ERROR: Docker access token not set.", loc)
		return fmt.Errorf("docker access token not set in environment variables")
	}

	cmd := exec.CommandContext(ctx, "docker", "login", "-u", cfg.DockerUsername, "--password-stdin")
	stdin, err := cmd.StdinPipe()
	if err != nil {
		log.Printf("%s: ERROR: Failed to create stdin pipe: %v", loc, err)
		return fmt.Errorf("failed to create stdin pipe: %w", err)
	}

	go func() {
		defer stdin.Close()
		if _, err := stdin.Write([]byte(cfg.DockerAccessToken)); err != nil {
			log.Printf("%s: ERROR: Failed to write to stdin: %v", loc, err)
		}
	}()

	output, err := cmd.CombinedOutput()
	if err != nil {
		log.Printf("%s: ERROR: Docker login failed.\n\tOutput: %s\n\tError: %v", loc, string(output), err)
		return fmt.Errorf("docker login failed: %w", err)
	}

	log.Printf("%s: INFO: Successfully logged in to Docker Hub.", loc)
	return nil
}

func PullSpecifiedTag(ctx context.Context, fullImage string) (string, error) {
	loc := util.GetFileAndMethod()
	log.Printf("%s: INFO: Pulling image '%s'.", loc, fullImage)

	cmd := exec.CommandContext(ctx, "docker", "pull", fullImage)
	output, err := cmd.CombinedOutput()

	if err != nil {
		log.Printf("%s: ERROR: Failed to pull image '%s'.\n\tOutput: %s\n\tError: %v", loc, fullImage, string(output), err)
		return "", fmt.Errorf("failed to pull image: %w", err)
	}

	log.Printf("%s: INFO: Successfully pulled image '%s'.\n\tOutput: %s", loc, fullImage, string(output))
	return fullImage, nil
}

func PruneDanglingImages(ctx context.Context, fullImage string) error {
	loc := util.GetFileAndMethod()
	log.Printf("%s: INFO: Pruning dangling images for '%s'.", loc, fullImage)

	cmd := exec.CommandContext(ctx, "docker", "image", "prune", "-f")
	output, err := cmd.CombinedOutput()

	if err != nil {
		log.Printf("%s: ERROR: Failed to prune dangling images.\n\tOutput: %s\n\tError: %v", loc, string(output), err)
		return fmt.Errorf("failed to prune dangling images: %w", err)
	}

	log.Printf("%s: INFO: Successfully pruned dangling images.\n\tOutput: %s", loc, string(output))
	return nil
}

func ComposeDownContainers(ctx context.Context, cfg model.Config, serviceName string) error {
	loc := util.GetFileAndMethod()
	log.Printf("%s: INFO: Stopping and removing containers for service '%s'.", loc, serviceName)

	cmd := exec.CommandContext(ctx, "docker", "compose", "-f", cfg.ComposeFilePath, "down", serviceName)
	output, err := cmd.CombinedOutput()

	if err != nil {
		log.Printf("%s: ERROR: Failed to stop containers for service '%s'.\n\tOutput: %s\n\tError: %v", loc, serviceName, string(output), err)
		return fmt.Errorf("failed to stop containers: %w", err)
	}

	log.Printf("%s: INFO: Successfully stopped and removed containers for service '%s'.\n\tOutput: %s", loc, serviceName, string(output))
	return nil
}

func ComposeUpContainer(ctx context.Context, cfg model.Config, serviceName string) error {
	loc := util.GetFileAndMethod()
	log.Printf("%s: INFO: Starting containers for service '%s'.", loc, serviceName)

	cmd := exec.CommandContext(ctx, "docker", "compose", "-f", cfg.ComposeFilePath, "up", serviceName, "-d")
	output, err := cmd.CombinedOutput()

	if err != nil {
		log.Printf("%s: ERROR: Failed to start containers for service '%s'.\n\tOutput: %s\n\tError: %v", loc, serviceName, string(output), err)
		return fmt.Errorf("failed to start containers: %w", err)
	}

	log.Printf("%s: INFO: Successfully started containers for service '%s'.\n\tOutput: %s", loc, serviceName, string(output))
	return nil
}
