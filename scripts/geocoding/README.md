# Address Geocoding Script

Convert addresses to latitude/longitude coordinates using Google Geocoding API.

## Prerequisites

- Python 3.x
- `requests` library: `pip install requests`
- Google Maps API key with Geocoding API enabled

## Setup

1. **Activate Python virtual environment** (if using one):
   ```bash
   source ../../venv/bin/activate  # From scripts/geocoding directory
   ```

2. **Install dependencies**:
   ```bash
   pip install requests
   ```

3. **Create your input file** with addresses (one per line):
   ```
   35351 23 Mile Rd, New Baltimore, MI
   123 Main St, Detroit, MI
   456 Woodward Ave, Royal Oak, MI
   ```

## Usage

### Basic Usage (JSON output)

```bash
python geocode_addresses.py addresses.txt YOUR_API_KEY
```

Output: `addresses_geocoded.json`

### CSV Output

```bash
python geocode_addresses.py addresses.txt YOUR_API_KEY --format csv
```

Output: `addresses_geocoded.csv`

### Custom Output Filename

```bash
python geocode_addresses.py addresses.txt YOUR_API_KEY --output my_results.json
```

### Example with Your API Key

```bash
python geocode_addresses.py addresses.txt AIzaSyCnvmhReeuZ_a_KuZn_vxCuboxHh_Jpwt8
```

## Input File Format

**addresses.txt:**
```
35351 23 Mile Rd, New Baltimore, MI
123 Main St, Detroit, MI
456 Woodward Ave, Royal Oak, MI
```

One address per line. Can include city, state, zip code, etc.

## Output Formats

### JSON Output Example

```json
[
  {
    "input_address": "35351 23 Mile Rd, New Baltimore, MI",
    "formatted_address": "35351 23 Mile Rd, New Baltimore, MI 48047, USA",
    "lat": "42.695123",
    "lng": "-82.737456",
    "location_type": "ROOFTOP",
    "status": "success"
  }
]
```

### CSV Output Example

```csv
input_address,formatted_address,lat,lng,location_type,status
"35351 23 Mile Rd, New Baltimore, MI","35351 23 Mile Rd, New Baltimore, MI 48047, USA",42.695123,-82.737456,ROOFTOP,success
```

## Location Types

- **ROOFTOP**: Most accurate (exact building)
- **RANGE_INTERPOLATED**: Interpolated between two points
- **GEOMETRIC_CENTER**: Center of a location (e.g., street, polygon)
- **APPROXIMATE**: Approximate location

## Rate Limiting

The script includes a 0.1 second delay between requests (10 requests/second) to be respectful to the API. You can adjust this in the code if needed.

## Error Handling

Failed geocoding attempts will show:
- `status: "failed: ZERO_RESULTS"` - Address not found
- `status: "failed: INVALID_REQUEST"` - Malformed request
- `status: "error: <message>"` - Network or other errors

## Google API Pricing

Google Geocoding API pricing (as of 2024):
- First 40,000 requests/month: Free
- Beyond that: $5 per 1,000 requests

Check current pricing: https://developers.google.com/maps/documentation/geocoding/usage-and-billing

## Example Session

```bash
$ python geocode_addresses.py addresses.txt AIzaSyCnvmhReeuZ_a_KuZn_vxCuboxHh_Jpwt8

Geocoding 3 addresses...
[1/3] Geocoding: 35351 23 Mile Rd, New Baltimore, MI
  ✓ 42.695123, -82.737456
[2/3] Geocoding: 123 Main St, Detroit, MI
  ✓ 42.331427, -83.045754
[3/3] Geocoding: 456 Woodward Ave, Royal Oak, MI
  ✓ 42.489353, -83.146043

Results saved to: addresses_geocoded.json

============================================================
SUMMARY
============================================================
Total addresses: 3
Successful:      3 (100.0%)
Failed:          0 (0.0%)
============================================================
```

## Tips

1. **More accurate results**: Include city, state, and zip code
2. **Batch processing**: Add all your addresses to one file
3. **Review results**: Check the `formatted_address` to verify Google found the right place
4. **Location type**: "ROOFTOP" is most accurate, "APPROXIMATE" may need manual verification

## Troubleshooting

**"Error: File 'addresses.txt' not found"**
- Make sure the file exists in the current directory
- Use absolute path: `/full/path/to/addresses.txt`

**"API key not valid"**
- Verify Geocoding API is enabled in Google Cloud Console
- Check API key restrictions (IP or HTTP referrer)

**All requests fail**
- Check internet connection
- Verify API key hasn't hit rate limits or billing issues
