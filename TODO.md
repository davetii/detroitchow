# TODO

Active work items, blockers, and immediate next steps for DetroitChow development.

**Last Updated**: 2025-11-25

---
- [ ] Implement Admin Tool front end
---

## Up Next (Prioritized)
- [ ] technical decision on back end batch processing, data cleansing, job scheduling, etc..

## Major Features
- [ ] Hours Calculator
- [ ] Spec for Consumer Service
- [ ] Implement Consumer Service
- [ ] Planing for DetroitChow Web
- [ ] DetroitChow Map
- [ ] DetroitChow Web Features
- [ ] Planing for DetroitChow Android
- [ ] Planing for DetroitChow Apple
- [ ] Planning for recurring data capture of locations


### Data Phase
- [ ] Validate locations data quality via Google API (phone formats, URLs, coordinates)
- [ ] use Google API to add more locations
- [ ] pop tags
- [ ] Design recurring Data collection approach

### Architecting Phase
- [ ] Document data import/merge strategy for OSM data
- [ ] Define API endpoint structure (draft OpenAPI spec?)
- [ ] Create data deduplication strategy document
- [ ] Finalize frontend framework decision (React/Vue/Next.js)
- [ ] Define mobile app strategy (Flutter vs React Native vs Native)

### Technical Setup
- [ ] Set up backend project structure (after framework decision)
- [ ] Configure development database (Docker or local?)
- [ ] Set up CI/CD pipeline basics (GitHub Actions?)
- [ ] Choose hosting platform (AWS/GCP/Azure/Heroku)

### Batch Data Population Phase


---

## Blocked / Waiting For Decision

- [ ] Authentication approach (blocked on: backend framework decision)
- [ ] API design patterns (blocked on: backend framework decision)
- [ ] Frontend deployment strategy (blocked on: frontend framework decision)
- [ ] Image storage solution (blocked on: hosting platform decision)

---

## Backlog (Low Priority / Future)

- [ ] Design logo and branding
- [ ] Set up Google Places API account
- [ ] Set up Yelp API account
- [ ] Research social media API rate limits (Facebook, Instagram, Twitter)
- [ ] Investigate Mapbox vs Google Maps for frontend
- [ ] Plan SEO strategy
- [ ] Set up analytics (Google Analytics, Plausible, etc.)
- [ ] Create admin interface mockups
- [ ] Research PWA (Progressive Web App) for mobile web experience

---

## Questions / Decisions Needed

- **Backend Framework**: Spring Boot, Node.js/Express, Django/FastAPI, or Go?
- **Frontend Framework**: React, Vue, or Next.js (for SSR/SEO)?
- **Mobile Strategy**: Single codebase (Flutter/React Native) or native apps?
- **Hosting**: Self-hosted vs managed platform? Budget considerations?
- **Database Hosting**: Managed PostgreSQL (RDS, Cloud SQL) or self-hosted?
- **Domain strategy**: Use API subdomain (api.detroitchow.com) or path-based (/api/)?
- **API versioning**: URL-based (/v1/) or header-based?

---

## Ideas / Parking Lot

Things to consider but not prioritized yet:

- User accounts and favorites/saved restaurants
- User-submitted content (reviews, photos, corrections)
- Email notifications for new restaurants or updates
- Weekly email digest of new restaurants
- Integration with reservation systems (OpenTable, Resy)
- Filter by dietary restrictions (vegan, gluten-free, halal, kosher)
- "Open now" filtering based on current time
- Distance/radius search from user location
- Restaurant collections/lists (e.g., "Best Pizza in Detroit", "Date Night Spots")
- Social sharing features
- Dark mode for web/mobile apps

---

## Recently Completed

Move items here when done, then archive to CHANGELOG.md periodically:
- [x] Review and validate OSM data quality in collected JSON files
- [x] Implement Admin Service
- [x] Spec for Admin Service
- [x] 65 OSM with NO Zip code
- [x] dedup locations post OSM
- [x] Merge OSM Content to main data
- [x] Import OSM data to database
- [x] Database schema design and implementation
- [x] Legacy data import (538 restaurants)
- [x] OSM data collection scripts
- [x] Core documentation (DATABASE.md, DATA_COLLECTION.md, CLAUDE.md)
- [x] Project structure and Python environment setup
- [x] CHANGELOG.md and DECISIONS.md created
- [x] Stage OSM Content 
---

## Notes

- Keep this file focused on actionable items
- Strategic features belong in ROADMAP.md
- Completed work gets documented in CHANGELOG.md
- Technical decisions and rationale go in DECISIONS.md
- Review and update this file regularly (weekly/bi-weekly)

