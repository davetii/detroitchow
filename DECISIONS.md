# Technical Decisions

This document records key technical and architectural decisions made during DetroitChow development. Each decision includes the context, reasoning, and implications.

---

## Database Architecture

### Decision: PostgreSQL as Primary Database
**Date**: 2025-11-09
**Status**: Implemented

**Context**: Need a reliable, scalable database for restaurant data aggregation from multiple sources.

**Decision**: Use PostgreSQL with custom schema (`detroitchow`)

**Rationale**:
- Robust support for complex queries and relationships
- JSON support for flexible data from various APIs
- Future extensibility with PostGIS for geographic queries
- Strong community support and tooling
- Open source

---

## Data Model Decisions

### Decision: locationid as VARCHAR(50) instead of numeric
**Date**: 2025-11-09
**Status**: Implemented

**Context**: Need to support multiple data sources with different ID formats (legacy numeric, OpenStreetMap composite IDs, future API IDs)

**Decision**: Store `locationid` as VARCHAR(50)

**Rationale**:
- Legacy data uses numeric IDs (1-538)
- OSM data uses composite format: "osm-{type}{id}" (e.g., "osm-n123456" for node 123456)
- Future sources (Google Places, Yelp) may have alphanumeric IDs
- Provides maximum flexibility for data source integration
- Application layer can validate format per source

**Trade-offs**:
- Slightly larger storage footprint vs INT
- No automatic numeric validation at database level
- Requires application-level validation

---

### Decision: lat/lng as VARCHAR(50) instead of DECIMAL
**Date**: 2025-11-09
**Status**: Implemented

**Context**: Coordinates come from various sources with different precision levels

**Decision**: Store latitude/longitude as VARCHAR(50)

**Rationale**:
- Different data sources provide varying precision
- Allows flexible validation in application layer
- Can store raw values exactly as provided by source APIs
- Future migration to PostGIS GEOGRAPHY type if needed

**Trade-offs**:
- No database-level numeric validation
- Cannot use built-in spatial functions without conversion
- Slightly larger storage vs DECIMAL(10,8)
- Application must handle validation and formatting

**Future Consideration**: May migrate to PostGIS GEOGRAPHY type for advanced spatial queries

---

## Data Integrity

### Decision: Audit Columns via Database Triggers
**Date**: 2025-11-09
**Status**: Implemented

**Context**: Need to track when records are created and modified

**Decision**: Use PostgreSQL triggers to automatically populate audit columns (create_date, create_user, updated_date, update_user)

**Rationale**:
- Guarantees audit trail regardless of application code
- Prevents accidental omission in INSERT/UPDATE statements
- Centralized logic in database layer
- Cannot be bypassed by application bugs
- Consistent behavior across all future applications (web, mobile, admin tools)

**Implementation**: Triggers fire on INSERT (sets create fields) and UPDATE (sets update fields)

---

### Decision: CASCADE DELETE Foreign Keys
**Date**: 2025-11-09
**Status**: Implemented

**Context**: Related data (tags, menus, links, hours) should not exist without parent location

**Decision**: Use CASCADE DELETE on all foreign keys to `locations` table

**Rationale**:
- Maintains referential integrity automatically
- Prevents orphaned records
- Simplifies deletion logic in application layer
- Clear data ownership model (location owns all related data)

**Tables affected**: tags, menus, links, location_hours

---

## Data Strategy

### Decision: Multi-Source Data Aggregation
**Date**: 2025-11-09
**Status**: Partially Implemented

**Context**: No single data source has complete Metro Detroit restaurant information

**Decision**: Aggregate from multiple sources with conflict resolution in application layer

**Sources**:
1. **Legacy data** (Implemented): Original DetroitChow.com dataset (538 restaurants, 15 years old)
2. **OpenStreetMap** (imported): Community-maintained POI data
3. **Google Places API** (Planned): Current business information
4. **Yelp API** (Planned): Reviews and ratings
5. **Social media** (Planned): Facebook, Instagram, Twitter links

---

## Database Management

### Decision: Liquibase Managed Separately from Application
**Date**: 2025-11-27
**Status**: Implemented

**Context**: Database schema migrations need to be managed independently from application deployments to support multiple applications, controlled deployment timing, and infrastructure flexibility.

**Decision**: Liquibase configuration and changesets are maintained in `/database/liquibase/` as a standalone Maven project, separate from the Spring Boot application.

**Rationale**:
- **Deployment Independence**: Database migrations can be executed separately from application deployments
- **Multi-Application Support**: Future applications (public API, mobile backend, admin tools) can share the same database without duplicating migration logic
- **Controlled Execution**: Database changes can be applied during maintenance windows independent of code releases
- **Clear Separation of Concerns**: Schema management is distinct from application business logic
- **CI/CD Flexibility**: Migrations can be run as separate pipeline steps before or after application deployment
- **Rollback Safety**: Database rollbacks can be performed without requiring application redeployment
- **Team Collaboration**: DBAs or DevOps can manage schema changes without modifying application code

**Implementation**:
- Liquibase Maven project in `/database/liquibase/`
- Helper scripts: `migrate.sh`, `status.sh`, `rollback.sh`
- Separate profiles for local development and production environments
- Credentials stored in `liquibase.local.properties` (gitignored)
- Spring Boot applications use `hibernate.ddl-auto: none` to prevent schema modification
- Comprehensive documentation in `/database/liquibase/README.md`

**Trade-offs**:
- **Pro**: Independent versioning and deployment of schema vs application
- **Pro**: Simpler application configuration (no Liquibase dependency)
- **Pro**: Easier to audit and review schema changes separately
- **Con**: Requires manual migration step before running applications locally
- **Con**: Developers must remember to run migrations when switching branches with schema changes
- **Con**: Additional project to maintain

**Alternatives Considered**:
- **Embedded Liquibase in Spring Boot** - Rejected because it couples schema changes to application deployment and doesn't scale to multiple applications
- **Flyway** - Liquibase chosen for more advanced features (rollback support, multiple database format support, preconditions)
- **Manual SQL scripts** - Rejected due to lack of version tracking, rollback support, and migration state management

**Migration from Previous Approach**:
- Removed Liquibase dependency from `detroitchow-admin/pom.xml`
- Moved changesets from `detroitchow-admin/src/main/resources/db/changelog/` to `/database/liquibase/changelog/changesets/`
- Removed Liquibase configuration from all Spring Boot `application*.yml` files
- Changed Hibernate DDL mode from `validate` to `none` in production profile

---

## Technology Stack (In Progress)

### Decision: Backend Framework - TBD
**Date**: Under consideration
**Status**: Not decided

**Context**: Need to select backend framework for REST API development

**Leading Candidate**: Spring Boot (Java)

**Considerations**:
- Strong typing and enterprise patterns
- Large ecosystem for REST APIs
- PostgreSQL integration well-supported
- Team experience and preferences TBD

**Alternative Options**:
- Node.js/Express (JavaScript/TypeScript)
- Django/FastAPI (Python - synergy with data collection scripts)
- Go (performance, simpler deployment)

**Decision Pending**: Framework selection in progress

---

### Decision: Frontend Framework - TBD
**Date**: Under consideration
**Status**: Not decided

**Context**: Need to select frontend framework for web application

**Options Under Consideration**:
- React (large ecosystem, component-based)
- Vue (simpler learning curve, progressive)
- Next.js (React with SSR, SEO benefits)

**Requirements**:
- Responsive design (mobile-first)
- Fast initial load times
- Good SEO for restaurant discovery
- Map integration (Google Maps or Mapbox)

**Decision Pending**: Framework selection in progress

---

### Decision: Mobile Strategy - TBD
**Date**: Under consideration
**Status**: Not decided

**Context**: Need native or cross-platform mobile apps for iOS and Android

**Options Under Consideration**:
1. **Flutter** - Single codebase, native performance
2. **React Native** - JavaScript, large community
3. **Native** - Swift (iOS) + Kotlin (Android), best performance

**Considerations**:
- Development resources (one developer vs team)
- Performance requirements
- Platform-specific features needed
- Code sharing with web frontend

**Decision Pending**: Mobile strategy in progress

---

## Documentation Strategy

### Decision: Comprehensive Documentation for AI-Assisted Development
**Date**: 2025-11-10
**Status**: Implemented

**Context**: Project may have long gaps between development sessions, multiple collaborators, and AI-assisted development

**Decision**: Maintain detailed documentation including CLAUDE.md for AI assistant context

**Documents Created**:
- `README.md` - Project overview and quick start
- `DATABASE.md` - Schema documentation and conventions
- `DATA_COLLECTION.md` - Data source procedures
- `CLAUDE.md` - AI assistant development guidelines
- `ROADMAP.md` - Product backlog
- `CHANGELOG.md` - Version history
- `DECISIONS.md` - This file

**Rationale**:
- Reduces onboarding time for new collaborators
- Provides context across development sessions
- AI assistants can reference guidelines and conventions
- Documents the "why" behind non-obvious decisions
- Supports asynchronous collaboration

---

## Future Decisions Needed

- [ ] Backend framework selection (Spring Boot vs alternatives)
- [ ] Frontend framework selection (React vs Vue vs Next.js)
- [ ] Mobile development approach (Flutter vs React Native vs Native)
- [ ] Hosting and deployment strategy (AWS, GCP, Azure, Heroku)
- [ ] CI/CD pipeline approach
- [ ] Testing strategy (unit, integration, E2E)
- [ ] Authentication/authorization approach (OAuth, JWT, sessions)
- [ ] Caching strategy (Redis, CDN)
- [ ] Image storage and optimization (S3, Cloudinary)
- [ ] Analytics and monitoring tools
- [ ] SEO strategy and implementation
- [ ] Data deduplication and merge strategy for multi-source data
- [ ] API rate limiting and caching for external data sources

---

## Decision Template

Use this template for future decisions:

```markdown
### Decision: [Title]
**Date**: YYYY-MM-DD
**Status**: [Proposed | Accepted | Implemented | Deprecated | Superseded]

**Context**: [Describe the problem or situation requiring a decision]

**Decision**: [State the decision clearly]

**Rationale**:
- [Reason 1]
- [Reason 2]
- [Reason 3]

**Trade-offs**:
- [Pro/Con 1]
- [Pro/Con 2]

**Alternatives Considered**:
- [Alternative 1] - [Why not chosen]
- [Alternative 2] - [Why not chosen]
```
