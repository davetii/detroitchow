-- Check the actual data types of the google_places columns
SELECT
    column_name,
    data_type,
    udt_name,
    is_nullable
FROM information_schema.columns
WHERE table_schema = 'detroitchow'
  AND table_name = 'google_places'
  AND column_name IN ('txtsearch_json', 'detail_json', 'store_json')
ORDER BY column_name;
