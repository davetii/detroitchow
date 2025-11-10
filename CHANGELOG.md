# Changelog

All notable changes to the DetroitChow project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### In Progress
- Technology stack finalization (backend framework selection)
- Development environment setup and tooling decisions
- Product feature prioritization
- OpenStreetMap data integration and import procedures

### Planned
- Backend API development (Spring Boot candidate)
- Frontend framework selection and implementation
- Mobile app development (iOS and Android)
- Social media data aggregation features
- Automated data validation and quality checks

## [0.1.0] - 2025-11-10

### Added
- **Database Schema**: PostgreSQL schema with 6 core tables (`locations`, `tags`, `menus`, `links`, `sites`, `location_hours`)
  - Automatic audit trail via database triggers
  - CASCADE DELETE foreign keys for data integrity
- **Data Collection**: Python scripts for OpenStreetMap queries (by city and county)
- **Legacy Data Import**: 538 restaurants from original DetroitChow.com (15 years old)
- **Documentation**: DATABASE.md, DATA_COLLECTION.md, CLAUDE.md, ROADMAP.md, DECISIONS.md, CHANGELOG.md, README.md
- **Project Infrastructure**: Python venv, git repo, directory structure

### Data Status
- 538 legacy restaurants imported
- Metro Detroit OSM data collected (awaiting import and review)

**See [DECISIONS.md](DECISIONS.md) for technical decisions and rationale**

## [0.0.1] - 2025-11-09

### Added
- Initial project setup and repository structure
- Base directory structure for database, data, scripts, and documentation

---

## Version History Notes

### Version Numbering
- **0.x.x**: Pre-release development phase
- **1.0.0**: First production release (planned after MVP features complete)

### Categories Used
- **Added**: New features, files, or capabilities
- **Changed**: Changes to existing functionality
- **Deprecated**: Features that will be removed in future versions
- **Removed**: Features that have been removed
- **Fixed**: Bug fixes
- **Security**: Security improvements or vulnerability fixes
- **Technical Decisions**: Architectural or design decisions made
- **Data Collection Completed**: Data gathering milestones

### Key Milestones Ahead
- [ ] Backend API implementation
- [ ] Frontend application launch
- [ ] Mobile apps (iOS/Android)
- [ ] Production deployment
- [ ] Version 1.0.0 release
