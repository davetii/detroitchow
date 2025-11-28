# DetroitChow

A restaurant discovery platform for Metro Detroit, aggregating restaurant and specialty food market information from multiple sources.

**Domain:** [detroitchow.com](https://detroitchow.com)

## Overview

DetroitChow helps users discover restaurants and food markets across Metro Detroit by combining data from multiple sources including legacy data, OpenStreetMap, and future integrations with Google Places, Yelp, and social media platforms.

## Technology Stack

- **Database:** PostgreSQL (schema: `detroitchow`)
- **Backend Admin API:** Spring Boot 3.4.10 (Java 21) with RESTful API
  - OpenAPI 3.0 specification-driven design
  - Liquibase for database migrations
  - JPA/Hibernate for ORM
  - Comprehensive testing (JUnit 5, Mockito, Cucumber)
- **Frontend:** React with Fetch API
- **Data Collection:** Python scripts for data aggregation
- **Target Platforms:** Web (responsive), Android, iOS

## Prerequisites

- PostgreSQL 12+ installed and running
- Java 21+ (for Spring Boot Admin API)
- Maven 3.8+ (for building Java projects)
- Python 3.8+ with `pip` (for data collection scripts)
- Git
- Node 24
- React ^19.2
- Vite

## Quick Start

### 1. Database Setup

```bash
createdb detroitchow
psql -U your_user -d detroitchow -f database/schema/schema.sql
```

### 2. Import Data

```bash
psql -U your_user -d detroitchow -f data/imports/detroitchow_legacy_imports.sql
```

### 3. Spring Boot Admin API

```bash
cd detroitchow-admin

# Build the project
mvn clean package

# Run with H2 in-memory database (development)
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=test"

# Access API at: http://localhost:8080/api/v1
# Swagger UI: http://localhost:8080/api/v1/swagger-ui.html
```

For production PostgreSQL setup:
```bash
export DB_HOST=localhost DB_PORT=5432 DB_NAME=detroitchow
export DB_USER=detroitchow_owner DB_PASSWORD=your_password
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

See **[detroitchow-admin/README.md](detroitchow-admin/README.md)** for detailed API documentation.

### 4. Python Environment

```bash
python -m venv venv
source venv/bin/activate  # Linux/Mac
pip install requests
```

See **[DATABASE.md](docs/DATABASE.md)** for detailed setup instructions and **[DATA_COLLECTION.md](docs/DATA_COLLECTION.md)** for data collection scripts.

## Project Structure

```
detroitchow/
├── database/
│   ├── schema.sql                  # Database schema with triggers
│   └── google-places.sql           # Google Places import SQL
├── data/
│   ├── legacy/                     # Original 15-year-old dataset
│   ├── imports/                    # Generated SQL import files
│   └── osm-raw/                    # OpenStreetMap query results
├── detroitchow-admin/              # Spring Boot Admin API
│   ├── src/
│   │   ├── main/java/              # Java source code
│   │   ├── main/resources/         # Configuration and OpenAPI spec
│   │   └── test/                   # Tests (JUnit, Mockito, Cucumber)
│   ├── pom.xml                     # Maven configuration
│   └── README.md                   # Admin API documentation
├── docs/                           # Documentation
│   ├── DATABASE.md                 # Database documentation
│   └── DATA_COLLECTION.md          # Data collection guide
├── scripts/
│   └── data-collect/               # Data collection scripts
├── venv/                           # Python virtual environment
├── CLAUDE.md                       # AI assistant instructions
├── PROJECT_CONTEXT.md              # Detailed project context
└── README.md                       # This file
```

## Current Status

**✅ Completed:**
- Database schema design and implementation
- Legacy data import (538 locations)
- OpenStreetMap data collection
- Spring Boot Admin API with Location and Menu management
- OpenAPI 3.0 specification and code generation
- Comprehensive testing framework (JUnit, Mockito, Cucumber)
- Liquibase database migrations

**🚧 In Progress:**
- Google Places API integration
- Frontend Admin Tool development
- **[TODO.md](TODO.md)**

**📋 Planned:**
- Public-facing web frontend
- Mobile applications (Android/iOS)
- Social media aggregation
- Authentication and authorization
- **[ROADMAP.md](ROADMAP.md)** - Product Backlog

## Documentation

### Planning & Progress
- **[TODO.md](TODO.md)** - Active tasks, blockers, and immediate next steps
- **[ROADMAP.md](ROADMAP.md)** - Product backlog and feature roadmap
- **[CHANGELOG.md](CHANGELOG.md)** - Version history and release notes
- **[DECISIONS.md](DECISIONS.md)** - Technical decisions and architectural rationale

### Technical Documentation
- **[DATABASE.md](docs/DATABASE.md)** - Database schema, setup, conventions, and query examples
- **[DATA_COLLECTION.md](docs/DATA_COLLECTION.md)** - Data sources, collection scripts, and import procedures
- **[CLAUDE.md](CLAUDE.md)** - AI assistant instructions and development guidelines

## Resources

- **Notion Knowledge Base:** [DetroitChow Workspace](https://www.notion.so/davetii/DetroitChow-2a6df0b1520d80408941c3f5b6c7185f)
- **Domain:** [detroitchow.com](https://detroitchow.com)

## Contributing

This project is in early development. Please refer to the documentation:
- [DECISIONS.md](DECISIONS.md) for technical decisions and rationale
- [DATABASE.md](docs/DATABASE.md) for database standards and conventions
- [DATA_COLLECTION.md](docs/DATA_COLLECTION.md) for data collection guidelines
- [CLAUDE.md](CLAUDE.md) for development guidelines

## License

[License information to be added]
