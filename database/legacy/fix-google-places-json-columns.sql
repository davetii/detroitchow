-- Fix google_places table JSON column types
-- Convert json columns to jsonb to match JPA entity expectations
-- Run this against your PostgreSQL database with:
-- psql -U detroitchow_owner -h narwhal -d detroitchow -f database/fix-google-places-json-columns.sql

-- Convert txtsearch_json from json to jsonb
ALTER TABLE detroitchow.google_places
  ALTER COLUMN txtsearch_json TYPE jsonb USING txtsearch_json::jsonb;

-- Convert detail_json from json to jsonb
ALTER TABLE detroitchow.google_places
  ALTER COLUMN detail_json TYPE jsonb USING detail_json::jsonb;

-- Convert store_json from json to jsonb
ALTER TABLE detroitchow.google_places
  ALTER COLUMN store_json TYPE jsonb USING store_json::jsonb;

-- Verify the changes
\d detroitchow.google_places
