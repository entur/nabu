# Nabu - Project Guide for Claude

## Project Overview

**Nabu** is an operational status application developed by Entur (Norway's public transportation authority) that tracks and monitors timetable data delivery processes. It provides real-time status updates and notifications about data processing jobs in the public transportation data pipeline.

### Purpose
- Monitor timetable data delivery status
- Track job execution events across the data processing pipeline
- Provide notifications to users about data processing outcomes
- Maintain operational visibility for public transport data management

## Key Components

### 1. Event Processing System
The core of Nabu is its event processing system that handles various types of events:
- **JobEvent**: Tracks job execution states (queued, in progress, completed, failed)
- **CrudEvent**: Monitors create/read/update/delete operations
- **Notification**: Manages user notifications about job outcomes
- **TimeTableAction**: Specific actions related to timetable processing

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

11. **Retired GEOCODER domain**: Geocoder v1 (Pelias) was decommissioned in the v1->v3 migration. `GEOCODER` was removed from `JobEvent.JobDomain` (and the ninkasi UI / marduk job logic). The `domain` column is a plain `String`, not an enum ordinal, so historical rows with `domain='GEOCODER'` remain readable; new code simply never produces or advertises it. Do not reintroduce a GEOCODER job domain here.

