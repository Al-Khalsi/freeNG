package handler

import (
	"fmt"
	"github.com/seyedali-dev/cd/pkg/deploy"
	"github.com/seyedali-dev/cd/pkg/model"
	"github.com/seyedali-dev/cd/pkg/util"
	"log"
	"net/http"
)

func HealthCheckHandler(w http.ResponseWriter, r *http.Request) {
	w.WriteHeader(http.StatusOK)
	_, _ = fmt.Fprint(w, "OK")
}

func WebhookHandler(cfg model.Config) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		loc := util.GetFileAndMethod()
		service := r.URL.Query().Get("service")
		image := r.URL.Query().Get("image")
		tag := r.URL.Query().Get("tag")

		log.Printf("%s: Webhook triggered from docker hub:\n                    Service: %s\n                    Image: %s:%s",
			loc, service, image, tag)

		if service == "" || image == "" {
			http.Error(w, "Missing service or image parameter", http.StatusBadRequest)
			return
		}

		ctx := r.Context()
		log.Printf("%s: Deploying service:\n                    %s", loc, service)

		if err := deploy.DeployService(ctx, cfg, service, image, tag); err != nil {
			log.Printf("%s: Error deploying service:\n                    Service: %s\n                    Image: %s:%s\n                    Error: %v",
				loc, service, image, tag, err)
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		log.Printf("%s: Successfully deployed service:\n                    Service: %s\n                    Image: %s:%s",
			loc, service, image, tag)
		w.WriteHeader(http.StatusOK)
		_, _ = fmt.Fprintf(w, "Service %s deployed with image %s:%s\n", service, image, tag)
	}

}
