CREATE TABLE google_places (
    id SERIAL PRIMARY KEY,
    locationid VARCHAR(50) NOT NULL,
    place_id VARCHAR(500) NOT NULL,
    premise_place_id VARCHAR(500) ,
    store_place_id VARCHAR(500) ,
    lat VARCHAR(50),
    lon VARCHAR(50),
    phone1 VARCHAR(500),
    phone2 VARCHAR(500),
    formatted_address VARCHAR(1000),
    website VARCHAR(1000),
    google_url VARCHAR(1000),
    business_status VARCHAR(50),
    txtsearch_json jsonb,
    detail_json jsonb,
    store_json jsonb,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


alter table detroitchow.google_places add columns premise_place_id VARCHAR(500), store_place_id VARCHAR(500) 
alter table detroitchow.google_places add column store_json jsonb