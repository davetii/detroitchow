# Detroit Chow Admin API

A Spring Boot microservice for managing Detroit Chow restaurant locations and menus. Built with Spring Boot 3.4.10, PostgreSQL, Liquibase, JUnit 5, Mockito, and Cucumber.

## Project Structure

```
detroitchow-admin/
├── src/
│   ├── main/
│   │   ├── java/com/detroitchow/admin/
│   │   │   ├── controller/          # REST controllers
│   │   │   ├── service/             # Business logic
│   │   │   ├── entity/              # JPA entities
│   │   │   ├── repository/          # Data access layer
│   │   │   ├── dto/                 # Data transfer objects
│   │   │   ├── mapper/              # Entity-DTO mappers
│   │   │   └── DetroitChowAdminApplication.java
│   │   └── resources/
│   │       ├── application.yml      # Main configuration
│   │       ├── application-test.yml # Test profile (H2)
│   │       ├── application-prod.yml # Production profile (PostgreSQL)
│   │       ├── api/
│   │       │   └── detroitchow-admin-api.yaml
│   │       └── db/
│   │           └── changelog/
│   │               ├── db.changelog-master.yaml
│   │               └── 001-init-schema.yaml
│   └── test/
│       ├── java/com/detroitchow/admin/
│       │   ├── service/                         # Service tests
│       │   └── cucumber/
│       │       ├── CucumberRunnerTest.java
│       │       └── steps/
│       │           └── LocationStepDefinitions.java
│       └── resources/
│           └── features/
│               └── locations.feature
├── pom.xml
└── README.md
```

## Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL 14+ (for production)
- Docker (optional, for PostgreSQL)

## Building the Project

### Clone and build

```bash
# Generate API classes from OpenAPI spec
mvn clean generate-sources

# Build the project
mvn clean package

# Build without running tests
mvn clean package -DskipTests
```

### OpenAPI Generator

The project uses OpenAPI Generator Maven plugin to generate controller interfaces from the OpenAPI spec:

```bash
mvn openapi-generator:generate
```

Generated files are placed in:
- Controllers: `target/generated-sources/openapi/src/main/java/com/detroitchow/admin/api/`
- Models: `target/generated-sources/openapi/src/main/java/com/detroitchow/admin/model/`

## Running the Application

### Development (H2 In-Memory Database)

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=test"
```

Access the API at `http://localhost:8080/api/v1`
Swagger UI: `http://localhost:8080/api/v1/swagger-ui.html`
H2 Console: `http://localhost:8080/api/v1/h2-console`

### Production (PostgreSQL)

Set environment variables:
```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=detroitchow
export DB_USER=detroitchow_owner
export DB_PASSWORD=your_password

mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

Or use `application-prod.yml` with environment-specific values.

## Database Setup

### PostgreSQL Setup

```bash
# Create database
createdb detroitchow

# Create user
createuser detroitchow_owner

# Grant privileges
psql -d detroitchow -c "ALTER ROLE detroitchow_owner WITH CREATEDB;"
```

### Liquibase Migrations

Migrations are automatically applied on application startup. Changelog files:
- `src/main/resources/db/changelog/db.changelog-master.yaml` - Master changelog
- `src/main/resources/db/changelog/001-init-schema.yaml` - Initial schema

Run migrations manually:
```bash
mvn liquibase:update
```

## API Endpoints

### Locations

- `GET /api/v1/locations` - Get all locations (with pagination and filtering)
- `GET /api/v1/location/{id}` - Get a specific location
- `POST /api/v1/location` - Create a new location
- `PUT /api/v1/location` - Update a location
- `DELETE /api/v1/location/{id}` - Delete a location

### Menus

- `GET /api/v1/location/{locationId}/menus` - Get menus for a location
- `POST /api/v1/location/{locationId}/menus` - Add a menu to a location
- `GET /api/v1/location/{locationId}/menus/{menuId}` - Get a specific menu
- `PUT /api/v1/location/{locationId}/menus/{menuId}` - Update a menu
- `DELETE /api/v1/location/{locationId}/menus/{menuId}` - Delete a menu
- `POST /api/v1/location/{locationId}/menus/reorder` - Reorder menus by priority

## Testing

### Unit Tests with Mockito

```bash
mvn test -Dtest=LocationServiceTest
```

### Integration Tests with Cucumber

```bash
mvn test
```

Feature files are in `src/test/resources/features/`

### Run specific test class

```bash
mvn test -Dtest=LocationServiceTest#testCreateLocation
```

## Configuration

### application.yml (Main)
- JPA/Hibernate settings
- Jackson JSON configuration
- Liquibase settings
- Logging levels

### application-test.yml (H2 Testing)
- H2 in-memory database
- Test-specific logging

### application-prod.yml (PostgreSQL Production)
- PostgreSQL database configuration
- Connection pooling with HikariCP
- Production logging levels
- Compression settings

## Dependencies

Key dependencies:
- **Spring Boot 3.4.10** - Framework
- **Spring Data JPA** - Data persistence
- **PostgreSQL Driver** - Production database
- **H2** - Test database
- **Liquibase** - Database migrations
- **Jackson 2.18.0** - JSON processing
- **Lombok** - Reduce boilerplate
- **JUnit 5** - Testing framework
- **Mockito** - Mocking
- **Cucumber 7.20.1** - BDD testing
- **AssertJ** - Fluent assertions
- **Springdoc OpenAPI** - Swagger UI

## Key Features

- **RESTful API** - Based on OpenAPI 3.0 specification
- **Database Support** - PostgreSQL (production), H2 (testing)
- **Database Migrations** - Liquibase managed
- **JSON Support** - Comprehensive Jackson configuration
- **Pagination** - Location list pagination
- **Filtering** - Filter locations by status
- **Menu Priority** - Manage menu order with priority field
- **Audit Columns** - Automatic create/update tracking
- **Error Handling** - Custom exceptions and error responses
- **Testing** - Unit tests, integration tests, and BDD with Cucumber
- **Logging** - SLF4J with proper logging levels

## Development Workflow

1. **Define API** - Edit `detroitchow-admin-api.yaml`
2. **Generate Code** - Run `mvn openapi-generator:generate`
3. **Implement Services** - Add business logic in `service/` package
4. **Implement Controllers** - Implement generated interfaces in `controller/` package
5. **Add Tests** - Write unit tests and Cucumber scenarios
6. **Run Tests** - Execute `mvn test`
7. **Build** - Run `mvn clean package`

## JSON Support

The application includes comprehensive JSON support:
- Jackson databind for core JSON processing
- JavaTime support for date/time serialization
- JDK8 optional type support
- Null-safe serialization
- Configurable pretty printing

## Logging

Logging is configured in `application.yml` with different levels for development and production:
- Development: DEBUG for com.detroitchow, INFO for frameworks
- Production: WARN for root logger, INFO for application

## Future Enhancements

- Google Places API integration
- OpenStreetMap integration
- Website parsing for menus and hours
- Hours calculation logic
- Authentication/Authorization
- Rate limiting
- Caching strategy
- Microservices patterns (circuit breaker, etc.)

## Troubleshooting

### H2 Connection Issues
Ensure H2 driver is in classpath and `MODE=PostgreSQL` is set in connection string

### Liquibase Errors
Check `db.changelog-master.yaml` includes correct paths to changelog files

### OpenAPI Generation Failures
Validate `detroitchow-admin-api.yaml` against OpenAPI 3.0 spec

## License

Copyright © 2024 Detroit Chow. All rights reserved.
