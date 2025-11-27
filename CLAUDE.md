# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## AI Assistant Session Startup Protocol

**REQUIRED - READ FIRST:** At the beginning of each new session, you MUST:

1. Check the Notion workspace for recent updates, decisions, and project context
2. Query the DetroitChow Notion database: https://www.notion.so/davetii/DetroitChow-2a6df0b1520d80408941c3f5b6c7185f
3. Review recent work history and current priorities
4. Look for any decisions or changes that affect the current task

**Why this matters:** The Notion workspace contains project history and context that supplements this codebase. Checking it ensures continuity across sessions and prevents duplicate or conflicting work.

**How to access:** Use available Notion MCP tools (if connected) to query the workspace before starting any work.

## Project Overview

DetroitChow is a restaurant discovery platform for Metro Detroit that aggregates restaurant and specialty food market information from multiple sources. The project is in initial development phase with a PostgreSQL database and planned Spring Boot backend.

**Domain:** detroitchow.com
**Target Platforms:** Web (responsive), Android, iOS

## Technology Stack

- **Database:** PostgreSQL (schema: `detroitchow`)
  - **Schema Management:** Liquibase (managed separately in `/database/liquibase/`)
- **Backend Admin API:** Spring Boot 3.4.10 (Java 21) with RESTful API
  - OpenAPI 3.0 specification-driven design
  - JPA/Hibernate for ORM
  - Testing: JUnit 5, Mockito, Cucumber BDD
  - Development: H2 in-memory database
  - Production: PostgreSQL with HikariCP connection pooling
- **Frontend Admin Tool:** TBD (React, Vue, or similar)
- **Frontend (Pending):** TBD (React, Vue, or similar)
- **Data Scripts:** Python for data collection and migration
- **Python Dependencies:** requests library (for OpenStreetMap API queries)

## Critical Database Conventions

These conventions are non-negotiable and must be followed:

1. **locationid is VARCHAR(50), NOT numeric**
   - Supports multiple data source formats (legacy numeric, OSM composite IDs like "osm-n123456")
   - Never treat as an integer in queries or code

2. **lat/lng are VARCHAR(50), NOT DECIMAL**
   - Allows validation flexibility in application code
   - Can store coordinates from various sources with different precision

3. **All tables include audit columns** (create_date, create_user, updated_date, update_user)
   - NEVER manually set these in INSERT/UPDATE statements
   - Automatically populated via database triggers

4. **Always use schema prefix:** `detroitchow.tablename` in all SQL

5. **Foreign keys use CASCADE DELETE** - Deleting a location automatically removes tags, menus, links, hours

## Database Schema

### Core Tables

- **locations** - Primary table (538 imported from legacy data)
  - locationid (PK, VARCHAR(50))
  - name, description, status (enum: active/temporarily_closed/permanently_closed)
  - address1, address2, city, locality, region, country
  - phone1, phone2, lat, lng
  - website, facebook, twitter, instagram, opentable, tripadvisor, yelp

- **tags** - Many-to-many categories for locations
- **menus** - Menu links and images
- **links** - General links (videos, articles)
- **sites** - Reference data for site icons (Instagram, Facebook, etc.)
- **location_hours** - Operating hours text entries

### Working with the Database

**Database migrations are managed by Liquibase in `/database/liquibase/`**

**Run database migrations:**
```bash
cd database/liquibase
./scripts/migrate.sh
```

**Check migration status:**
```bash
cd database/liquibase
./scripts/status.sh
```

**See full documentation:**
```bash
cat database/liquibase/README.md
```

**Verify data:**
```sql
SELECT COUNT(*) FROM detroitchow.locations;  -- Should return 538
```

**Example query pattern:**
```sql
SELECT locationid, name, city
FROM detroitchow.locations
WHERE status = 'active' AND city = 'Detroit';
```

## Data Collection Scripts

Located in `scripts/data-collect/`:

### OpenStreetMap Data Collection

**Query restaurants by city:**
```bash
cd scripts/data-collect
python get_restaurants_by_city.py 'Michigan' 'Sterling Heights'
```

**Query restaurants by county:**
```bash
cd scripts/data-collect
python osm_restaurant_importer.py
```

Both scripts:
- Query the Overpass API (OpenStreetMap)
- Save results to JSON files in the current directory
- Do NOT automatically write to the database (files are for review first)
- Generate locationid as "osm-{type}{id}" (e.g., "osm-n123456" for node 123456)

### Script Outputs

- `get_restaurants_by_city.py` → `{City}_{State}_restaurants.json`
- `osm_restaurant_importer.py` → `macomb_county_restaurants.json` and `.csv`

## Data Sources

### Current Data

1. **Legacy Data** (IMPORTED)
   - Source: Original DetroitChow.com from 15 years ago
   - File: `data/legacy/GetStores-response-indented.json` and `get.json`
   - 538 restaurants successfully imported
   - Import SQL: `data/imports/detroitchow_legacy_imports.sql`

2. **OpenStreetMap Data** (COLLECTED, not yet imported)
   - County-level queries in `data/osm-raw/`
   - City-level queries in `scripts/data-collect/`
   - Extensive Metro Detroit coverage collected

### Future Data Sources

- Google Places API
- Yelp API
- Social media APIs (Facebook, Instagram, Twitter)
- Manual submissions
- Web scraping (ethical, respecting robots.txt)

## Spring Boot Admin API Development

Located in `detroitchow-admin/`

### Building and Running

**Build the project:**
```bash
cd detroitchow-admin
mvn clean package
```

**Run with H2 (development):**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=test"
```

**Run with PostgreSQL (production):**
```bash
export DB_HOST=localhost DB_PORT=5432 DB_NAME=detroitchow
export DB_USER=detroitchow_owner DB_PASSWORD=your_password
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

### OpenAPI Code Generation

The API is designed using OpenAPI 3.0 specification. Controller interfaces and model classes are generated from the spec:

```bash
cd detroitchow-admin
mvn openapi-generator:generate
```

Generated files: `target/generated-sources/openapi/`

### API Endpoints

- Base URL: `http://localhost:8080/api/v1`
- Swagger UI: `http://localhost:8080/api/v1/swagger-ui.html`
- API Spec: `detroitchow-admin/src/main/resources/api/detroitchow-admin-api.yaml`

**Key Endpoints:**
- `GET /api/v1/locations` - List all locations (with pagination/filtering)
- `POST /api/v1/location` - Create location
- `PUT /api/v1/location` - Update location
- `DELETE /api/v1/location/{id}` - Delete location
- `GET /api/v1/location/{locationId}/menus` - Get menus for a location
- `POST /api/v1/location/{locationId}/menus` - Add menu to a location

### Testing

**Run all tests:**
```bash
cd detroitchow-admin
mvn test
```

**Run specific test:**
```bash
mvn test -Dtest=LocationServiceTest
```

### Database Migrations

**Database schema is managed separately in `/database/liquibase/`**

The admin application does NOT manage migrations. All schema changes must be applied via Liquibase before deploying the application.

See the [Database Management](#working-with-the-database) section for migration instructions.

## Python Development

**Activate virtual environment:**
```bash
source venv/bin/activate  # Linux/Mac
venv\Scripts\activate     # Windows
```

**Install dependencies:**
```bash
pip install requests
```

**Run data collection scripts:**
```bash
cd scripts/data-collect
python osm_restaurant_importer.py
```

## Architecture Principles

1. **API-First Design** - Backend exposes REST API, all clients consume it
2. **Strong Schema Enforcement** - Database enforces data integrity
3. **Audit Trail via Triggers** - Not application code
4. **Parameterized Queries Only** - NEVER string concatenation for SQL
5. **URL Validation Required** - Social media links must be validated
6. **Multi-Source Support** - locationid and coordinate fields support various formats

## Known Technical Debt

1. **Yelp Review Data:** Legacy JSON contains reviews but not migrated to schema yet
2. **Data Validation:** No validation on lat/lng values (stored as strings)
3. **Phone Format:** No standardization of phone number formats
4. **URL Validation:** No validation that social media URLs are well-formed

## Directory Structure

```
detroitchow/
├── database/
│   ├── liquibase/              # Database migration management
│   │   ├── changelog/          # Liquibase changesets
│   │   ├── scripts/            # Helper scripts (migrate.sh, status.sh, rollback.sh)
│   │   ├── pom.xml             # Maven configuration for Liquibase
│   │   ├── liquibase.properties # Template configuration
│   │   └── README.md           # Full migration documentation
│   └── legacy/                 # Legacy SQL files (reference only)
├── data/
│   ├── legacy/                 # Original 15-year-old dataset (2 JSON files)
│   ├── imports/                # Generated SQL import files
│   └── osm-raw/                # OpenStreetMap query results by county
├── detroitchow-admin/          # Spring Boot Admin API
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
│   │   │       └── api/              # OpenAPI specification
│   │   └── test/                     # Unit & integration tests
│   ├── pom.xml                       # Maven build configuration
│   └── README.md                     # Admin API documentation
├── scripts/
│   └── data-collect/           # OSM data collection scripts + city-level results
├── venv/                       # Python virtual environment
└── CLAUDE.md                   # Project documentation and AI assistant guidance
```

## Resources

- **Notion Knowledge Base:** https://www.notion.so/davetii/DetroitChow-2a6df0b1520d80408941c3f5b6c7185f
- **Domain:** detroitchow.com

## Development Status

✅ **Completed:**
- Database schema design and implementation
- Initial data migration from legacy JSON (538 locations)
- OpenStreetMap data collection scripts
- Extensive OSM data collection (Metro Detroit counties and cities)
- **Database Migration System:**
  - Liquibase configuration in `/database/liquibase/`
  - Maven-based migration management
  - Helper scripts for common operations
  - Separate from application deployment
- **Backend Admin API (detroitchow-admin):**
  - Spring Boot 3.4.10 application with Java 21
  - OpenAPI 3.0 specification and code generation
  - Location and Menu management endpoints
  - JPA entities, repositories, services, and controllers
  - Comprehensive testing suite (JUnit, Mockito, Cucumber)
  - Swagger UI for API documentation
  - Multi-profile support (test/H2, prod/PostgreSQL)

🚧 **In Progress:**
- Google Places API integration
- Frontend Admin Tool development

📋 **Planned:**
- Public-facing frontend implementation
- Mobile app development (Android/iOS)
- Social media aggregation features
- Authentication and authorization
- Rate limiting and caching
