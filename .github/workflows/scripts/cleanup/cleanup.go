package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
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
// 	username := "dukeofjava"
    password := os.Getenv("DOCKER_PASSWORD")
//	password := "2devsimi20"
    repo := os.Getenv("IMAGE_NAME")
//	repo := "dukeofjava/pixelfreebies-service"

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

	// Keep the latest 3 tags, delete the rest
	if len(devTags) > 3 {
		tagsToDelete := devTags[3:]
		for _, tag := range tagsToDelete {
			deleteURL := fmt.Sprintf("https://hub.docker.com/v2/repositories/%s/tags/%s/", repo, tag)
			deleteReq, _ := http.NewRequest("DELETE", deleteURL, nil)
			deleteReq.Header.Set("Authorization", fmt.Sprintf("Bearer %s", token))

			deleteResp, err := client.Do(deleteReq)
			if err != nil || deleteResp.StatusCode != http.StatusNoContent {
				log.Printf("Failed to delete tag %s: %v", tag, err)
			} else {
				log.Printf("Deleted tag: %s", tag)
			}
		}
	} else {
		fmt.Println("No tags to delete")
	}
}
