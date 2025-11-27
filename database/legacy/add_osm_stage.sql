-- ============================================================================
-- DetroitChow Database Schema
-- PostgreSQL DDL
-- Database: detroitchow
-- Schema: detroitchow
-- ============================================================================


-- ============================================================================
-- TABLES
-- ============================================================================


-- osm_locations_stage
CREATE TABLE detroitchow.osm_locations_stage (
    osm_id VARCHAR(150) NOT NULL,
    name VARCHAR(255) NOT NULL,
    housenumber VARCHAR(500),
    street VARCHAR(500),
    city VARCHAR(100),
    locality VARCHAR(100),
    state VARCHAR(100),
    postcode VARCHAR(100),
    country VARCHAR(100),
    amenity VARCHAR(2000),
    cuisine VARCHAR(2000),
    phone VARCHAR(20),
    lat VARCHAR(100),
    lng VARCHAR(100),
    opening_hours  VARCHAR(2000),
    website VARCHAR(2000),
    menu_url VARCHAR(2000),
    email VARCHAR(2000),
    facebook VARCHAR(2000),
    twitter VARCHAR(2000),
    instagram VARCHAR(2000),
    PRIMARY KEY (osm_id)
);


alter table detroitchow.osm_locations_stage add column usage_status varchar(50);

alter table detroitchow.osm_locations_stage ALTER COLUMN usage_status type varchar(50);