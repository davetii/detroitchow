alter table detroitchow.locations add column contact_text VARCHAR(2000);

insert into locations
(
locationid,
    name,
    address1,
    city,
	region, 
    zip,
    country,
    phone1,
    lat,
    lng,
	hours,
    website,
	contact_text,
    facebook,
    twitter,
    instagram
)
select  (ROW_NUMBER() OVER ()) + 600 as row_num, 
    name,
    housenumber || ' ' || street,
    city,
    state,
    postcode,
    country,
    phone,
    lat,
    lng,
    opening_hours,
    website,
    email,
    facebook,
    twitter,
    instagram
from detroitchow.osm_locations_stage
where usage_status in ('TARGET1', 'MISSING_CITIES_IMPORT');

-- added 1178 rows
-- new total 1716 




