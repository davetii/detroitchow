-- ============================================================================
-- DetroitChow Database Schema
-- PostgreSQL DDL
-- Database: detroitchow
-- Schema: detroitchow
-- ============================================================================

-- Create the schema
CREATE SCHEMA IF NOT EXISTS detroitchow;

-- Set search path so you don't have to prefix table names
SET search_path TO detroitchow, public;

-- Create ENUM type in the schema
CREATE TYPE detroitchow.location_status AS ENUM (
    'active', 
    'temporarily_closed', 
    'permanently_closed'
);

-- ============================================================================
-- TABLES
-- ============================================================================

-- Sites table (referenced by links, so create first)
CREATE TABLE detroitchow.sites (
    site VARCHAR(100) NOT NULL,
    link VARCHAR(2000),
    icon VARCHAR(2000),
    create_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    create_user VARCHAR(100),
    updated_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    update_user VARCHAR(100),
    PRIMARY KEY (site)
);

-- Locations table
CREATE TABLE detroitchow.locations (
    locationid VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    status detroitchow.location_status DEFAULT 'active',
    address1 VARCHAR(500),
    address2 VARCHAR(500),
    city VARCHAR(100),
    locality VARCHAR(100),
    zip VARCHAR(100),
    region VARCHAR(100),
    country VARCHAR(2),
    phone1 VARCHAR(20),
    phone2 VARCHAR(20),
    lat VARCHAR(100),
    lng VARCHAR(100),
    website VARCHAR(2000),
    facebook VARCHAR(2000),
    twitter VARCHAR(2000),
    instagram VARCHAR(2000),
    opentable VARCHAR(2000),
    tripadvisor VARCHAR(2000),
    yelp VARCHAR(2000),
    create_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    create_user VARCHAR(100),
    updated_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    update_user VARCHAR(100),
    PRIMARY KEY (locationid)
);

-- Tags table
CREATE TABLE detroitchow.tags (
    locationid VARCHAR(50) NOT NULL,
    tag VARCHAR(100) NOT NULL,
    create_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    create_user VARCHAR(100),
    updated_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    update_user VARCHAR(100),
    PRIMARY KEY (locationid, tag),
    CONSTRAINT fk_tags_location FOREIGN KEY (locationid) 
        REFERENCES detroitchow.locations(locationid) ON DELETE CASCADE
);

-- Menus table
CREATE TABLE detroitchow.menus (
    locationid VARCHAR(50) NOT NULL,
    menu_link VARCHAR(2000) NOT NULL,
    image VARCHAR(2000),
    priority INT DEFAULT 0,
    create_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    create_user VARCHAR(100),
    updated_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    update_user VARCHAR(100),
    PRIMARY KEY (locationid, menu_link),
    CONSTRAINT fk_menus_location FOREIGN KEY (locationid) 
        REFERENCES detroitchow.locations(locationid) ON DELETE CASCADE
);

-- Links table
CREATE TABLE detroitchow.links (
    locationid VARCHAR(50) NOT NULL,
    link VARCHAR(2000) NOT NULL,
    link_type VARCHAR(50) NOT NULL,
    site VARCHAR(100),
    icon VARCHAR(2000),
    create_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    create_user VARCHAR(100),
    updated_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    update_user VARCHAR(100),
    PRIMARY KEY (locationid, link),
    CONSTRAINT fk_links_location FOREIGN KEY (locationid) 
        REFERENCES detroitchow.locations(locationid) ON DELETE CASCADE,
    CONSTRAINT fk_links_site FOREIGN KEY (site) 
        REFERENCES detroitchow.sites(site) ON DELETE SET NULL
);

-- Location hours table
CREATE TABLE detroitchow.location_hours (
    locationid VARCHAR(50) NOT NULL,
    line_no INT NOT NULL,
    text VARCHAR(2000),
    create_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    create_user VARCHAR(100),
    updated_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    update_user VARCHAR(100),
    PRIMARY KEY (locationid, line_no),
    CONSTRAINT fk_location_hours_location FOREIGN KEY (locationid) 
        REFERENCES detroitchow.locations(locationid) ON DELETE CASCADE
);

-- ============================================================================
-- INDEXES
-- ============================================================================

-- Locations indexes
CREATE INDEX idx_locations_status ON detroitchow.locations(status);
CREATE INDEX idx_locations_city ON detroitchow.locations(city);
CREATE INDEX idx_locations_status_city ON detroitchow.locations(status, city);
CREATE INDEX idx_locations_locality ON detroitchow.locations(locality);
CREATE INDEX idx_locations_region ON detroitchow.locations(region);
CREATE INDEX idx_locations_country ON detroitchow.locations(country);

-- Links indexes
CREATE INDEX idx_links_link_type ON detroitchow.links(link_type);

-- ============================================================================
-- TRIGGERS FOR AUDIT COLUMNS
-- ============================================================================

-- Function to update audit columns (in schema)
CREATE OR REPLACE FUNCTION detroitchow.update_audit_columns()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        NEW.create_date = CURRENT_TIMESTAMP;
        NEW.create_user = CURRENT_USER;
        NEW.updated_date = CURRENT_TIMESTAMP;
        NEW.update_user = CURRENT_USER;
    ELSIF (TG_OP = 'UPDATE') THEN
        NEW.create_date = OLD.create_date;
        NEW.create_user = OLD.create_user;
        NEW.updated_date = CURRENT_TIMESTAMP;
        NEW.update_user = CURRENT_USER;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply triggers to all tables
CREATE TRIGGER trigger_locations_audit
    BEFORE INSERT OR UPDATE ON detroitchow.locations
    FOR EACH ROW
    EXECUTE FUNCTION detroitchow.update_audit_columns();

CREATE TRIGGER trigger_tags_audit
    BEFORE INSERT OR UPDATE ON detroitchow.tags
    FOR EACH ROW
    EXECUTE FUNCTION detroitchow.update_audit_columns();

CREATE TRIGGER trigger_menus_audit
    BEFORE INSERT OR UPDATE ON detroitchow.menus
    FOR EACH ROW
    EXECUTE FUNCTION detroitchow.update_audit_columns();

CREATE TRIGGER trigger_links_audit
    BEFORE INSERT OR UPDATE ON detroitchow.links
    FOR EACH ROW
    EXECUTE FUNCTION detroitchow.update_audit_columns();

CREATE TRIGGER trigger_sites_audit
    BEFORE INSERT OR UPDATE ON detroitchow.sites
    FOR EACH ROW
    EXECUTE FUNCTION detroitchow.update_audit_columns();

CREATE TRIGGER trigger_location_hours_audit
    BEFORE INSERT OR UPDATE ON detroitchow.location_hours
    FOR EACH ROW
    EXECUTE FUNCTION detroitchow.update_audit_columns();

-- ============================================================================
-- GRANT PERMISSIONS (adjust as needed)
-- ============================================================================

-- Grant usage on schema to your application user
-- GRANT USAGE ON SCHEMA detroitchow TO your_app_user;
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA detroitchow TO your_app_user;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA detroitchow TO your_app_user;

-- ============================================================================
-- COMMENTS
-- ============================================================================

COMMENT ON SCHEMA detroitchow IS 'DetroitChow application schema';
COMMENT ON TABLE detroitchow.locations IS 'Stores restaurants and specialty food stores';
COMMENT ON TABLE detroitchow.tags IS 'Tags applied to locations for categorization';
COMMENT ON TABLE detroitchow.menus IS 'Menu links and images for locations';
COMMENT ON TABLE detroitchow.links IS 'General links (videos, articles, etc.) for locations';
COMMENT ON TABLE detroitchow.sites IS 'Reference data for site icons and links';
COMMENT ON TABLE detroitchow.location_hours IS 'Operating hours text for locations';

COMMENT ON COLUMN detroitchow.locations.locality IS 'County or equivalent administrative division';
COMMENT ON COLUMN detroitchow.locations.region IS 'State/Province';
COMMENT ON COLUMN detroitchow.locations.country IS 'Two-letter country code';
COMMENT ON COLUMN detroitchow.menus.priority IS 'Display order for menus (lower = higher priority)';
COMMENT ON COLUMN detroitchow.links.link_type IS 'Type of link: video, html, article, image';