# TODO

Active work items, blockers, and immediate next steps for DetroitChow development.

**Last Updated**: 2025-11-10

---

## In Progress
- [ ] Parse OSM Conetent and add to main dataset
- [ ] Design Data collection approach
- [ ] Finalize backend framework decision (Spring Boot vs alternatives)
- [ ] Finalize frontend framework decision (React/Vue/Next.js)
- [ ] Define mobile app strategy (Flutter vs React Native vs Native)

---

## Up Next (Prioritized)

### Documentation Phase
- [ ] Review and validate OSM data quality in collected JSON files
- [ ] Document data import/merge strategy for OSM data
- [ ] Define API endpoint structure (draft OpenAPI spec?)
- [ ] Create data deduplication strategy document

### Data Phase
- [ ] Import OSM data to database (after review)
- [ ] Write data merge script for handling duplicate locations
- [ ] Validate legacy data quality (phone formats, URLs, coordinates)

### Technical Setup
- [ ] Set up backend project structure (after framework decision)
- [ ] Configure development database (Docker or local?)
- [ ] Set up CI/CD pipeline basics (GitHub Actions?)
- [ ] Choose hosting platform (AWS/GCP/Azure/Heroku)

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

- [x] Database schema design and implementation
- [x] Legacy data import (538 restaurants)
- [x] OSM data collection scripts
- [x] Core documentation (DATABASE.md, DATA_COLLECTION.md, CLAUDE.md)
- [x] Project structure and Python environment setup
- [x] CHANGELOG.md and DECISIONS.md created

---

## Notes

- Keep this file focused on actionable items
- Strategic features belong in ROADMAP.md
- Completed work gets documented in CHANGELOG.md
- Technical decisions and rationale go in DECISIONS.md
- Review and update this file regularly (weekly/bi-weekly)
