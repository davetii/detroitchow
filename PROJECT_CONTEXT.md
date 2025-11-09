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
- **Location:** `database/schema/schema.sql`

### Backend (Planned)
- **Framework:** Spring Boot (Java)
- **API Style:** RESTful
- **Primary Language:** Java

### Frontend (Pending Decision)
- **Web:** TBD (React, Vue, or similar)
- **Mobile:** TBD (Flutter, React Native, or native)

### Development Tools
- Python for data migration/scripting
- Docker for local development
- Git for version control

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
│   ├── schema/
│   │   ├── schema.sql          # Current production schema
│   │   └── drop_schema.sql     # Clean drop script
│   ├── migrations/             # Schema version history
│   └── seed-data/              # Data import scripts
│
├── data/
│   ├── legacy/                 # Original 15-year-old dataset
│   ├── imports/                # Generated SQL import files
│   └── exports/                # Data exports
│
├── backend/            # Spring Boot API
├── web/                # Web frontend
├── mobile/             # Mobile apps
├── scripts/            # Utility scripts
└── docs/               # Project documentation
```

## Current Status

### ✅ Completed
- Database schema design and implementation
- Initial data migration from legacy JSON dataset (538 locations)
- Python migration script (`generate_location_inserts.py`)
- Schema drop/recreate scripts

### 🚧 In Progress
- Technology stack finalization
- Development environment setup

### 📋 Planned
- Backend API development
- Frontend implementation
- Mobile app development
- Social media aggregation features

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
| schema.sql | Database schema definition | `database/schema/` |
| drop_schema.sql | Clean schema removal | `database/schema/` |
| generate_location_inserts.py | JSON to SQL converter | `database/seed-data/` |
| detroitchow-legacy-538.json | Original dataset | `data/legacy/` |

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
6. **Ask before major changes** - Tech stack decisions are still being finalized

## Contact / Maintainer

- **Developer:** Dave
- **Role:** Solutions Architect / Full Stack Developer
- **Experience:** Spring Boot, Node.js, PostgreSQL, Kubernetes, microservices

---

*This file should be updated as architectural decisions are made and the project evolves.*