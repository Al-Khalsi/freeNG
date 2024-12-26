package util

import (
	"fmt"
	"gopkg.in/gomail.v2"
	"log"
	"time"
)

// EmailService is responsible for sending emails.
type EmailService struct {
	Username string
	Password string
	SMTPHost string
	SMTPPort int
}

// NewEmailService creates a new instance of EmailService.
func NewEmailService(username, password, smtpHost string, smtpPort int) *EmailService {
	return &EmailService{
		Username: username,
		Password: password,
		SMTPHost: smtpHost,
		SMTPPort: smtpPort,
	}
}

// SendEmail sends an email to the specified recipients.
func (emailService *EmailService) SendEmail(to []string, subject, body string, isHTML bool) error {
	m := gomail.NewMessage()
	m.SetHeader("From", emailService.Username)
	m.SetHeader("To", to...)
	m.SetHeader("Subject", subject)

	if isHTML {
		m.SetBody("text/html", body) // Set body as HTML
	} else {
		m.SetBody("text/plain", body) // Set body as plain text
	}

	dialer := gomail.NewDialer(emailService.SMTPHost, emailService.SMTPPort, emailService.Username, emailService.Password)

	// Send the email
	if err := dialer.DialAndSend(m); err != nil {
		return fmt.Errorf("failed to send email: %w", err)
	}
	return nil
}

// SendErrorEmail sends an error email notification.
func (emailService *EmailService) SendErrorEmail(recipients []string, serviceName, output, image, tag, err string) {
	loc := GetFileAndMethod()

	subject := fmt.Sprintf("🚨 Deployment Failed: %s", serviceName)
	body := fmt.Sprintf(`
	<!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
        <title>Deployment Error Notification</title>
        <style>
            body {
                font-family: 'Arial', sans-serif;
                background-color: #141b22;
                margin: 0;
                padding: 0;
                color: #eee;
            }
            .container {
                width: 90%%;
                max-width: 600px;
                margin: 20px auto;
                background-color: #141b22;
                border-radius: 8px;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                overflow: hidden;
            }
            .banner {
                background-color: #7a5af8;
                color: #fff;
                padding: 15px;
                text-align: center;
                font-size: 1.2em;
                font-weight: bold;
            }
            .content {
                padding: 20px;
            }
            .section {
                margin-bottom: 15px;
            }
            .section h3 {
                margin: 0 0 5px 0;
                color: hsla(286, 100%%, 72%%, 0.8);
            }
            .section p {
                margin: 0;
                background: #1E2835;
                padding: 10px;
                border-radius: 4px;
                color: #798DA3;
                white-space: pre-wrap;
            }
            footer {
                margin-top: 20px;
                text-align: center;
                font-size: 0.85em;
                color: #798DA3;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="banner">\u26a0 Deployment Failed</div>
            <div class="content">
                <div class="section">
                    <h3>Service:</h3>
                    <p>%s</p>
                </div>
                <div class="section">
                    <h3>Error:</h3>
                    <p>%s</p>
                </div>
                <div class="section">
                    <h3>Docker Output:</h3>
                    <p>%s</p>
                </div>
                <div class="section">
                    <h3>Image:</h3>
                    <p>%s</p>
                </div>
                <div class="section">
                    <h3>Tag:</h3>
                    <p>%s</p>
                </div>
                <footer>Thank you for your attention.</footer>
            </div>
        </div>
    </body>
    </html>
	`, serviceName, err, output, image, tag)

	if err := emailService.SendEmail(recipients, subject, body, true); err != nil {
		log.Printf("%s: ERROR: Failed to send error email notification: %v", loc, err)
	}
}

// SendSuccessEmail sends a success email notification.
func (emailService *EmailService) SendSuccessEmail(recipients []string, serviceName, image, tag string) {
	loc := GetFileAndMethod()

	subject := fmt.Sprintf("✅ Deployment Successful: %s", serviceName)
	body := fmt.Sprintf(`
	<!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
        <title>Deployment Success Notification</title>
        <style>
            body {
                font-family: 'Arial', sans-serif;
                background-color: #05071b;
                margin: 0;
                padding: 0;
                color: #eee;
            }
            .container {
                width: 90%%;
                max-width: 600px;
                margin: 20px auto;
                background-color: #141b22;
                border-radius: 8px;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                overflow: hidden;
            }
            .banner {
                background-color: #a5eec6;
                color: #000;
                padding: 15px;
                text-align: center;
                font-size: 1.2em;
                font-weight: bold;
            }
            .content {
                padding: 20px;
            }
            .section {
                margin-bottom: 15px;
            }
            .section h3 {
                margin: 0 0 5px 0;
                color: #7a5af8;
            }
            .section p {
                margin: 0;
                background: #eee;
                padding: 10px;
                border-radius: 4px;
                color: #DF71FFCC;
                white-space: pre-wrap;
            }
            footer {
                margin-top: 20px;
                text-align: center;
                font-size: 0.85em;
                color: #798DA3;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="banner">\u2705 Deployment Successful</div>
            <div class="content">
                <div class="section">
                    <h3>Service:</h3>
                    <p>%s</p>
                </div>
                <div class="section">
                    <h3>Image:</h3>
                    <p>%s</p>
                </div>
                <div class="section">
                    <h3>Tag:</h3>
                    <p>%s</p>
                </div>
                <div class="section">
                    <h3>Deployment Time:</h3>
                    <p>%s</p>
                </div>
                <footer>Thank you for your attention.</footer>
            </div>
        </div>
    </body>
    </html>
	`, serviceName, image, tag, time.Now().Format(time.RFC1123))

	if err := emailService.SendEmail(recipients, subject, body, true); err != nil {
		log.Printf("%s: ERROR: Failed to send success email notification: %v", loc, err)
	}
}
