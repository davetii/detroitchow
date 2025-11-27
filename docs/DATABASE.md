# Database Documentation

## Overview

DetroitChow uses PostgreSQL with a schema-based approach. All tables reside in the `detroitchow` schema and include comprehensive audit tracking via database triggers.

## Critical Conventions

These conventions are **non-negotiable** and must be followed in all code:

### 1. locationid is VARCHAR(50), NOT numeric
- Supports multiple data source formats:
  - Legacy numeric IDs: `"12345"`
  - OpenStreetMap composite IDs: `"osm-n123456"`, `"osm-w789012"`
  - Future source formats
- **Never treat as an integer** in queries or application code
- Always use string comparison and manipulation

### 2. lat/lng are VARCHAR(50), NOT DECIMAL
- Allows validation flexibility in application code
- Can store coordinates from various sources with different precision
- Application layer is responsible for validation and conversion
- Enables storing raw data as received from various APIs

### 3. Audit Columns
All tables include these columns:
- `create_date TIMESTAMP`
- `create_user VARCHAR(100)`
- `updated_date TIMESTAMP`
- `update_user VARCHAR(100)`

**Important:**
- **NEVER manually set these** in INSERT/UPDATE statements
- Automatically populated via database triggers
- Triggers use `CURRENT_TIMESTAMP` and `CURRENT_USER`

### 4. Schema Prefix Required
Always use the schema prefix in all SQL:
```sql
-- CORRECT
SELECT * FROM detroitchow.locations;

-- INCORRECT
SELECT * FROM locations;
```

### 5. Foreign Key Cascade Behavior
- All foreign keys use `ON DELETE CASCADE`
- Deleting a location automatically removes:
  - Associated tags
  - Menus
  - Links
  - Operating hours
- Ensures referential integrity without orphaned records

## Database Schema

### Core Tables

#### locations
Primary table storing restaurant and food market information.

**Columns:**
- `locationid VARCHAR(50) PRIMARY KEY` - Unique identifier (supports multiple formats)
- `name VARCHAR(200) NOT NULL` - Business name
- `description TEXT` - Detailed description
- `status VARCHAR(50)` - Enum: `active`, `temporarily_closed`, `permanently_closed`
- `address1 VARCHAR(200)` - Primary address line
- `address2 VARCHAR(200)` - Secondary address line (suite, unit, etc.)
- `city VARCHAR(100)` - City name
- `locality VARCHAR(100)` - Neighborhood or locality
- `region VARCHAR(100)` - State/province
- `country VARCHAR(100)` - Country (default: USA)
- `phone1 VARCHAR(50)` - Primary phone number
- `phone2 VARCHAR(50)` - Secondary phone number
- `lat VARCHAR(50)` - Latitude (stored as string)
- `lng VARCHAR(50)` - Longitude (stored as string)
- `website VARCHAR(500)` - Official website URL
- `facebook VARCHAR(500)` - Facebook page URL
- `twitter VARCHAR(500)` - Twitter profile URL
- `instagram VARCHAR(500)` - Instagram profile URL
- `opentable VARCHAR(500)` - OpenTable URL
- `tripadvisor VARCHAR(500)` - TripAdvisor URL
- `yelp VARCHAR(500)` - Yelp page URL
- Audit columns (managed by triggers)

**Current Data:** 538 locations imported from legacy data

#### tags
Many-to-many relationship for categorizing locations.

**Columns:**
- `tagid SERIAL PRIMARY KEY`
- `locationid VARCHAR(50) REFERENCES locations ON DELETE CASCADE`
- `tag VARCHAR(100) NOT NULL` - Category tag (e.g., "pizza", "vegan", "breakfast")
- Audit columns

**Indexes:**
- `idx_tags_locationid` on `locationid`
- `idx_tags_tag` on `tag`

#### menus
Menu links and images for locations.

**Columns:**
- `menuid SERIAL PRIMARY KEY`
- `locationid VARCHAR(50) REFERENCES locations ON DELETE CASCADE`
- `menu_url VARCHAR(500)` - Link to menu (PDF, image, or webpage)
- `menu_type VARCHAR(50)` - Type: `pdf`, `image`, `link`
- `description TEXT` - Menu description
- Audit columns

#### links
General links associated with locations (videos, articles, press, etc.).

**Columns:**
- `linkid SERIAL PRIMARY KEY`
- `locationid VARCHAR(50) REFERENCES locations ON DELETE CASCADE`
- `link_url VARCHAR(500) NOT NULL`
- `link_type VARCHAR(50)` - Type: `video`, `article`, `press`, `other`
- `description TEXT`
- Audit columns

#### location_hours
Operating hours for locations (stored as text entries).

**Columns:**
- `hoursid SERIAL PRIMARY KEY`
- `locationid VARCHAR(50) REFERENCES locations ON DELETE CASCADE`
- `day_of_week VARCHAR(20)` - Day: `Monday`, `Tuesday`, etc.
- `open_time VARCHAR(20)` - Opening time (e.g., "9:00 AM")
- `close_time VARCHAR(20)` - Closing time (e.g., "10:00 PM")
- `is_closed BOOLEAN` - Indicates if closed on this day
- Audit columns

#### sites
Reference table for social media and review site metadata.

**Columns:**
- `siteid SERIAL PRIMARY KEY`
- `site_name VARCHAR(100) NOT NULL UNIQUE` - Site name: `facebook`, `instagram`, etc.
- `icon_url VARCHAR(500)` - URL to site's icon/logo
- `display_name VARCHAR(100)` - User-friendly display name
- Audit columns

**Pre-populated sites:** Facebook, Instagram, Twitter, Yelp, TripAdvisor, OpenTable

## Schema Management with Liquibase

**Important:** Database schema changes are managed using Liquibase, maintained separately from application code in `/database/liquibase/`.

### Why Liquibase is Separate

- **Deployment Independence**: Database migrations can run separately from application deployments
- **Multi-Application Support**: Multiple applications can share the same database without duplicating migration logic
- **Controlled Execution**: Schema changes can be applied during maintenance windows independent of code releases
- **Clear Separation**: Schema management is distinct from application business logic

See `/database/liquibase/README.md` for comprehensive Liquibase documentation.

## Setup Instructions

### 1. Create Database

```bash
# Create database (if not exists)
createdb detroitchow

# Or using psql
psql -U postgres -c "CREATE DATABASE detroitchow;"
```

### 2. Configure Liquibase Credentials

```bash
cd database/liquibase
cp liquibase.properties liquibase.local.properties
# Edit liquibase.local.properties with your database credentials
```

### 3. Run Database Migrations

```bash
cd database/liquibase
./scripts/migrate.sh
```

This applies all Liquibase changesets, which create:
- The `detroitchow` schema
- All tables with constraints
- Triggers for audit columns
- Indexes for performance

### 4. Verify Migration Status

```bash
cd database/liquibase
./scripts/status.sh
```

### 5. Import Legacy Data (if needed)

```bash
psql -U your_user -d detroitchow -f data/imports/detroitchow_legacy_imports.sql
```

This imports 538 restaurants from the original DetroitChow.com.

### 4. Verify Import

```sql
-- Check locations count
SELECT COUNT(*) FROM detroitchow.locations;  -- Should return 538

-- Check data distribution
SELECT status, COUNT(*)
FROM detroitchow.locations
GROUP BY status;

-- Sample locations
SELECT locationid, name, city, status
FROM detroitchow.locations
LIMIT 10;
```

## Common Query Patterns

### Find Active Restaurants in a City

```sql
SELECT locationid, name, address1, phone1
FROM detroitchow.locations
WHERE status = 'active'
  AND city = 'Detroit'
ORDER BY name;
```

### Get Location with All Social Media Links

```sql
SELECT
    locationid,
    name,
    website,
    facebook,
    instagram,
    twitter,
    yelp
FROM detroitchow.locations
WHERE locationid = 'your-location-id';
```

### Find Locations by Tag

```sql
SELECT DISTINCT l.locationid, l.name, l.city
FROM detroitchow.locations l
JOIN detroitchow.tags t ON l.locationid = t.locationid
WHERE t.tag = 'pizza'
  AND l.status = 'active'
ORDER BY l.name;
```

### Get Location with Operating Hours

```sql
SELECT
    l.name,
    h.day_of_week,
    h.open_time,
    h.close_time,
    h.is_closed
FROM detroitchow.locations l
LEFT JOIN detroitchow.location_hours h ON l.locationid = h.locationid
WHERE l.locationid = 'your-location-id'
ORDER BY
    CASE h.day_of_week
        WHEN 'Monday' THEN 1
        WHEN 'Tuesday' THEN 2
        WHEN 'Wednesday' THEN 3
        WHEN 'Thursday' THEN 4
        WHEN 'Friday' THEN 5
        WHEN 'Saturday' THEN 6
        WHEN 'Sunday' THEN 7
    END;
```

### Count Locations by City

```sql
SELECT city, COUNT(*) as location_count
FROM detroitchow.locations
WHERE status = 'active'
GROUP BY city
ORDER BY location_count DESC;
```

## Database Maintenance

### Schema Changes

**All schema changes must be done through Liquibase changesets.**

1. Create a new changeset file in `/database/liquibase/changelog/changesets/`
2. Include it in `/database/liquibase/changelog/db.changelog-master.yaml`
3. Run migrations: `cd database/liquibase && ./scripts/migrate.sh`

See `/database/liquibase/README.md` for detailed instructions on creating changesets.

### Rollback Migrations

```bash
cd database/liquibase
./scripts/rollback.sh 1  # Rollback last changeset
```

Or using Maven directly:
```bash
cd database/liquibase
mvn liquibase:rollback -Plocal -Dliquibase.rollbackCount=1
```

### Check Migration Status

```bash
cd database/liquibase
./scripts/status.sh
```

### Backup Database

```bash
pg_dump -U your_user -d detroitchow -F c -f detroitchow_backup.dump
```

### Restore Database

```bash
pg_restore -U your_user -d detroitchow -c detroitchow_backup.dump
```

## Known Technical Debt

1. **Yelp Review Data**
   - Legacy JSON contains review data
   - Not yet migrated to database schema
   - Future: Create `reviews` table

2. **Data Validation**
   - No database-level validation on `lat`/`lng` values
   - Application layer must validate coordinate formats
   - Consider adding CHECK constraints or validation triggers

3. **Phone Number Format**
   - No standardization of phone number formats
   - Stored as received from various sources
   - Future: Implement normalization (e.g., E.164 format)

4. **URL Validation**
   - No validation that social media URLs are well-formed
   - No verification that URLs match expected domain patterns
   - Future: Add validation triggers or application-layer checks

5. **Hours Storage**
   - Hours stored as text, not structured time types
   - Limits ability to query "open now" efficiently
   - Future: Consider time range types or structured format

## Architecture Principles

1. **Strong Schema Enforcement** - Database enforces data integrity through constraints
2. **Audit Trail via Triggers** - Not application code, ensures consistency
3. **Parameterized Queries Only** - NEVER use string concatenation for SQL
4. **Multi-Source Support** - Schema designed to accommodate data from various APIs
5. **Cascade Deletes** - Maintain referential integrity automatically

## Performance Considerations

### Existing Indexes

- Primary keys on all tables (automatic)
- `idx_tags_locationid` - Speeds up tag lookups by location
- `idx_tags_tag` - Speeds up finding locations by tag

### Future Indexes to Consider

```sql
-- For city-based queries
CREATE INDEX idx_locations_city ON detroitchow.locations(city)
WHERE status = 'active';

-- For status queries
CREATE INDEX idx_locations_status ON detroitchow.locations(status);

-- For coordinate-based queries (if doing proximity searches)
-- Note: Requires PostGIS extension
CREATE INDEX idx_locations_geom ON detroitchow.locations
USING GIST (ST_MakePoint(lng::double precision, lat::double precision));
```

## Migration Strategy

When adding new data sources (Google Places, Yelp API, etc.):

1. Use appropriate `locationid` prefix (e.g., `google-{place_id}`, `yelp-{business_id}`)
2. Map source fields to schema columns
3. Normalize data formats where possible
4. Store raw source data in `description` or future `metadata` JSONB column
5. Handle duplicate detection (same physical location, different source IDs)

## Connection Strings

**Development:**
```
postgresql://username:password@localhost:5432/detroitchow
```

**Production:**
```
postgresql://username:password@production-host:5432/detroitchow?sslmode=require
```

## Security Notes

- Never commit database credentials to version control
- Use environment variables for connection strings
- Implement role-based access control (RBAC) for production
- Regularly rotate database passwords
- Enable SSL/TLS for production connections
