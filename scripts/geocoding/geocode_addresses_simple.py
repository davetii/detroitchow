#!/usr/bin/env python3
"""
Geocode addresses using Google Geocoding API (no external dependencies)

Uses only Python standard library (urllib, json) - no requests library needed.

Usage:
    python geocode_addresses_simple.py addresses.txt YOUR_API_KEY
"""

import sys
import time
import json
import csv
import urllib.request
import urllib.parse
from typing import List, Dict, Optional

def geocode_address(address: str, api_key: str) -> Optional[Dict]:
    """Geocode a single address using Google Geocoding API"""
    base_url = "https://maps.googleapis.com/maps/api/geocode/json"

    params = urllib.parse.urlencode({
        'address': address,
        'key': api_key
    })

    url = f"{base_url}?{params}"

    try:
        with urllib.request.urlopen(url) as response:
            data = json.loads(response.read().decode())

        if data['status'] == 'OK' and len(data['results']) > 0:
            result = data['results'][0]
            location = result['geometry']['location']

            return {
                'input_address': address,
                'formatted_address': result['formatted_address'],
                'lat': str(location['lat']),
                'lng': str(location['lng']),
                'location_type': result['geometry']['location_type'],
                'status': 'success'
            }
        else:
            return {
                'input_address': address,
                'formatted_address': None,
                'lat': None,
                'lng': None,
                'location_type': None,
                'status': f'failed: {data["status"]}'
            }
    except Exception as e:
        return {
            'input_address': address,
            'formatted_address': None,
            'lat': None,
            'lng': None,
            'location_type': None,
            'status': f'error: {str(e)}'
        }

def main():
    if len(sys.argv) < 3:
        print("Usage: python geocode_addresses_simple.py <input_file> <api_key>")
        print("\nExample:")
        print("  python geocode_addresses_simple.py addresses.txt YOUR_API_KEY")
        sys.exit(1)

    input_file = sys.argv[1]
    api_key = sys.argv[2]

    # Read addresses
    try:
        with open(input_file, 'r') as f:
            addresses = [line.strip() for line in f if line.strip()]
    except FileNotFoundError:
        print(f"Error: File '{input_file}' not found")
        sys.exit(1)

    total = len(addresses)
    print(f"Geocoding {total} addresses...\n")

    results = []
    success_count = 0

    for i, address in enumerate(addresses, 1):
        print(f"[{i}/{total}] {address}")
        result = geocode_address(address, api_key)
        results.append(result)

        if result['status'] == 'success':
            print(f"  ✓ Lat: {result['lat']}, Lng: {result['lng']}")
            print(f"  → {result['formatted_address']}\n")
            success_count += 1
        else:
            print(f"  ✗ {result['status']}\n")

        # Rate limiting
        if i < total:
            time.sleep(0.1)

    # Save results
    output_file = input_file.rsplit('.', 1)[0] + '_geocoded.json'
    with open(output_file, 'w') as f:
        json.dump(results, f, indent=2)

    # Summary
    print("="*60)
    print("SUMMARY")
    print("="*60)
    print(f"Total addresses: {total}")
    print(f"Successful:      {success_count} ({success_count/total*100:.1f}%)")
    print(f"Failed:          {total - success_count} ({(total-success_count)/total*100:.1f}%)")
    print(f"\nResults saved to: {output_file}")
    print("="*60)

if __name__ == '__main__':
    main()
