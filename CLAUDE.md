# Nabu - Project Guide for Claude

## Project Overview

**Nabu** is an operational status application developed by Entur (Norway's public transportation authority) that tracks and monitors timetable data delivery processes. It provides real-time status updates and notifications about data processing jobs in the public transportation data pipeline.

### Purpose
- Monitor timetable data delivery status
- Track job execution events across the data processing pipeline
- Provide notifications to users about data processing outcomes
- Maintain operational visibility for public transport data management

## Technology Stack

### Core Technologies
- **Language**: Java 21
- **Framework**: Spring Boot 3.x
- **Web Layer**: Jersey (JAX-RS) for REST APIs
- **Database**: PostgreSQL with PostGIS spatial extensions
- **ORM**: Hibernate/JPA with Spatial support
- **Build Tool**: Maven
- **Container**: Docker (Alpine-based with Liberica OpenJRE)

### Key Dependencies
- **Spring Boot Starters**: Web, Data JPA, Jersey, Mail, Cache, Quartz, Security, Actuator
- **Google Cloud**: Spring Cloud GCP (Pub/Sub integration)
- **Database**: PostgreSQL driver, PostGIS JDBC, Flyway migrations
- **Security**: OAuth2 with Entur helpers
- **API Documentation**: OpenAPI 3.0 (Swagger)
- **Monitoring**: Micrometer with Prometheus registry
- **Testing**: JUnit Jupiter, Testcontainers, H2 (in-memory)

## Project Structure

```
nabu/
├── src/main/java/no/rutebanken/nabu/
│   ├── App.java                          # Spring Boot application entry point
│   ├── config/                           # Configuration classes
│   │   ├── JacksonConfig.java
│   │   ├── OAuth2Config.java
│   │   ├── AuthorizationConfig.java
│   │   └── NotificationProcessorConfiguration.java
│   ├── domain/                           # Domain models
│   │   ├── event/                        # Event domain objects
│   │   │   ├── CrudEvent.java
│   │   │   ├── JobEvent.java
│   │   │   ├── Notification.java
│   │   │   └── TimeTableAction.java
│   │   └── SystemJobStatus.java
│   ├── event/                            # Event handling
│   │   ├── EventHandler.java
│   │   ├── EventService.java
│   │   ├── NotificationProcessor.java
│   │   ├── ImmediateNotificationService.java
│   │   ├── ScheduledNotificationService.java
│   │   ├── SystemJobStatusUpdater.java
│   │   ├── filter/                       # Event filtering
│   │   ├── support/                      # Event support utilities
│   │   └── user/                         # User-related events
│   ├── provider/                         # Data provider management
│   │   ├── ProviderResource.java
│   │   ├── ProviderRepository.java
│   │   └── model/
│   ├── rest/                             # REST API endpoints
│   │   ├── external/                     # Public APIs
│   │   │   ├── TimetableDataDeliveryStatusResource.java
│   │   │   └── ExternalOpenApiResource.java
│   │   ├── internal/                     # Internal APIs
│   │   │   ├── AdminSummaryResource.java
│   │   │   ├── ChangeLogResource.java
│   │   │   ├── LatestUploadResource.java
│   │   │   ├── NotificationResource.java
│   │   │   └── TimeTableJobEventResource.java
│   │   ├── domain/                       # REST domain objects
│   │   ├── mapper/                       # DTO mappers
│   │   └── exception/                    # Exception handlers
│   ├── security/                         # Security configuration
│   │   └── oauth2/
│   ├── filter/                           # Request/response filters
│   ├── repository/                       # Data access layer
│   └── exceptions/                       # Custom exceptions
├── src/main/resources/
│   ├── db/migration/                     # Flyway database migrations
│   ├── openapi/                          # OpenAPI specifications
│   │   └── openapi-events-external.json
│   ├── templates/                        # Freemarker email templates
│   ├── application.properties
│   ├── logback.xml
│   └── messages*.properties              # i18n messages
├── src/test/java/                        # Test sources (16 test files)
├── api/events/apiproxy/                  # API proxy configuration
├── helm/nabu/                            # Kubernetes Helm charts
├── terraform/                            # Infrastructure as Code
│   ├── main.tf
│   ├── variables.tf
│   └── env/
├── .github/workflows/
│   └── push.yml                          # CI/CD pipeline
├── Dockerfile                            # Multi-stage Docker build
├── pom.xml                               # Maven configuration
└── dependencycheck-suppression.xml       # OWASP dependency check config
```

## Key Components

### 1. Event Processing System
The core of Nabu is its event processing system that handles various types of events:
- **JobEvent**: Tracks job execution states (queued, in progress, completed, failed)
- **CrudEvent**: Monitors create/read/update/delete operations
- **Notification**: Manages user notifications about job outcomes
- **TimeTableAction**: Specific actions related to timetable processing
- **GeoCoderAction**: Geocoding-related events

### 2. REST API Endpoints

#### External APIs (Public)
- **GET** `/timetable-events/status/{codespace}/{correlationId}` - Get data delivery status
  - Returns: `DataDeliveryStatus` with state (IN_PROGRESS, FAILED, OK), date, fileName

#### Internal APIs (Administrative)
- Admin summary and statistics
- Change log tracking
- Latest upload information
- Notification management
- Timetable job event queries

### 3. Provider Management
Manages data providers (organizations) that submit timetable data:
- Provider configuration and metadata
- Chouette integration information
- Provider-specific settings and permissions

### 4. Notification System
- **Immediate notifications**: Real-time alerts via email
- **Scheduled notifications**: Batched notification delivery
- Freemarker templates for email content
- Configurable notification rules and recipients

### 5. Security & Authorization
- OAuth2 authentication using Entur's OAuth2 helpers
- Role-based access control
- Integration with Entur's permission store
- Secure REST endpoints with JWT token validation

### 6. Database Schema
- PostgreSQL with PostGIS for spatial data support
- Flyway migrations for schema versioning
- Entities for:
  - System job status tracking
  - Event logs and history
  - Provider configurations
  - User notifications
  - Administrative zones (geospatial)

## Building and Running

### Development Setup
```bash
# Build the project
mvn clean install

# Run with in-memory H2 database
mvn spring-boot:run -Ph2 -Dspring.config.location=/path/to/application.properties

# Run tests
mvn test
```

### Docker Build
```bash
# Build Docker image (with H2 profile)
mvn -Pf8-build,h2

# Run in Docker (development)
docker run -it --name nabu \
  -e JAVA_OPTIONS="-Xmx1280m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005" \
  -p 5005:5005 \
  -v /path/to/application.properties:/app/config/application.properties:ro \
  nabu:latest
```

### Configuration
Example `application.properties` for development:
```properties
server.port=9004
spring.jpa.show-sql=false
spring.jpa.hibernate.ddl-auto=create
logging.level.no.rutebanken=INFO
```

For PostgreSQL:
```properties
spring.jpa.database=POSTGRESQL
spring.datasource.url=jdbc:postgresql://localhost:5432/nabu
spring.datasource.username=nabu
spring.datasource.password=<password>
spring.jpa.hibernate.ddl-auto=update
```

## Database Management

### Flyway Migrations
```bash
# Execute migration
./flyway -url=jdbc:postgresql://localhost:5432/nabu \
  -locations=filesystem:/path/to/nabu/src/main/resources/db/migration \
  migrate

# Baseline existing database
./flyway -url=jdbc:postgresql://localhost:5432/nabu \
  -locations=filesystem:/path/to/nabu/src/main/resources/db/migration \
  baseline
```

### Schema Changes
- Create new migration files in `src/main/resources/db/migration/`
- Follow Flyway naming convention: `V{version}__{description}.sql`
- Commit migrations together with related code changes

## CI/CD Pipeline

### GitHub Actions Workflow
The project uses GitHub Actions (`.github/workflows/push.yml`) with the following stages:

1. **maven-verify**: 
   - Builds and tests the application
   - Runs on Ubuntu 24.04 with Java 21
   - Caches Maven dependencies
   - Uploads build artifacts
   - Performs SonarCloud code analysis

2. **Openapi-lint**:
   - Validates OpenAPI specification
   - Uploads spec to bucket on master branch

3. **docker-build** (master only):
   - Builds Docker image using Entur's reusable workflow

4. **docker-push** (master only):
   - Pushes Docker image to registry

## Monitoring and Health Checks

### Endpoints
- **Liveness**: `http://localhost:9004/jersey/appstatus/up`
- **Readiness**: `http://localhost:9004/jersey/appstatus/ready`
- **Metrics**: Exposed via Spring Actuator for Prometheus scraping

### Observability
- Structured logging with Logstash encoder
- Metrics exported to Prometheus
- Integration with Entur's monitoring infrastructure

## Integration Points

### Google Cloud Pub/Sub
- Event publishing and subscription
- Asynchronous job notifications
- Integration with Entur's data pipeline

### External Systems
- Chouette (timetable data processing)
- Permission store (authorization)
- Email service (notifications)
- Organization registry

## Testing Strategy

- **Unit Tests**: 16 test files using JUnit Jupiter
- **Integration Tests**: Testcontainers for PostgreSQL and GCP emulation
- **In-Memory Testing**: H2 database for fast test execution
- **Test Coverage**: Monitored via Jacoco and reported to SonarCloud

## API Usage Example

### Upload Timetable File
```bash
curl -vX POST \
  -F "file=@avinor-netex_201609291122.zip" \
  http://localhost:9004/jersey/files/21
```

### Check Delivery Status
```bash
curl http://localhost:9004/jersey/timetable-events/status/{codespace}/{correlationId}
```

Response:
```json
{
  "state": "IN_PROGRESS",
  "date": "2024-01-15T10:30:00Z",
  "fileName": "avinor-netex_201609291122.zip"
}
```

## Development Guidelines

### Code Organization
- Follow package-by-feature structure
- Keep REST resources focused on HTTP concerns
- Business logic in service classes
- Use repositories for data access
- Event handlers for asynchronous processing

### API Design
- OpenAPI-first approach
- Generate code from OpenAPI specifications using `openapi-generator-maven-plugin`
- Maintain backward compatibility
- Version APIs appropriately

### Security Considerations
- Never commit secrets or credentials
- Use environment variables for sensitive configuration
- Validate all input data
- Follow OAuth2 best practices
- Implement proper authorization checks

### Code Quality
- SonarCloud analysis on every push
- OWASP dependency check
- Follow existing code style
- Write tests for new features
- Document complex business logic

## License

Licensed under the **EUPL v1.2** (European Union Public Licence)
- See `LICENSE.txt` for full text
- Permissive license for EU public sector software
- Allows modification and distribution with attribution

## Deployment

### Kubernetes (Helm)
- Helm charts located in `helm/nabu/`
- Deployed to Entur's Kubernetes clusters
- Environment-specific configurations

### Infrastructure (Terraform)
- Infrastructure definitions in `terraform/`
- Manages cloud resources (GCP)
- Environment separation via `terraform/env/`

## Important Notes for AI Assistants

1. **Event-Driven Architecture**: This application is heavily event-driven. Changes to event handling should consider downstream effects.

2. **Multi-Tenant**: The system handles multiple data providers (organizations). Always consider provider isolation.

3. **Database Migrations**: Schema changes MUST include Flyway migration scripts. Never modify schema without migrations.

4. **OpenAPI Contract**: The external API is contract-first. Changes to the API require OpenAPI spec updates first.

5. **Security First**: All REST endpoints should be properly secured. Internal vs external APIs have different security requirements.

6. **Notification Templates**: Email notifications use Freemarker templates in `src/main/resources/templates/`.

7. **Geospatial Data**: The application uses PostGIS for spatial queries. Be aware of spatial data types and operations.

8. **Integration Tests**: When modifying integrations, ensure Testcontainers-based tests are updated.

9. **Entur Ecosystem**: This service is part of Entur's larger data processing pipeline. Changes may affect other services.

10. **Monitoring**: All significant operations should emit metrics and logs for observability.

## Common Development Tasks

### Adding a New Event Type
1. Create event class in `domain.event` package
2. Implement `EventHandler` for processing
3. Register handler in `EventService`
4. Add database migration if needed
5. Update tests

### Adding a New REST Endpoint
1. Update OpenAPI specification if external API
2. Create resource class in appropriate `rest/` subdirectory
3. Implement service logic
4. Add security annotations
5. Write integration tests

### Modifying Database Schema
1. Create new Flyway migration in `db/migration/`
2. Update JPA entities
3. Test migration on clean database
4. Test upgrade from previous version
5. Update repository methods if needed

## Support and Documentation

- **Repository**: https://github.com/entur/nabu
- **CI/CD**: CircleCI and GitHub Actions
- **Organization**: Entur (Norwegian public transportation)
- **Package**: `no.rutebanken` (legacy namespace, now under Entur)

---

**Last Updated**: January 2025
**Version**: 0.0.1-SNAPSHOT
**Maintained by**: Entur
