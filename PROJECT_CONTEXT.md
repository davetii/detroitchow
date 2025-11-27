# DetroitChow Project Context

**Last Updated:** November 2025  
**Project Status:** Initial Development Phase  
**Domain:** detroitchow.com (owned)

## Quick Overview

DetroitChow is a comprehensive restaurant discovery platform for Metro Detroit that aggregates information from multiple sources to help users discover and interact with restaurants and specialty food markets.

## Target Platforms

- **Web:** Responsive web application
- **Android:** Native or cross-platform Android app  
- **iOS:** Native or cross-platform iPhone app

## Technology Stack

### Database
- **Engine:** PostgreSQL
- **Schema:** `detroitchow`
- **Schema Management:** Liquibase (standalone in `/database/liquibase/`)

### Backend Admin API (detroitchow-admin)
- **Framework:** Spring Boot 3.4.10
- **Language:** Java 21
- **API Style:** RESTful (OpenAPI 3.0 specification-driven)
- **ORM:** JPA/Hibernate
- **Database Migration:** Liquibase
- **Testing:** JUnit 5, Mockito, Cucumber BDD
- **API Documentation:** Swagger UI / Springdoc OpenAPI
- **Development DB:** H2 in-memory
- **Production DB:** PostgreSQL with HikariCP
- **Build Tool:** Maven 3.8+

### Frontend (Pending Decision)
- **Admin Tool:** TBD (React, Vue, or similar)
- **Public Web:** TBD (React, Vue, or similar)
- **Mobile:** TBD (Flutter, React Native, or native)

### Development Tools
- Python for data migration/scripting
- Docker for local development
- Git for version control
- Maven for Java builds
- OpenAPI Generator for code generation

## Database Schema Overview

### Core Tables

**locations** - Primary table storing restaurants and food markets
- Primary Key: `locationid` VARCHAR(50) - NOT numeric
- Contains: name, address, contact info, coordinates, social media links
- Status: Enum of 'active', 'temporarily_closed', 'permanently_closed'
- Audit columns on all tables (create_date, create_user, updated_date, update_user)

**tags** - Categories/labels for locations (many-to-many)

**menus** - Menu links and images for locations

**links** - General links (videos, articles, etc.) for locations

**sites** - Reference data for site icons and links (Instagram, Facebook, etc.)

**location_hours** - Operating hours text entries

### Key Schema Conventions

❗ **CRITICAL CONVENTIONS:**

1. **locationid is VARCHAR(50), NOT numeric**
   - Supports multiple data source formats
   - Original data uses numeric IDs, but future sources may not

2. **lat/lng are VARCHAR(50), NOT DECIMAL**
   - Allows validation flexibility in application code
   - Handles malformed data gracefully
   - Can store coordinates from various sources with different precision

3. **All tables include audit columns**
   - create_date, create_user, updated_date, update_user
   - Automatically populated via triggers
   - DO NOT manually set these in INSERT/UPDATE statements

4. **Always use schema prefix:** `detroitchow.tablename`

5. **Foreign keys use CASCADE DELETE** for child tables
   - Deleting a location automatically removes tags, menus, links, hours

## Project Structure

```
detroitchow/
├── database/           # All database-related files
│   ├── schema.sql              # Current production schema
│   └── google-places.sql       # Google Places import SQL
│
├── data/
│   ├── legacy/                 # Original 15-year-old dataset
│   ├── imports/                # Generated SQL import files
│   └── osm-raw/                # OpenStreetMap query results
│
├── detroitchow-admin/  # Spring Boot Admin API
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/detroitchow/admin/
│   │   │   │   ├── controller/       # REST controllers
│   │   │   │   ├── service/          # Business logic
│   │   │   │   ├── entity/           # JPA entities
│   │   │   │   ├── repository/       # Data access layer
│   │   │   │   ├── dto/              # Data transfer objects
│   │   │   │   └── mapper/           # Entity-DTO mappers
│   │   │   └── resources/
│   │   │       ├── application*.yml  # Spring Boot configuration
│   │   │       ├── api/              # OpenAPI specification
│   │   │       └── db/changelog/     # Liquibase migrations
│   │   └── test/                     # Unit & integration tests
│   ├── pom.xml                       # Maven build configuration
│   └── README.md                     # Admin API documentation
│
├── scripts/
│   └── data-collect/           # Data collection scripts
│
├── venv/               # Python virtual environment
└── PROJECT_CONTEXT.md  # This file
```

## Current Status

### ✅ Completed
- Database schema design and implementation
- Initial data migration from legacy JSON dataset (538 locations)
- OpenStreetMap data collection scripts
- Python migration script (`generate_location_inserts.py`)
- Schema drop/recreate scripts
- **Backend Admin API (detroitchow-admin):**
  - Spring Boot 3.4.10 application with Java 21
  - OpenAPI 3.0 specification and code generation pipeline
  - Location and Menu management REST endpoints
  - JPA entities, repositories, services, controllers, DTOs, and mappers
  - Liquibase database migration framework
  - Comprehensive testing suite (JUnit 5, Mockito, Cucumber BDD)
  - Swagger UI for interactive API documentation
  - Multi-profile configuration (test/H2, prod/PostgreSQL)
  - Maven build with OpenAPI Generator plugin

### 🚧 In Progress
- Google Places API integration
- Frontend Admin Tool development

### 📋 Planned
- Public-facing frontend implementation
- Mobile app development (Android/iOS)
- Social media aggregation features
- Authentication and authorization
- Rate limiting and caching strategies

## Data Sources

### Legacy Data (Imported)
- **Source:** Original DetroitChow.com from 15 years ago
- **Format:** JSON array with 538 restaurant objects
- **Location:** `data/legacy/detroitchow-legacy-538.json`
- **Status:** Successfully migrated to database

**Legacy Data Field Mapping:**
```
JSON Field → Database Column
storeId → locationid
name → name
address → address1
city → city
state → region
phone → phone1
website → website
facebook → facebook
twitter → twitter
lat → lat
lng → lng
```

### Future Data Sources (Planned)
- Google Places API
- Yelp API
- Social media APIs (Facebook, Instagram, Twitter)
- Manual submissions
- Web scraping (ethically, with respect to robots.txt)

## Key Files Reference

| File | Purpose | Location |
|------|---------|----------|
| schema.sql | Database schema definition | `database/` |
| google-places.sql | Google Places import SQL | `database/` |
| detroitchow-admin-api.yaml | OpenAPI specification | `detroitchow-admin/src/main/resources/api/` |
| pom.xml | Maven build configuration | `detroitchow-admin/` |
| application.yml | Main Spring Boot config | `detroitchow-admin/src/main/resources/` |
| db.changelog-master.yaml | Liquibase master changelog | `detroitchow-admin/src/main/resources/db/changelog/` |

## Development Conventions

### Naming Conventions
- **Database:** snake_case (locationid, create_date)
- **Java:** camelCase for variables, PascalCase for classes
- **SQL Keywords:** UPPERCASE (SELECT, INSERT, CREATE)
- **File Names:** kebab-case for scripts (generate-locations.py)

### Code Organization
- Keep database scripts in `database/`
- One-off utilities in `scripts/`
- Production code in respective platform directories
- Documentation in `docs/`

### Git Commit Messages
- Use present tense ("Add feature" not "Added feature")
- Reference issue numbers when applicable
- Keep first line under 50 characters
- Provide detailed explanation in body if needed

## Common Tasks

### Database Reset
```bash
psql -U your_user -d detroitchow -f database/schema/drop_schema.sql
psql -U your_user -d detroitchow -f database/schema/schema.sql
```

### Import Legacy Data
```bash
cd database/seed-data
python generate_location_inserts.py ../../data/legacy/detroitchow-legacy-538.json ../../data/imports/locations_import.sql
psql -U your_user -d detroitchow -f ../../data/imports/locations_import.sql
```

### Verify Import
```sql
SELECT COUNT(*) FROM detroitchow.locations;
-- Should return 538
```

### Spring Boot Admin API

**Build the project:**
```bash
cd detroitchow-admin
mvn clean package
```

**Run with H2 in-memory database (development):**
```bash
cd detroitchow-admin
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=test"
```

**Run with PostgreSQL (production):**
```bash
export DB_HOST=localhost DB_PORT=5432 DB_NAME=detroitchow
export DB_USER=detroitchow_owner DB_PASSWORD=your_password
cd detroitchow-admin
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

**Access API:**
- Base URL: `http://localhost:8080/api/v1`
- Swagger UI: `http://localhost:8080/api/v1/swagger-ui.html`
- H2 Console (test profile): `http://localhost:8080/api/v1/h2-console`

**Run tests:**
```bash
cd detroitchow-admin
mvn test
```

**Generate OpenAPI code:**
```bash
cd detroitchow-admin
mvn openapi-generator:generate
```

## Architecture Notes

### Design Philosophy
- API-first design (backend exposes REST API)
- Mobile and web apps are clients of the API
- Database is PostgreSQL with strong schema enforcement
- Prefer normalization over denormalization initially
- Use database triggers for audit trail (not application code)

### Scalability Considerations
- Current schema supports ~100k locations without optimization
- Indexes on frequently queried columns (status, city, region)
- Prepared for future sharding by locationid if needed

### Security Notes
- All user input must be validated and sanitized
- Use parameterized queries (NEVER string concatenation for SQL)
- Social media links should be validated as proper URLs
- Phone numbers stored as strings (international format flexibility)

## Known Issues / Technical Debt

1. **Yelp Review Data:** Legacy JSON contains Yelp reviews but not migrated to schema yet
2. **Data Validation:** No validation on lat/lng values yet (stored as strings)
3. **Phone Format:** No standardization of phone number formats
4. **URL Validation:** No validation that social media URLs are well-formed

## Resources

- **Notion Knowledge Base:** https://www.notion.so/davetii/DetroitChow-2a6df0b1520d80408941c3f5b6c7185f
- **Domain:** detroitchow.com
- **Original Site:** (archived 15 years ago)

## Notes for AI Tools

When working with this codebase:

1. **Always check this file first** for project context and conventions
2. **locationid is VARCHAR** - Don't treat it as numeric in queries or code
3. **Don't manually set audit columns** - They're auto-populated by triggers
4. **Use schema prefix** - Always qualify table names with `detroitchow.`
5. **Consult Notion** - The Notion knowledge base has additional architectural decisions
6. **OpenAPI-first design** - Modify `detroitchow-admin-api.yaml` first, then generate code with `mvn openapi-generator:generate`
7. **Spring Boot profiles** - Use `test` profile for H2 development, `prod` for PostgreSQL
8. **Liquibase for migrations** - Never manually alter database schema; create Liquibase changesets
9. **Comprehensive testing** - Write JUnit tests for services, Cucumber scenarios for BDD integration tests

## Contact / Maintainer

- **Developer:** Dave
- **Role:** Solutions Architect / Full Stack Developer
- **Experience:** Spring Boot, Node.js, PostgreSQL, Kubernetes, microservices

---

*This file should be updated as architectural decisions are made and the project evolves.*