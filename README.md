# DetroitChow

A restaurant discovery platform for Metro Detroit, aggregating restaurant and specialty food market information from multiple sources.

**Domain:** [detroitchow.com](https://detroitchow.com)

## Overview

DetroitChow helps users discover restaurants and food markets across Metro Detroit by combining data from multiple sources including legacy data, OpenStreetMap, and future integrations with Google Places, Yelp, and social media platforms.

## Technology Stack

- **Database:** PostgreSQL (schema: `detroitchow`)
- **Backend (Planned):** Spring Boot (Java) with RESTful API
- **Frontend (Pending):** TBD (React, Vue, or similar)
- **Data Collection:** Python scripts for data aggregation
- **Target Platforms:** Web (responsive), Android, iOS

## Prerequisites

- PostgreSQL 12+ installed and running
- Python 3.8+ with `pip`
- Git

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

### 3. Python Environment

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
│   └── schema/
│       └── schema.sql              # Database schema with triggers
├── data/
│   ├── legacy/                     # Original 15-year-old dataset
│   ├── imports/                    # Generated SQL import files
│   └── osm-raw/                    # OpenStreetMap query results
├── docs/                           # Documentation
│   ├── DATABASE.md                 # Database documentation
│   └── DATA_COLLECTION.md          # Data collection guide
├── scripts/
│   └── data-collect/               # Data collection scripts
├── venv/                           # Python virtual environment
├── CLAUDE.md                       # AI assistant instructions
└── README.md                       # This file
```

## Current Status

**Completed:**
- Database schema design and implementation
- Initial data migration from legacy JSON (538 locations)
- OpenStreetMap data collection scripts
- Extensive OSM data collection for Metro Detroit

**In Progress:**
- Technology stack finalization
- Development environment setup

**Planned:**
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
