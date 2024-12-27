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
		<title>Deployment Success Notification</title>
		<style>
			body {
				font-family: 'Arial', sans-serif;
				background-color: #05071b; /* --bg-darkBlue */
				margin: 0;
				padding: 20px;
				color: #eee;
			}
			.container {
				width: 90%%;
				max-width: 600px;
				margin: 0 auto;
				background-color: #141b22; /* --bg-darkGray2 */
				border-radius: 8px;
				box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1), 0 0 50px rgba(122, 90, 248, 0.2);
				position: relative;
				overflow: hidden;
			}
			.content {
				padding: 20px;
			}
			.banner {
				background-color: hsla(286, 100%%, 72%%, 0.8); /* --bg-lightPurple for success */
				color: #000; /* --cl-black */
				padding: 15px;
				text-align: center;
				font-size: 1.2em;
				font-weight: bold;
				position: relative;
			}
			.section {
				margin-bottom: 20px;
			}
			.section h3 {
				margin: 0 0 8px 0;
				color: #7a5af8; /* --bg-purple for success template */
				font-size: 1.1em;
			}
			.section p {
				margin: 0;
				background: #1E2835; /* --bg-darkGray */
				padding: 12px 16px;
				border-radius: 4px;
				color: #798DA3; /* --cl-lightBlue */
				white-space: pre-wrap;
				font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
				font-size: 0.9em;
				border: 1px solid rgba(255, 255, 255, 0.13); /* --cl-gray-m1 */
				overflow-x: auto;
			}
			footer {
				margin-top: 20px;
				text-align: center;
				font-size: 0.85em;
				color: #798DA3; /* --cl-lightBlue */
			}
			.star {
				position: absolute;
				color: rgba(255, 255, 255, 0.13); /* --cl-gray-m1 */
				pointer-events: none;
				z-index: 1;
				animation: twinkle 2s infinite; /* Apply animation */
			}
			@keyframes twinkle {
				0%% { opacity: 0; }
				50%% { opacity: 1; }
				100%% { opacity: 0; }
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
		<script>
			function createStars() {
				const container = document.querySelector('.container');
				const starCount = 30;
				const stars = ['✧', '✦', '⋆', '✫', '✯', '★', '✳', '✴', '✵'];
				
				for (let i = 0; i < starCount; i++) {
					const star = document.createElement('span');
					star.className = 'star';
					star.textContent = stars[Math.floor(Math.random() * stars.length)];
					star.style.left = Math.random() * 100 + '%%';
					star.style.top = Math.random() * 100 + '%%';
					star.style.animationDelay = Math.random() * 3 + 's';
					star.style.animationDuration = (2 + Math.random() * 3) + 's';
					star.style.fontSize = (8 + Math.random() * 8) + 'px';
					container.appendChild(star);
				}
			}
			window.addEventListener('load', createStars);
		</script>
	</body>
	</html>
	`, serviceName, err, output, image, tag)

	if err := emailService.SendEmail(recipients, subject, body, true); err != nil {
		log.Printf("%s: ERROR: Failed to send error email notification: %v", loc, err)
		return
	}
	log.Printf("%s: INFO: Successfully sent failure email notification to recipients: {%s}", loc, recipients)
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
				background-color: #05071b; /* --bg-darkBlue */
				margin: 0;
				padding: 20px;
				color: #eee;
			}
			.container {
				width: 90%%;
				max-width: 600px;
				margin: 0 auto;
				background-color: #141b22; /* --bg-darkGray2 */
				border-radius: 8px;
				box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1), 0 0 50px rgba(122, 90, 248, 0.2);
				position: relative;
				overflow: hidden;
			}
			.content {
				padding: 20px;
			}
			.banner {
				background-color: #a5eec6;
				color: #000; /* --cl-black */
				padding: 15px;
				text-align: center;
				font-size: 1.2em;
				font-weight: bold;
				position: relative;
			}
			.section {
				margin-bottom: 20px;
			}
			.section h3 {
				margin: 0 0 8px 0;
				color: #7a5af8; /* --bg-purple for success template */
				font-size: 1.1em;
			}
			.section p {
				margin: 0;
				background: #1E2835; /* --bg-darkGray */
				padding: 12px 16px;
				border-radius: 4px;
				color: #798DA3; /* --cl-lightBlue */
				white-space: pre-wrap;
				font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
				font-size: 0.9em;
				border: 1px solid rgba(255, 255, 255, 0.13); /* --cl-gray-m1 */
				overflow-x: auto;
			}
			footer {
				margin-top: 20px;
				text-align: center;
				font-size: 0.85em;
				color: #798DA3; /* --cl-lightBlue */
			}
			.star {
				position: absolute;
				color: rgba(255, 255, 255, 0.13); /* --cl-gray-m1 */
				pointer-events: none;
				z-index: 1;
				animation: twinkle 2s infinite; /* Apply animation */
			}
			@keyframes twinkle {
				0%% { opacity: 0; }
				50%% { opacity: 1; }
				100%% { opacity: 0; }
			}
		</style>
	</head>
	<body>
		<div class="container">
			<div class="banner">\u26a0 Deployment Success</div>
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
		<script>
			function createStars() {
				const container = document.querySelector('.container');
				const starCount = 30;
				const stars = ['✧', '✦', '⋆', '✫', '✯', '★', '✳', '✴', '✵'];
				
				for (let i = 0; i < starCount; i++) {
					const star = document.createElement('span');
					star.className = 'star';
					star.textContent = stars[Math.floor(Math.random() * stars.length)];
					star.style.left = Math.random() * 100 + '%%';
					star.style.top = Math.random() * 100 + '%%';
					star.style.animationDelay = Math.random() * 3 + 's';
					star.style.animationDuration = (2 + Math.random() * 3) + 's';
					star.style.fontSize = (8 + Math.random() * 8) + 'px';
					container.appendChild(star);
				}
			}
			window.addEventListener('load', createStars);
		</script>
	</body>
	</html>
	`, serviceName, image, tag, time.Now().Format(time.RFC1123))

	if err := emailService.SendEmail(recipients, subject, body, true); err != nil {
		log.Printf("%s: ERROR: Failed to send success email notification: %v", loc, err)
		return
	}
	log.Printf("%s: INFO: Successfully sent success email notification to recipients: {%s}", loc, recipients)
}
