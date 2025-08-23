# Time Capsule API 📮

A secure REST API that allows users to create time capsule messages (text or files) to be automatically sent at a future date via email. Perfect for sending messages to your future self, loved ones, or for any delayed communication needs.
<div align="center">
     <div>
         <img src="https://img.shields.io/badge/-SpringBoot-black?style=for-the-badge&logoColor=white&logo=springboot&color=47A248" alt="spring boot" />
         <img src="https://img.shields.io/badge/-Spring_AI-black?style=for-the-badge&logo=spring&logoColor=white&color=6DB33F" alt="spring ai" />
         <img src="https://img.shields.io/badge/-Angular-black?style=for-the-badge&logo=angular&logoColor=white&color=DD0031" alt="angular" />
      </div>
</div>


## 🌟 Features

### Core Functionality
- **Secure Authentication**: JWT and OAuth2 implementation with Spring Security
- **Message Management**: Full CRUD operations for time capsule messages
- **Future Scheduling**: Set precise delivery dates for your messages
- **Automatic Delivery**: Reliable background scheduler using Quartz
- **Email Integration**: SMTP support (Gmail, SendGrid, or custom providers)
- **File Attachments**: Support for file uploads with local
- **Audit Trail**: Complete logging and tracking of message delivery status

### AI Enhancement (Optional)
- **Message Generation**: AI-powered message creation from prompts
- **Content Improvement**: Enhance manually written messages
- **Smart Suggestions**: Generate inspiring, well-crafted future messages

### Delivery Channels
- **Primary**: Email delivery via SMTP
- **Fallback**: Console/log simulation when SMTP unavailable
- **Future Extensions**: SMS (Twilio), WhatsApp, Slack integration ready

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL or MySQL
- SMTP server access (Gmail, SendGrid, etc.)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/time-capsule-api.git
   cd time-capsule-api
   ```

2. **Configure application properties**
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```
   
3. **Build and run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Access the API**
   - API Base URL: `http://localhost:8080/api/v1`
   - Swagger Documentation: `http://localhost:8080/swagger-ui.html`

## 📚 API Documentation

### Authentication Endpoints
```
POST /api/v1/auth/register - Register new user
POST /api/v1/auth/login    - Login user
POST /api/v1/auth/refresh  - Refresh JWT token
```

### Message Management
```
GET    /api/v1/messages          - List user's messages
POST   /api/v1/messages          - Create new time capsule
GET    /api/v1/messages/{id}     - Get message details
PUT    /api/v1/messages/{id}     - Update message
DELETE /api/v1/messages/{id}     - Delete message
```

### AI Enhancement (Optional)
```
POST /api/v1/ai/generate-message - Generate message from prompt
POST /api/v1/ai/improve-message  - Improve existing message
```

### Example Request: Create Time Capsule
```json
{
  "title": "Letter to my future self",
  "content": "Dear future me, I hope you remember...",
  "recipientEmail": "future-me@example.com",
  "deliveryDate": "2025-12-25T10:00:00Z",
  "attachments": ["file1.jpg", "document.pdf"]
}
```

## 🛠️ Technology Stack

- **Backend**: Java 17+, Spring Boot 3+
- **Security**: Spring Security, JWT, OAuth2
- **Database**: PostgreSQL/MySQL with JPA/Hibernate
- **Scheduling**: Spring Scheduler or Quartz
- **Documentation**: Swagger/OpenAPI
- **File Storage**: Local filesystem or AWS S3
- **Message Queue**: RabbitMQ/Kafka (optional)
- **AI Integration**: OpenAI, Mistral, or HuggingFace APIs

## 🏗️ Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Client Apps   │───▶│   REST API       │───▶│   Database      │
│                 │    │   (Spring Boot)  │    │   (PostgreSQL)  │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                               │
                               ▼
                       ┌──────────────────┐
                       │   Scheduler      │
                       │   (Background)   │
                       └─────────┬────────┘
                                 ▼
                       ┌──────────────────┐
                       │   Email Service  │
                       │   (SMTP)         │
                       └──────────────────┘
```

## 🔧 Configuration

### Environment Variables
```bash
# Database
DB_URL=jdbc:postgresql://localhost:5432/timecapsule
DB_USERNAME=your_username
DB_PASSWORD=your_password

# JWT
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=86400000

# Email
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email
MAIL_PASSWORD=your_password

# AI Integration (Optional)
OPENAI_API_KEY=your_openai_key
AI_MODEL=gpt-3.5-turbo
```

### Scheduler Configuration
The application supports both Spring's built-in scheduler and Quartz for more complex scheduling needs:

```java
@Scheduled(fixedRate = 60000) // Check every minute
public void processPendingMessages() {
    // Implementation
}
```

## 📝 Logging and Monitoring

The application provides comprehensive logging for:
- Message creation and updates
- Delivery attempts and results
- Authentication events
- System errors and performance metrics

Logs are structured in JSON format for easy parsing and monitoring integration.

## 🚀 Deployment

### Docker
```bash
docker build -t time-capsule-api .
docker run -p 8080:8080 time-capsule-api
```

### Production Considerations
- Use environment-specific configuration profiles
- Set up proper SSL/TLS certificates
- Configure proper CORS policies
- Implement rate limiting
- Set up monitoring and alerting
- Use a production-grade database setup
- Configure file storage with proper backup
