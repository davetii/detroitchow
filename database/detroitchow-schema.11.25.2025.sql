--
-- PostgreSQL database dump
--

\restrict b5MLXG9f5kGr5Fre9Nbj2q1Kqso51GkRYIWbcIajfgfnj1ByGfCCfHFHv8j73tH

-- Dumped from database version 16.10
-- Dumped by pg_dump version 16.10

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: detroitchow; Type: SCHEMA; Schema: -; Owner: detroitchow_owner
--

CREATE SCHEMA detroitchow;


ALTER SCHEMA detroitchow OWNER TO detroitchow_owner;

--
-- Name: SCHEMA detroitchow; Type: COMMENT; Schema: -; Owner: detroitchow_owner
--

COMMENT ON SCHEMA detroitchow IS 'DetroitChow application schema';


--
-- Name: fuzzystrmatch; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS fuzzystrmatch WITH SCHEMA detroitchow;


--
-- Name: EXTENSION fuzzystrmatch; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION fuzzystrmatch IS 'determine similarities and distance between strings';


--
-- Name: pg_trgm; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pg_trgm WITH SCHEMA detroitchow;


--
-- Name: EXTENSION pg_trgm; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pg_trgm IS 'text similarity measurement and index searching based on trigrams';


--
-- Name: location_status; Type: TYPE; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE TYPE detroitchow.location_status AS ENUM (
    'active',
    'temporarily_closed',
    'permanently_closed'
);


ALTER TYPE detroitchow.location_status OWNER TO detroitchow_owner;

--
-- Name: update_audit_columns(); Type: FUNCTION; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE FUNCTION detroitchow.update_audit_columns() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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
$$;


ALTER FUNCTION detroitchow.update_audit_columns() OWNER TO detroitchow_owner;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: google_places; Type: TABLE; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE TABLE detroitchow.google_places (
    id integer NOT NULL,
    locationid character varying(50) NOT NULL,
    place_id character varying(500) NOT NULL,
    lat character varying(50),
    lng character varying(50),
    phone1 character varying(500),
    phone2 character varying(500),
    formatted_address character varying(1000),
    website character varying(1000),
    google_url character varying(1000),
    business_status character varying(50),
    txtsearch_json jsonb,
    detail_json jsonb,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    premise_place_id character varying(500),
    store_place_id character varying(500),
    store_json jsonb
);


ALTER TABLE detroitchow.google_places OWNER TO detroitchow_owner;

--
-- Name: google_places_id_seq; Type: SEQUENCE; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE SEQUENCE detroitchow.google_places_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE detroitchow.google_places_id_seq OWNER TO detroitchow_owner;

--
-- Name: google_places_id_seq; Type: SEQUENCE OWNED BY; Schema: detroitchow; Owner: detroitchow_owner
--

ALTER SEQUENCE detroitchow.google_places_id_seq OWNED BY detroitchow.google_places.id;


--
-- Name: links; Type: TABLE; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE TABLE detroitchow.links (
    locationid character varying(50) NOT NULL,
    link character varying(2000) NOT NULL,
    link_type character varying(50) NOT NULL,
    site character varying(100),
    icon character varying(2000),
    create_date timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    create_user character varying(100),
    updated_date timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    update_user character varying(100)
);


ALTER TABLE detroitchow.links OWNER TO detroitchow_owner;

--
-- Name: TABLE links; Type: COMMENT; Schema: detroitchow; Owner: detroitchow_owner
--

COMMENT ON TABLE detroitchow.links IS 'General links (videos, articles, etc.) for locations';


--
-- Name: COLUMN links.link_type; Type: COMMENT; Schema: detroitchow; Owner: detroitchow_owner
--

COMMENT ON COLUMN detroitchow.links.link_type IS 'Type of link: video, html, article, image';


--
-- Name: location_hours; Type: TABLE; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE TABLE detroitchow.location_hours (
    locationid character varying(50) NOT NULL,
    line_no integer NOT NULL,
    text character varying(2000),
    create_date timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    create_user character varying(100),
    updated_date timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    update_user character varying(100)
);


ALTER TABLE detroitchow.location_hours OWNER TO detroitchow_owner;

--
-- Name: TABLE location_hours; Type: COMMENT; Schema: detroitchow; Owner: detroitchow_owner
--

COMMENT ON TABLE detroitchow.location_hours IS 'Operating hours text for locations';


--
-- Name: locations; Type: TABLE; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE TABLE detroitchow.locations (
    locationid character varying(50) NOT NULL,
    name character varying(255) NOT NULL,
    description character varying(2000),
    status detroitchow.location_status DEFAULT 'active'::detroitchow.location_status,
    address1 character varying(500),
    address2 character varying(500),
    city character varying(100),
    locality character varying(100),
    zip character varying(100),
    region character varying(100),
    country character varying(2),
    phone1 character varying(20),
    phone2 character varying(20),
    lat character varying(100),
    lng character varying(100),
    website character varying(2000),
    facebook character varying(2000),
    twitter character varying(2000),
    instagram character varying(2000),
    opentable character varying(2000),
    tripadvisor character varying(2000),
    yelp character varying(2000),
    create_date timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    create_user character varying(100),
    updated_date timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    update_user character varying(100),
    hours character varying(2000),
    contact_text character varying(2000)
);


ALTER TABLE detroitchow.locations OWNER TO detroitchow_owner;

--
-- Name: TABLE locations; Type: COMMENT; Schema: detroitchow; Owner: detroitchow_owner
--

COMMENT ON TABLE detroitchow.locations IS 'Stores restaurants and specialty food stores';


--
-- Name: COLUMN locations.locality; Type: COMMENT; Schema: detroitchow; Owner: detroitchow_owner
--

COMMENT ON COLUMN detroitchow.locations.locality IS 'County or equivalent administrative division';


--
-- Name: COLUMN locations.region; Type: COMMENT; Schema: detroitchow; Owner: detroitchow_owner
--

COMMENT ON COLUMN detroitchow.locations.region IS 'State/Province';


--
-- Name: COLUMN locations.country; Type: COMMENT; Schema: detroitchow; Owner: detroitchow_owner
--

COMMENT ON COLUMN detroitchow.locations.country IS 'Two-letter country code';


--
-- Name: menus; Type: TABLE; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE TABLE detroitchow.menus (
    locationid character varying(50) NOT NULL,
    menu_link character varying(2000) NOT NULL,
    image character varying(2000),
    priority integer DEFAULT 0,
    create_date timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    create_user character varying(100),
    updated_date timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    update_user character varying(100),
    descr character varying(500)
);


ALTER TABLE detroitchow.menus OWNER TO detroitchow_owner;

--
-- Name: TABLE menus; Type: COMMENT; Schema: detroitchow; Owner: detroitchow_owner
--

COMMENT ON TABLE detroitchow.menus IS 'Menu links and images for locations';


--
-- Name: COLUMN menus.priority; Type: COMMENT; Schema: detroitchow; Owner: detroitchow_owner
--

COMMENT ON COLUMN detroitchow.menus.priority IS 'Display order for menus (lower = higher priority)';


--
-- Name: osm_locations_stage; Type: TABLE; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE TABLE detroitchow.osm_locations_stage (
    osm_id character varying(150) NOT NULL,
    name character varying(255) NOT NULL,
    housenumber character varying(500),
    street character varying(500),
    city character varying(100),
    locality character varying(100),
    state character varying(100),
    postcode character varying(100),
    country character varying(100),
    amenity character varying(2000),
    cuisine character varying(2000),
    phone character varying(20),
    lat character varying(100),
    lng character varying(100),
    opening_hours character varying(2000),
    website character varying(2000),
    menu_url character varying(2000),
    email character varying(2000),
    facebook character varying(2000),
    twitter character varying(2000),
    instagram character varying(2000),
    usage_status character varying(50)
);


ALTER TABLE detroitchow.osm_locations_stage OWNER TO detroitchow_owner;

--
-- Name: sites; Type: TABLE; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE TABLE detroitchow.sites (
    site character varying(100) NOT NULL,
    link character varying(2000),
    icon character varying(2000),
    create_date timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    create_user character varying(100),
    updated_date timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    update_user character varying(100)
);


ALTER TABLE detroitchow.sites OWNER TO detroitchow_owner;

--
-- Name: TABLE sites; Type: COMMENT; Schema: detroitchow; Owner: detroitchow_owner
--

COMMENT ON TABLE detroitchow.sites IS 'Reference data for site icons and links';


--
-- Name: tags; Type: TABLE; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE TABLE detroitchow.tags (
    locationid character varying(50) NOT NULL,
    tag character varying(100) NOT NULL,
    create_date timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    create_user character varying(100),
    updated_date timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    update_user character varying(100)
);


ALTER TABLE detroitchow.tags OWNER TO detroitchow_owner;

--
-- Name: TABLE tags; Type: COMMENT; Schema: detroitchow; Owner: detroitchow_owner
--

COMMENT ON TABLE detroitchow.tags IS 'Tags applied to locations for categorization';


--
-- Name: google_places id; Type: DEFAULT; Schema: detroitchow; Owner: detroitchow_owner
--

ALTER TABLE ONLY detroitchow.google_places ALTER COLUMN id SET DEFAULT nextval('detroitchow.google_places_id_seq'::regclass);


--
-- Name: google_places google_places_pkey; Type: CONSTRAINT; Schema: detroitchow; Owner: detroitchow_owner
--

ALTER TABLE ONLY detroitchow.google_places
    ADD CONSTRAINT google_places_pkey PRIMARY KEY (id);


--
-- Name: links links_pkey; Type: CONSTRAINT; Schema: detroitchow; Owner: detroitchow_owner
--

ALTER TABLE ONLY detroitchow.links
    ADD CONSTRAINT links_pkey PRIMARY KEY (locationid, link);


--
-- Name: location_hours location_hours_pkey; Type: CONSTRAINT; Schema: detroitchow; Owner: detroitchow_owner
--

ALTER TABLE ONLY detroitchow.location_hours
    ADD CONSTRAINT location_hours_pkey PRIMARY KEY (locationid, line_no);


--
-- Name: locations locations_pkey; Type: CONSTRAINT; Schema: detroitchow; Owner: detroitchow_owner
--

ALTER TABLE ONLY detroitchow.locations
    ADD CONSTRAINT locations_pkey PRIMARY KEY (locationid);


--
-- Name: menus menus_pkey; Type: CONSTRAINT; Schema: detroitchow; Owner: detroitchow_owner
--

ALTER TABLE ONLY detroitchow.menus
    ADD CONSTRAINT menus_pkey PRIMARY KEY (locationid, menu_link);


--
-- Name: osm_locations_stage osm_locations_stage_pkey; Type: CONSTRAINT; Schema: detroitchow; Owner: detroitchow_owner
--

ALTER TABLE ONLY detroitchow.osm_locations_stage
    ADD CONSTRAINT osm_locations_stage_pkey PRIMARY KEY (osm_id);


--
-- Name: sites sites_pkey; Type: CONSTRAINT; Schema: detroitchow; Owner: detroitchow_owner
--

ALTER TABLE ONLY detroitchow.sites
    ADD CONSTRAINT sites_pkey PRIMARY KEY (site);


--
-- Name: tags tags_pkey; Type: CONSTRAINT; Schema: detroitchow; Owner: detroitchow_owner
--

ALTER TABLE ONLY detroitchow.tags
    ADD CONSTRAINT tags_pkey PRIMARY KEY (locationid, tag);


--
-- Name: idx_links_link_type; Type: INDEX; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE INDEX idx_links_link_type ON detroitchow.links USING btree (link_type);


--
-- Name: idx_locations_city; Type: INDEX; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE INDEX idx_locations_city ON detroitchow.locations USING btree (city);


--
-- Name: idx_locations_country; Type: INDEX; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE INDEX idx_locations_country ON detroitchow.locations USING btree (country);


--
-- Name: idx_locations_locality; Type: INDEX; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE INDEX idx_locations_locality ON detroitchow.locations USING btree (locality);


--
-- Name: idx_locations_region; Type: INDEX; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE INDEX idx_locations_region ON detroitchow.locations USING btree (region);


--
-- Name: idx_locations_status; Type: INDEX; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE INDEX idx_locations_status ON detroitchow.locations USING btree (status);


--
-- Name: idx_locations_status_city; Type: INDEX; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE INDEX idx_locations_status_city ON detroitchow.locations USING btree (status, city);


--
-- Name: links trigger_links_audit; Type: TRIGGER; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE TRIGGER trigger_links_audit BEFORE INSERT OR UPDATE ON detroitchow.links FOR EACH ROW EXECUTE FUNCTION detroitchow.update_audit_columns();


--
-- Name: location_hours trigger_location_hours_audit; Type: TRIGGER; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE TRIGGER trigger_location_hours_audit BEFORE INSERT OR UPDATE ON detroitchow.location_hours FOR EACH ROW EXECUTE FUNCTION detroitchow.update_audit_columns();


--
-- Name: locations trigger_locations_audit; Type: TRIGGER; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE TRIGGER trigger_locations_audit BEFORE INSERT OR UPDATE ON detroitchow.locations FOR EACH ROW EXECUTE FUNCTION detroitchow.update_audit_columns();


--
-- Name: menus trigger_menus_audit; Type: TRIGGER; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE TRIGGER trigger_menus_audit BEFORE INSERT OR UPDATE ON detroitchow.menus FOR EACH ROW EXECUTE FUNCTION detroitchow.update_audit_columns();


--
-- Name: sites trigger_sites_audit; Type: TRIGGER; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE TRIGGER trigger_sites_audit BEFORE INSERT OR UPDATE ON detroitchow.sites FOR EACH ROW EXECUTE FUNCTION detroitchow.update_audit_columns();


--
-- Name: tags trigger_tags_audit; Type: TRIGGER; Schema: detroitchow; Owner: detroitchow_owner
--

CREATE TRIGGER trigger_tags_audit BEFORE INSERT OR UPDATE ON detroitchow.tags FOR EACH ROW EXECUTE FUNCTION detroitchow.update_audit_columns();


--
-- Name: links fk_links_location; Type: FK CONSTRAINT; Schema: detroitchow; Owner: detroitchow_owner
--

ALTER TABLE ONLY detroitchow.links
    ADD CONSTRAINT fk_links_location FOREIGN KEY (locationid) REFERENCES detroitchow.locations(locationid) ON DELETE CASCADE;


--
-- Name: links fk_links_site; Type: FK CONSTRAINT; Schema: detroitchow; Owner: detroitchow_owner
--

ALTER TABLE ONLY detroitchow.links
    ADD CONSTRAINT fk_links_site FOREIGN KEY (site) REFERENCES detroitchow.sites(site) ON DELETE SET NULL;


--
-- Name: location_hours fk_location_hours_location; Type: FK CONSTRAINT; Schema: detroitchow; Owner: detroitchow_owner
--

ALTER TABLE ONLY detroitchow.location_hours
    ADD CONSTRAINT fk_location_hours_location FOREIGN KEY (locationid) REFERENCES detroitchow.locations(locationid) ON DELETE CASCADE;


--
-- Name: menus fk_menus_location; Type: FK CONSTRAINT; Schema: detroitchow; Owner: detroitchow_owner
--

ALTER TABLE ONLY detroitchow.menus
    ADD CONSTRAINT fk_menus_location FOREIGN KEY (locationid) REFERENCES detroitchow.locations(locationid) ON DELETE CASCADE;


--
-- Name: tags fk_tags_location; Type: FK CONSTRAINT; Schema: detroitchow; Owner: detroitchow_owner
--

ALTER TABLE ONLY detroitchow.tags
    ADD CONSTRAINT fk_tags_location FOREIGN KEY (locationid) REFERENCES detroitchow.locations(locationid) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

\unrestrict b5MLXG9f5kGr5Fre9Nbj2q1Kqso51GkRYIWbcIajfgfnj1ByGfCCfHFHv8j73tH

