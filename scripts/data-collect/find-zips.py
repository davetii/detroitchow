#!/usr/bin/env python3
"""
Script to find missing zip codes using Google Address Validation API.
Generates SQL UPDATE statements for detroitchow.locations table.

Usage: python find-zips.py <google_api_key> <input.csv> <output.sql> <errors.log>

Requirements:
  - Install: pip install requests
"""

import sys
import csv
import requests
import time
import os


def main():
    # Validate arguments
    if len(sys.argv) != 5:
        print("Usage: python find-zips.py <google_api_key> <input.csv> <output.sql> <errors.log>")
        sys.exit(1)
    
    api_key = sys.argv[1]
    input_file = sys.argv[2]
    output_file = sys.argv[3]
    error_file = sys.argv[4]
    
    print("=" * 70)
    print("DetroitChow Zip Code Update Script")
    print("=" * 70)
    print(f"Input file:  {input_file}")
    print(f"Output file: {output_file}")
    print(f"Error file:  {error_file}")
    print()
    
    # Check if input file exists
    if not os.path.exists(input_file):
        print(f"ERROR: Input file '{input_file}' not found")
        sys.exit(1)
    
    # Delete output files if they exist
    if os.path.exists(output_file):
        os.remove(output_file)
        print(f"Deleted existing output file: {output_file}")
    
    if os.path.exists(error_file):
        os.remove(error_file)
        print(f"Deleted existing error file: {error_file}")
    
    print()
    print("Processing records...")
    print("-" * 70)
    
    # Process the input file
    success_count = 0
    error_count = 0
    
    with open(input_file, 'r', encoding='utf-8') as infile, \
         open(output_file, 'w', encoding='utf-8') as outfile, \
         open(error_file, 'w', encoding='utf-8') as errfile:
        
        reader = csv.reader(infile)
        
        for row_num, row in enumerate(reader, 1):
            if len(row) < 4:
                error_msg = f"Row {row_num}: Invalid row (less than 4 fields): {row}"
                print(f"ERROR: {error_msg}")
                errfile.write(error_msg + '\n')
                error_count += 1
                continue
            
            # Extract fields and remove quotes
            location_id = row[0].strip('"')
            street_address = row[1].strip('"')
            city = row[2].strip('"')
            state = row[3].strip('"')
            
            print(f"  LocationID: {location_id}, Address: {street_address}, {city}, {state}")
            
            # Call Google Address Validation API
            zip_code = get_zip_code(api_key, street_address, city, state)
            
            if zip_code:
                update_stmt = f"update detroitchow.locations set zip = '{zip_code}' where locationid = '{location_id}';"
                outfile.write(update_stmt + '\n')
                print(f"✓ LocationID {location_id:5s} -> Zip: {zip_code}")
                success_count += 1
            else:
                error_msg = f"Row {row_num}: Failed to get zip code for LocationID={location_id}, Address={street_address}, {city}, {state}"
                print(f"✗ LocationID {location_id:5s} -> ERROR")
                errfile.write(error_msg + '\n')
                error_count += 1
            
            # Rate limiting delay (1 second between API calls)
            time.sleep(1)
    
    # Print summary
    print("-" * 70)
    print()
    print("Processing complete:")
    print(f"  ✓ Successful: {success_count}")
    print(f"  ✗ Errors:     {error_count}")
    print(f"  Total:        {success_count + error_count}")
    print()
    print(f"Output file: {output_file}")
    print(f"Error file:  {error_file}")
    print("=" * 70)


def get_zip_code(api_key, street_address, city, state):
    """
    Call Google Address Validation API to get zip code.
    
    Args:
        api_key: Google API key
        street_address: Street address
        city: City name
        state: State abbreviation (e.g., 'MI')
    
    Returns:
        5-digit zip code or None if not found
    """
    url = "https://addressvalidation.googleapis.com/v1:validateAddress"
    
    # Build address object for API
    address = {
        "address": {
            "addressLines": [street_address],
            "administrativeArea": state,
            "locality": city,
            "regionCode": "US"
        }
    }
    
    params = {"key": api_key}
    
    try:
        response = requests.post(url, json=address, params=params, timeout=10)
        response.raise_for_status()
        
        data = response.json()
        
        # Try to extract postal code from response
        # First try: postalAddress.postalCode
        if 'result' in data and 'address' in data['result']:
            address_data = data['result']['address']
            
            # Try postalAddress first
            if 'postalAddress' in address_data:
                postal_code = address_data['postalAddress'].get('postalCode', '')
                if postal_code:
                    # Return only first 5 digits (zip code without +4)
                    return postal_code[:5]
            
            # Try uspsData as fallback
            if 'uspsData' in data['result']:
                usps_data = data['result']['uspsData']
                if 'standardizedAddress' in usps_data:
                    zip_code = usps_data['standardizedAddress'].get('zipCode', '')
                    if zip_code:
                        return zip_code[:5]
        
        return None
    
    except requests.exceptions.Timeout:
        print(f"  API Timeout for {street_address}, {city}, {state}")
        return None
    except requests.exceptions.RequestException as e:
        print(f"  API Error for {street_address}, {city}, {state}: {str(e)}")
        return None
    except Exception as e:
        print(f"  Unexpected error for {street_address}, {city}, {state}: {str(e)}")
        return None


if __name__ == "__main__":
    main()