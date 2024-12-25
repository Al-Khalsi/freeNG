package main

import (
	"fmt"
	"log"
	"net/http"
	"os/exec"
)

func deployService(serviceName, image, tag string) error {
	if tag == "" {
		tag = "latest"
	}
	fullImage := fmt.Sprintf("%s:%s", image, tag)

	// Login to docker for private repositories
	log.Println("Attempting to log in to dockerhub.")
	cmd := exec.Command("docker", "login", "-u", "dukeofjava", "-p", "dckr_pat_BtLuwm5qumhUSPtatv0q4WsxBi0")
	if output, err := cmd.CombinedOutput(); err != nil {
		return fmt.Errorf("Failed to login to docker: %s, output: %s\n", err, output)
	}
	log.Println("Successfully logged in to docker")

	// Pull the specified tag
	log.Printf("Attempting to pull image: %s\n", fullImage)
	cmd = exec.Command("docker", "pull", fullImage)
	if _, err := cmd.CombinedOutput(); err != nil {
		log.Printf("Tag %s not found for image %s, falling back to 'latest'\n", tag, image)
		fullImage = fmt.Sprintf("%s:latest", image)
		cmd = exec.Command("docker", "pull", fullImage)
		if output, err := cmd.CombinedOutput(); err != nil {
			return fmt.Errorf("error pulling fallback 'latest' tag: %s, output: %s\n", err, output)
		}
	}
	log.Printf("Successfully pulled image: %s\n", fullImage)

	// Stop and Remove old container
	log.Printf("Attempting to stop and remove container: %s\n", serviceName)
	cmd = exec.Command("docker-compose", "-f", "/home/simi/DockGE/pixel/docker-compose.yaml", "down", serviceName)
	if err := cmd.Run(); err != nil {
		return fmt.Errorf("Failed to stop container %s: %s\n", serviceName, err)
	}
	log.Printf("Successfully stopped container: %s\n", serviceName)

	// Start service with docker-compose
	log.Printf("Attempting to start container: %s\n", serviceName)
	cmd = exec.Command("docker", "compose", "-f", "/home/simi/DockGE/pixel/docker-compose.yaml", "up", serviceName, "-d")
	if output, err := cmd.CombinedOutput(); err != nil {
		return fmt.Errorf("error starting service %s: %s, output: %s\n", serviceName, err, output)
	}
	log.Printf("Successfully started service: %s\n", serviceName)

	// Prune dangling images
	log.Printf("Attempting to purne images: %s\n", fullImage)
	cmd = exec.Command("docker", "image", "prune", "-f")
	if output, err := cmd.CombinedOutput(); err != nil {
		log.Printf("Warning: error pruning images: %s, output: %s\n", err, output)
	}
	log.Printf("Successfully pruned images: %s\n", fullImage)

	return nil
}

func webhookHandler(w http.ResponseWriter, r *http.Request) {
	service := r.URL.Query().Get("service")
	image := r.URL.Query().Get("image")
	tag := r.URL.Query().Get("tag")
	log.Printf("Webhook triggered from docker hub for service %s: %s:%s", service, image, tag)

	if service == "" || image == "" {
		http.Error(w, "Missing service or image parameter", http.StatusBadRequest)
		return
	}

	log.Printf("Deploying service: %s...\n", service)
	if err := deployService(service, image, tag); err != nil {
		log.Printf("Error deploying service %s with image %s:%s: %v\n", service, image, tag, err)
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	log.Printf("Successfully deployed service %s with image %s:%s\n", service, image, tag)

	w.WriteHeader(http.StatusOK)
	_, _ = fmt.Fprintf(w, "Service %s deployed with image %s:%s\n", service, image, tag)
}

func main() {
	http.HandleFunc("/webhook", webhookHandler)
	http.HandleFunc("/", func(writer http.ResponseWriter, request *http.Request) {
		log.Println("Home URL triggered.")
		writer.Header().Set("Content-Type", "text/html")
		_, _ = fmt.Fprintf(writer, "Hello! Go webhook-server home URL triggered!")
	})

	log.Println("Go webhook server started successfully.")
	log.Println("Listening on :5002...")
	log.Fatal(http.ListenAndServe(":5002", nil))
}
