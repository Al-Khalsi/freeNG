package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"os"
	"sort"
	"strings"
)

type DockerHubResponse struct {
	Results []struct {
		Name string `json:"name"`
	} `json:"results"`
}

func main() {
	username := os.Getenv("DOCKER_USERNAME")
	password := os.Getenv("DOCKER_PASSWORD")
	repo := os.Getenv("IMAGE_NAME")

	if username == "" || password == "" || repo == "" {
		log.Fatal("Missing required environment variables: DOCKER_USERNAME, DOCKER_PASSWORD, IMAGE_NAME")
	}

	// Authenticate and get a token
	authURL := "https://hub.docker.com/v2/users/login/"
	authPayload := fmt.Sprintf(`{"username": "%s", "password": "%s"}`, username, password)
	authResp, err := http.Post(authURL, "application/json", bytes.NewBuffer([]byte(authPayload)))
	if err != nil || authResp.StatusCode != http.StatusOK {
		log.Fatalf("Failed to authenticate: %v", err)
	}
	defer authResp.Body.Close()

	var authData map[string]interface{}
	json.NewDecoder(authResp.Body).Decode(&authData)
	token := authData["token"].(string)

	// Fetch all tags
	tagsURL := fmt.Sprintf("https://hub.docker.com/v2/repositories/%s/tags/", repo)
	req, _ := http.NewRequest("GET", tagsURL, nil)
	req.Header.Set("Authorization", fmt.Sprintf("Bearer %s", token))

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil || resp.StatusCode != http.StatusOK {
		log.Fatalf("Failed to fetch tags: %v", err)
	}
	defer resp.Body.Close()

	body, _ := ioutil.ReadAll(resp.Body)
	var dockerHubResponse DockerHubResponse
	_ = json.Unmarshal(body, &dockerHubResponse)

	// Filter and sort dev- tags
	var devTags []string
	for _, result := range dockerHubResponse.Results {
		if strings.HasPrefix(result.Name, "0.1-BETA-dev-") {
			devTags = append(devTags, result.Name)
		}
	}
	sort.Sort(sort.Reverse(sort.StringSlice(devTags)))

	// Logging purpose: Print the SHA values of the tags
	fmt.Printf("Found tags length: %d\n", len(devTags))
	var shaValues []string
	for _, tag := range devTags {
		shaValue := strings.TrimPrefix(tag, "0.1-BETA-dev-") // Remove the prefix
		shaValues = append(shaValues, shaValue)              // Collect the SHA values
	}
	fmt.Printf("SHA values of tags: %v\n", shaValues)

	// Keep the latest 3 tags, delete the rest
	if len(devTags) > 3 {
		tagsToDelete := devTags[3:]
		for _, tag := range tagsToDelete {
			deleteURL := fmt.Sprintf("https://hub.docker.com/v2/repositories/%s/tags/%s/", repo, tag)
			deleteReq, _ := http.NewRequest("DELETE", deleteURL, nil)
			deleteReq.Header.Set("Authorization", fmt.Sprintf("Bearer %s", token))

			deleteResp, err := client.Do(deleteReq)
			if err != nil || deleteResp.StatusCode != http.StatusNoContent {
				fmt.Printf("Failed to delete tag %s: %v", tag, err)
			} else {
			    shaValue := strings.TrimPrefix(tag, "0.1-BETA-dev-")
				fmt.Printf("Deleted tag SHA value: %s\n", shaValue)
			}
		}
	}
}
