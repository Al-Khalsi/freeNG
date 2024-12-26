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

		log.Printf("%s: INFO: Webhook triggered.\n\tService: %s\n\tImage: %s:%s", loc, service, image, tag)

		if service == "" || image == "" {
			http.Error(w, "Missing service or image parameter", http.StatusBadRequest)
			return
		}

		ctx := r.Context()
		log.Printf("%s: INFO: Deploying service '%s'", loc, service)

		// Create EmailService
		emailService := util.NewEmailService(
			"duke.of.java.spring@gmail.com",
			cfg.GoogleAccountApplicationPassword,
			"smtp.gmail.com",
			587,
		)

		if err := deploy.DeployService(ctx, cfg, service, image, tag, emailService); err != nil {
			log.Printf("%s: ERROR: Deployment failed.\n\tService: %s\n\tImage: %s:%s\n\tError: %v", loc, service, image, tag, err)
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		log.Printf("%s: INFO: Deployment successful.\n\tService: %s\n\tImage: %s:%s", loc, service, image, tag)
		w.WriteHeader(http.StatusOK)
		_, _ = fmt.Fprintf(w, "Service %s deployed with image %s:%s\n", service, image, tag)
	}
}
