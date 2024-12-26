package util

import (
	"fmt"
	"gopkg.in/gomail.v2"
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
func (s *EmailService) SendEmail(to []string, subject, body string) error {
	m := gomail.NewMessage()
	m.SetHeader("From", s.Username)
	m.SetHeader("To", to...)
	m.SetHeader("Subject", subject)
	m.SetBody("text/plain", body)

	d := gomail.NewDialer(s.SMTPHost, s.SMTPPort, s.Username, s.Password)

	// Send the email
	if err := d.DialAndSend(m); err != nil {
		return fmt.Errorf("failed to send email: %w", err)
	}
	return nil
}
