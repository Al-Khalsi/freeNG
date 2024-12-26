package docker

import (
	"context"
	"fmt"
	"github.com/seyedali-dev/cd/pkg/model"
	"github.com/seyedali-dev/cd/pkg/util"
	"log"
	"os/exec"
	"path/filepath"
	"runtime"
)

// getFileAndMethod returns the current file name and method name
func getFileAndMethod() string {
	pc, file, _, _ := runtime.Caller(1)
	return fmt.Sprintf("%s:%s", filepath.Base(file), runtime.FuncForPC(pc).Name())
}

func LoginToDockerHub(ctx context.Context, cfg model.Config) error {
	loc := util.GetFileAndMethod()
	log.Printf("%s: Attempting to log in to dockerhub.", loc)

	if cfg.DockerAccessToken == "" {
		return fmt.Errorf("docker access token not set in environment variables")
	}

	cmd := exec.CommandContext(ctx, "docker", "login", "-u", cfg.DockerUsername, "--password-stdin")
	stdin, err := cmd.StdinPipe()
	if err != nil {
		log.Printf("%s: Failed to create stdin pipe:\n                    %v", loc, err)
		return fmt.Errorf("failed to create stdin pipe: %w", err)
	}

	go func() {
		defer stdin.Close()
		if _, err := stdin.Write([]byte(cfg.DockerAccessToken)); err != nil {
			log.Printf("%s: Failed to write to stdin:\n                    %v", loc, err)
		}
	}()

	output, err := cmd.CombinedOutput()
	if err != nil {
		log.Printf("%s: Failed to login to Docker:\n                    Error: %v\n                    Output: %s",
			loc, err, string(output))
		return fmt.Errorf("failed to login to Docker: %w", err)
	}

	log.Printf("%s: Successfully logged in to Docker Hub", loc)
	return nil
}

func PullSpecifiedTag(ctx context.Context, fullImage, tag, image string) (string, error) {
	loc := util.GetFileAndMethod()
	log.Printf("%s: Attempting to pull image:\n                    %s", loc, fullImage)

	cmd := exec.CommandContext(ctx, "docker", "pull", fullImage)
	output, err := cmd.CombinedOutput()

	if err != nil {
		log.Printf("%s: Failed to pull image:\n                    Image: %s\n                    Output: %s",
			loc, fullImage, string(output))
		log.Printf("%s: Tag %s not found for image %s, falling back to 'latest'",
			loc, tag, image)

		fullImage = fmt.Sprintf("%s:latest", image)
		cmd = exec.CommandContext(ctx, "docker", "pull", fullImage)
		output, err = cmd.CombinedOutput()

		if err != nil {
			log.Printf("%s: Failed to pull fallback image:\n                    Image: %s\n                    Output: %s",
				loc, fullImage, string(output))
			return "", fmt.Errorf("failed to pull fallback image %s: %w", fullImage, err)
		}
		log.Printf("%s: Successfully pulled fallback 'latest' tag:\n                    Output: %s",
			loc, string(output))
	} else {
		log.Printf("%s: Successfully pulled image:\n                    Image: %s\n                    Output: %s",
			loc, fullImage, string(output))
	}

	return fullImage, nil
}

func ComposeDownContainers(ctx context.Context, cfg model.Config, serviceName string) error {
	loc := util.GetFileAndMethod()
	log.Printf("%s: Attempting to stop and remove container:\n                    %s", loc, serviceName)

	cmd := exec.CommandContext(ctx, "docker", "compose", "-f", cfg.ComposeFilePath, "down", serviceName)
	output, err := cmd.CombinedOutput()

	if err != nil {
		log.Printf("%s: Failed to compose down service:\n                    Service: %s\n                    Error: %v\n                    Output: %s",
			loc, serviceName, err, string(output))
		return fmt.Errorf("failed to compose down service %s: %w", serviceName, err)
	}

	log.Printf("%s: Successfully stopped container:\n                    Service: %s\n                    Output: %s",
		loc, serviceName, string(output))
	return nil
}

func ComposeUpContainer(ctx context.Context, cfg model.Config, serviceName string) error {
	loc := util.GetFileAndMethod()
	log.Printf("%s: Attempting to start container:\n                    %s", loc, serviceName)

	cmd := exec.CommandContext(ctx, "docker", "compose", "-f", cfg.ComposeFilePath, "up", serviceName, "-d")
	output, err := cmd.CombinedOutput()

	if err != nil {
		log.Printf("%s: Failed to start service:\n                    Service: %s\n                    Error: %v\n                    Output: %s",
			loc, serviceName, err, string(output))
		return fmt.Errorf("failed to start service %s: %w", serviceName, err)
	}

	log.Printf("%s: Successfully started service:\n                    Service: %s\n                    Output: %s",
		loc, serviceName, string(output))
	return nil
}

func PruneDanglingImages(ctx context.Context, fullImage string) error {
	loc := util.GetFileAndMethod()
	log.Printf("%s: Attempting to prune images:\n                    %s", loc, fullImage)

	cmd := exec.CommandContext(ctx, "docker", "image", "prune", "-f")
	output, err := cmd.CombinedOutput()

	if err != nil {
		log.Printf("%s: Failed to prune images:\n                    Error: %v\n                    Output: %s",
			loc, err, string(output))
		return fmt.Errorf("failed to prune images: %w", err)
	}

	log.Printf("%s: Successfully pruned images:\n                    Output: %s",
		loc, string(output))
	return nil
}
