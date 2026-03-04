# Quick Start Guide

Get the Notification Orchestration Engine running in under 5 minutes.

## Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- curl or Postman for testing

## Step 1: Start Infrastructure

```bash
cd notification-orchestration-engine
docker-compose up -d
```

Wait 30 seconds for all services to be healthy.

## Step 2: Build and Run

```bash
./gradlew clean build
./gradlew bootRun
```

The application starts on port 8083.

## Step 3: Create User Preferences

```bash
curl -X POST http://localhost:8083/api/preferences \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "channelOrder": ["PUSH", "SMS", "EMAIL"],
    "timezone": "Africa/Nairobi",
    "quietHoursEnabled": true,
    "quietHoursStart": 22,
    "quietHoursEnd": 7,
    "pushToken": "fcm_token_abc123",
    "phoneNumber": "+254704373903",
    "email": "user@example.com"
  }'
```

## Step 4: Send a Notification

```bash
curl -X POST http://localhost:8083/api/notifications \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "title": "Payment Received",
    "message": "You have received KES 5,000 from John Doe",
    "priority": "HIGH",
    "metadata": {
      "transactionId": "TXN123456"
    }
  }'
```

Expected response:
```json
{
  "notificationId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "user123",
  "title": "Payment Received",
  "message": "You have received KES 5,000 from John Doe",
  "priority": "HIGH",
  "status": "PENDING",
  "createdAt": "2024-01-15T10:30:00"
}
```

## Step 5: Check Logs

Watch the application logs to see:
- Kafka event consumption
- Channel routing decisions
- Delivery attempts with fallback
- Webhook delivery (if configured)

## Step 6: Explore Swagger UI

Open your browser:
```
http://localhost:8083/swagger-ui.html
```

Test all endpoints interactively.

## Testing Scenarios

### Test Deduplication
Send the same notification twice within 5 minutes:
```bash
# First request - succeeds
curl -X POST http://localhost:8083/api/notifications \
  -H "Content-Type: application/json" \
  -d '{"userId": "user123", "title": "Test", "message": "Duplicate test", "priority": "MEDIUM"}'

# Second request - rejected as duplicate
curl -X POST http://localhost:8083/api/notifications \
  -H "Content-Type: application/json" \
  -d '{"userId": "user123", "title": "Test", "message": "Duplicate test", "priority": "MEDIUM"}'
```

### Test Quiet Hours
Send a LOW priority notification during quiet hours (22:00-07:00 in user's timezone):
```bash
curl -X POST http://localhost:8083/api/notifications \
  -H "Content-Type: application/json" \
  -d '{"userId": "user123", "title": "Update", "message": "Can wait", "priority": "LOW"}'
```

The notification will be skipped. Check logs for confirmation.

### Test Priority Bypass
Send a HIGH priority notification during quiet hours:
```bash
curl -X POST http://localhost:8083/api/notifications \
  -H "Content-Type: application/json" \
  -d '{"userId": "user123", "title": "Alert", "message": "Urgent", "priority": "HIGH"}'
```

This will be delivered despite quiet hours.

## Stopping the Application

```bash
# Stop Spring Boot application
Ctrl+C

# Stop infrastructure
docker-compose down
```

## Troubleshooting

### Port Already in Use
If port 8083 is taken, change it in `application.yml`:
```yaml
server:
  port: 8084
```

### Kafka Connection Issues
Ensure Kafka is healthy:
```bash
docker-compose ps
```

All services should show "healthy" status.

### Database Connection Issues
Check PostgreSQL logs:
```bash
docker-compose logs postgres
```

## Next Steps

- Review the full README.md for architecture details
- Check PROJECT_STATUS.md for implementation status
- Explore the codebase structure
- Integrate real channel providers (FCM, Twilio, SendGrid)
- Set up monitoring with Prometheus and Grafana
