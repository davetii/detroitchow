# Data Collection Documentation

## Overview

DetroitChow aggregates restaurant data from multiple sources. This document covers data collection scripts, data source information, and import procedures.

## Current Data Sources

### 1. Legacy Data (IMPORTED)

**Source:** Original DetroitChow.com from approximately 15 years ago

**Status:** Fully imported to database

**Details:**
- **Files:**
  - `data/legacy/GetStores-response-indented.json`
  - `data/legacy/get.json`
- **Import SQL:** `data/imports/detroitchow_legacy_imports.sql`
- **Records:** 538 restaurants
- **Coverage:** Metro Detroit area
- **Data Quality:** High - manually curated data
- **Unique Features:** Contains Yelp reviews (not yet migrated to schema)

**Verification:**
```sql
SELECT COUNT(*) FROM detroitchow.locations;  -- Should return 538
```

### 2. OpenStreetMap Data (COLLECTED, not yet imported)

**Source:** OpenStreetMap via Overpass API

**Status:** Data collected, awaiting import to database

**Details:**
- **County-level data:** Stored in `data/osm-raw/`
- **City-level data:** Stored in `scripts/data-collect/`
- **Coverage:** Extensive Metro Detroit coverage
- **Data Format:** JSON files (one per query)
- **ID Format:** `osm-{element_type}{osm_id}` (e.g., `osm-n123456`)

**Counties Collected:**
- Macomb County
- Oakland County
- Wayne County
- (Additional counties as needed)

## Data Collection Scripts

All scripts located in `scripts/data-collect/`

### Python Environment Setup

**Create virtual environment:**
```bash
python -m venv venv
```

**Activate virtual environment:**
```bash
# Linux/Mac
source venv/bin/activate

# Windows
venv\Scripts\activate
```

**Install dependencies:**
```bash
pip install requests
```

### OpenStreetMap Scripts

#### get_restaurants_by_city.py

Queries OpenStreetMap for restaurants in a specific city.

**Usage:**
```bash
cd scripts/data-collect
python get_restaurants_by_city.py 'Michigan' 'Sterling Heights'
```

**Parameters:**
1. State name (e.g., `'Michigan'`)
2. City name (e.g., `'Sterling Heights'`)

**Output:**
- File: `{City}_{State}_restaurants.json`
- Format: JSON array of restaurant objects
- Location: Current directory (`scripts/data-collect/`)

**Example Output File:** `Sterling_Heights_Michigan_restaurants.json`

**What it does:**
1. Constructs Overpass API query for the specified city
2. Queries for nodes, ways, and relations tagged as restaurants or food establishments
3. Extracts relevant fields: name, address, coordinates, phone, website, cuisine
4. Generates `locationid` as `osm-{type}{id}`
5. Saves results to JSON file

**API Limits:**
- Overpass API has rate limits (check current status)
- Large cities may take longer to process
- Consider breaking large queries into smaller areas

#### get_restaurants_by_county.py

Queries OpenStreetMap for restaurants in Metro Detroit counties.

**Usage:**
```bash
cd scripts/data-collect
python get_restaurants_by_county.py
```

**Parameters:** None (hardcoded for Metro Detroit counties)

**Output:**
- File: `{county_name}_restaurants.json`
- Format: JSON array of restaurant objects
- Location: Current directory (`scripts/data-collect/`)

**Example Output Files:**
- `macomb_county_restaurants.json`
- `oakland_county_restaurants.json`
- `wayne_county_restaurants.json`

**What it does:**
1. Iterates through predefined list of counties
2. Constructs Overpass API query for each county
3. Extracts restaurant data with all available fields
4. Generates `locationid` as `osm-{type}{id}`
5. Saves results to separate JSON files per county

#### Legacy Script: osm_restaurant_importer.py

**Status:** Legacy script (superseded by `get_restaurants_by_county.py`)

**Note:** This script was the original implementation. Use `get_restaurants_by_county.py` instead.

### Script Output Format

All scripts output JSON in the following structure:

```json
[
  {
    "locationid": "osm-n123456789",
    "name": "Restaurant Name",
    "address1": "123 Main St",
    "city": "Detroit",
    "region": "Michigan",
    "country": "United States",
    "lat": "42.331427",
    "lng": "-83.045754",
    "phone1": "(313) 555-0100",
    "website": "https://example.com",
    "cuisine": "Italian",
    "amenity": "restaurant"
  }
]
```

**Field Mapping to Database:**
- `locationid` → `locationid` (VARCHAR)
- `name` → `name`
- `address1` → `address1`
- `city` → `city`
- `region` → `region`
- `country` → `country`
- `lat` → `lat` (VARCHAR)
- `lng` → `lng` (VARCHAR)
- `phone1` → `phone1`
- `website` → `website`
- `cuisine` → Can be added to `tags` table
- `amenity` → Can be added to `tags` table

## Data Import Process

### Manual Review (Required)

Before importing OSM data to the database:

1. **Review JSON files** for data quality
2. **Check for duplicates** against existing locations
3. **Validate coordinates** are within expected range
4. **Verify addresses** are complete
5. **Check website URLs** are well-formed

### Import to Database

**Option 1: Generate SQL INSERT statements**

Create a Python script to convert JSON to SQL:

```python
import json

with open('Sterling_Heights_Michigan_restaurants.json', 'r') as f:
    restaurants = json.load(f)

with open('import.sql', 'w') as out:
    for r in restaurants:
        out.write(f"""
        INSERT INTO detroitchow.locations
        (locationid, name, address1, city, region, country, lat, lng, phone1, website, status)
        VALUES
        ('{r['locationid']}', '{r['name']}', '{r.get('address1', '')}',
         '{r.get('city', '')}', '{r.get('region', '')}', '{r.get('country', '')}',
         '{r.get('lat', '')}', '{r.get('lng', '')}', '{r.get('phone1', '')}',
         '{r.get('website', '')}', 'active');
        """)
```

**Important:** Use parameterized queries in production code, not string concatenation.

**Option 2: Use COPY with CSV**

Convert JSON to CSV, then use PostgreSQL COPY command for faster imports.

**Option 3: Backend API import endpoint**

Once the Spring Boot backend is built, create an admin endpoint to handle bulk imports.

## Duplicate Detection

When importing data from multiple sources:

### Strategy 1: Name + Location Proximity

```sql
-- Find potential duplicates within 100 meters
SELECT
    l1.locationid,
    l1.name,
    l2.locationid,
    l2.name,
    ST_Distance(
        ST_MakePoint(l1.lng::double precision, l1.lat::double precision)::geography,
        ST_MakePoint(l2.lng::double precision, l2.lat::double precision)::geography
    ) as distance_meters
FROM detroitchow.locations l1
JOIN detroitchow.locations l2
    ON l1.name ILIKE l2.name
    AND l1.locationid < l2.locationid
WHERE ST_Distance(
    ST_MakePoint(l1.lng::double precision, l1.lat::double precision)::geography,
    ST_MakePoint(l2.lng::double precision, l2.lat::double precision)::geography
) < 100;
```

**Note:** Requires PostGIS extension.

### Strategy 2: Exact Address Match

```sql
-- Find duplicates by address
SELECT address1, city, COUNT(*)
FROM detroitchow.locations
GROUP BY address1, city
HAVING COUNT(*) > 1;
```

### Strategy 3: Manual Review

Export potential duplicates to CSV and review manually before import.

## Future Data Sources

### Google Places API

**Planned Integration**

**Benefits:**
- Comprehensive business information
- User reviews and ratings
- Photos
- Popular times
- Current operating status

**Implementation:**
- Use place_id as part of locationid: `google-{place_id}`
- Map fields to schema
- Handle rate limits (standard: 1000 requests/day free tier)

**API Endpoint:**
```
https://maps.googleapis.com/maps/api/place/nearbysearch/json
```

### Yelp Fusion API

**Planned Integration**

**Benefits:**
- Reviews and ratings
- Photos
- Price range
- Popular dishes
- Business attributes

**Implementation:**
- Use Yelp business_id: `yelp-{business_id}`
- Map fields to schema
- Handle rate limits (5000 requests/day free tier)

**API Endpoint:**
```
https://api.yelp.com/v3/businesses/search
```

### Social Media APIs

**Facebook, Instagram, Twitter**

**Planned Integration**

**Benefits:**
- Real-time posts and updates
- Event information
- Customer engagement metrics
- Photos and videos

**Implementation:**
- Store social media URLs in `locations` table
- Fetch posts/content dynamically (don't store)
- Cache for performance
- Respect API rate limits

### Manual Submissions

**User-Generated Content**

**Planned Feature**

**Benefits:**
- Community-driven data
- Local knowledge
- Quick updates for new businesses

**Implementation:**
- Web form for submissions
- Admin review queue
- Validation and verification process
- Generate locationid: `manual-{uuid}`

## Data Quality Guidelines

### Required Fields for Import

Minimum required fields for a location:
- `locationid` (unique)
- `name`
- `city`
- `status` (default: `active`)

### Recommended Fields

For better user experience:
- `address1`
- `lat` / `lng`
- `phone1`
- `website`

### Data Validation Checklist

Before importing:
- [ ] `locationid` is unique
- [ ] `name` is not empty
- [ ] `lat` is between -90 and 90 (if provided)
- [ ] `lng` is between -180 and 180 (if provided)
- [ ] `phone1` format is reasonable
- [ ] `website` URL is well-formed (starts with http/https)
- [ ] Social media URLs match expected domain patterns
- [ ] `status` is one of: `active`, `temporarily_closed`, `permanently_closed`

## Overpass API Reference

### Query Structure

```
[out:json][timeout:25];
area["name"="City Name"]["admin_level"="8"]->.searchArea;
(
  node["amenity"~"restaurant|cafe|fast_food|bar|pub"](area.searchArea);
  way["amenity"~"restaurant|cafe|fast_food|bar|pub"](area.searchArea);
  relation["amenity"~"restaurant|cafe|fast_food|bar|pub"](area.searchArea);
);
out body;
>;
out skel qt;
```

### Useful Tags

- `amenity=restaurant` - Sit-down restaurant
- `amenity=cafe` - Coffee shop or cafe
- `amenity=fast_food` - Fast food restaurant
- `amenity=bar` - Bar or pub
- `cuisine=*` - Type of cuisine (pizza, italian, mexican, etc.)
- `name=*` - Business name
- `addr:street`, `addr:housenumber`, `addr:city` - Address components
- `phone` - Phone number
- `website` - Website URL
- `opening_hours` - Operating hours

### API Limits

- Default timeout: 180 seconds
- Can be adjusted with `[timeout:300]`
- Rate limits apply (check current status)
- Large queries may fail - consider breaking into smaller areas

## Data Storage Locations

```
detroitchow/
├── data/
│   ├── legacy/                         # Original DetroitChow data
│   │   ├── GetStores-response-indented.json
│   │   └── get.json
│   ├── imports/                        # Generated SQL import files
│   │   └── detroitchow_legacy_imports.sql
│   └── osm-raw/                        # County-level OSM data
│       ├── macomb_county_restaurants.json
│       ├── oakland_county_restaurants.json
│       └── wayne_county_restaurants.json
└── scripts/
    └── data-collect/                   # City-level OSM data & scripts
        ├── get_restaurants_by_city.py
        ├── get_restaurants_by_county.py
        ├── osm_restaurant_importer.py  # Legacy
        └── *.json                      # City-specific output files
```

## Best Practices

1. **Always review data before import** - Don't blindly import large datasets
2. **Check for duplicates** - Compare against existing data
3. **Validate coordinates** - Ensure they fall within expected geographic bounds
4. **Preserve raw data** - Keep original JSON files for reference
5. **Document data sources** - Track where each location came from
6. **Handle rate limits** - Respect API usage limits
7. **Use version control** - Commit scripts but not large data files
8. **Test imports on staging** - Don't import directly to production

## Troubleshooting

### Script Errors

**"ModuleNotFoundError: No module named 'requests'"**
```bash
pip install requests
```

**"ConnectionError" or "Timeout"**
- Overpass API may be overloaded
- Check https://overpass-api.de/api/status
- Try again later or use different Overpass instance

**"Empty results"**
- Check city/county name spelling
- Verify area exists in OpenStreetMap
- Try broader search terms

### Data Quality Issues

**Missing coordinates**
- Some OSM entries lack lat/lng
- Consider geocoding service for missing coordinates

**Inconsistent phone formats**
- Normalize in application code
- Consider using libphonenumber library

**Duplicate entries from different sources**
- Implement duplicate detection before import
- Consider merge strategy for conflicting data

## Next Steps

1. **Review collected OSM data** in `data/osm-raw/` and `scripts/data-collect/`
2. **Implement duplicate detection** before import
3. **Create import script** to load OSM data to database
4. **Set up Google Places API** integration
5. **Set up Yelp API** integration
6. **Build admin interface** for managing imports
7. **Implement data validation** in backend API
