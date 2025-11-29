#!/usr/bin/env python3
"""
Geocode addresses using Google Geocoding API

This script reads addresses from a text file (one per line) and outputs
their latitude and longitude coordinates.

Input file format (addresses.txt):
    35351 23 Mile Rd, New Baltimore, MI
    123 Main St, Detroit, MI
    456 Woodward Ave, Royal Oak, MI

Output formats:
    - JSON file with detailed results
    - CSV file with address, lat, lng
    - Console output

Usage:
    python geocode_addresses.py addresses.txt YOUR_API_KEY
    python geocode_addresses.py addresses.txt YOUR_API_KEY --output results.json
    python geocode_addresses.py addresses.txt YOUR_API_KEY --format csv
"""

import sys
import time
import json
import csv
import requests
from typing import List, Dict, Optional

def geocode_address(address: str, api_key: str) -> Optional[Dict]:
    """
    Geocode a single address using Google Geocoding API

    Args:
        address: The address to geocode
        api_key: Google Maps API key

    Returns:
        Dictionary with geocoding results or None if failed
    """
    base_url = "https://maps.googleapis.com/maps/api/geocode/json"

    params = {
        'address': address,
        'key': api_key
    }

    try:
        response = requests.get(base_url, params=params)
        response.raise_for_status()
        data = response.json()

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

def geocode_addresses_from_file(filename: str, api_key: str, delay: float = 0.1) -> List[Dict]:
    """
    Geocode multiple addresses from a file

    Args:
        filename: Path to file with addresses (one per line)
        api_key: Google Maps API key
        delay: Delay between requests in seconds (default 0.1 = 10 requests/sec)

    Returns:
        List of geocoding results
    """
    results = []

    try:
        with open(filename, 'r') as f:
            addresses = [line.strip() for line in f if line.strip()]
    except FileNotFoundError:
        print(f"Error: File '{filename}' not found")
        sys.exit(1)

    total = len(addresses)
    print(f"Geocoding {total} addresses...")

    for i, address in enumerate(addresses, 1):
        print(f"[{i}/{total}] Geocoding: {address}")
        result = geocode_address(address, api_key)
        results.append(result)

        if result['status'] == 'success':
            print(f"  ✓ {result['lat']}, {result['lng']}")
        else:
            print(f"  ✗ {result['status']}")

        # Rate limiting - be nice to the API
        if i < total:
            time.sleep(delay)

    return results

def save_results_json(results: List[Dict], output_file: str):
    """Save results to JSON file"""
    with open(output_file, 'w') as f:
        json.dump(results, f, indent=2)
    print(f"\nResults saved to: {output_file}")

def save_results_csv(results: List[Dict], output_file: str):
    """Save results to CSV file"""
    with open(output_file, 'w', newline='') as f:
        writer = csv.DictWriter(f, fieldnames=[
            'input_address', 'formatted_address', 'lat', 'lng', 'location_type', 'status'
        ])
        writer.writeheader()
        writer.writerows(results)
    print(f"\nResults saved to: {output_file}")

def print_summary(results: List[Dict]):
    """Print summary statistics"""
    total = len(results)
    success = sum(1 for r in results if r['status'] == 'success')
    failed = total - success

    print("\n" + "="*60)
    print("SUMMARY")
    print("="*60)
    print(f"Total addresses: {total}")
    print(f"Successful:      {success} ({success/total*100:.1f}%)")
    print(f"Failed:          {failed} ({failed/total*100:.1f}%)")
    print("="*60)

def main():
    if len(sys.argv) < 3:
        print("Usage: python geocode_addresses.py <input_file> <api_key> [--output <file>] [--format json|csv]")
        print("\nExample:")
        print("  python geocode_addresses.py addresses.txt YOUR_API_KEY")
        print("  python geocode_addresses.py addresses.txt YOUR_API_KEY --output results.json")
        print("  python geocode_addresses.py addresses.txt YOUR_API_KEY --format csv")
        sys.exit(1)

    input_file = sys.argv[1]
    api_key = sys.argv[2]

    # Parse optional arguments
    output_file = None
    output_format = 'json'

    for i, arg in enumerate(sys.argv):
        if arg == '--output' and i + 1 < len(sys.argv):
            output_file = sys.argv[i + 1]
        if arg == '--format' and i + 1 < len(sys.argv):
            output_format = sys.argv[i + 1]

    # Default output filename
    if output_file is None:
        base_name = input_file.rsplit('.', 1)[0]
        output_file = f"{base_name}_geocoded.{output_format}"

    # Geocode all addresses
    results = geocode_addresses_from_file(input_file, api_key)

    # Save results
    if output_format == 'csv':
        save_results_csv(results, output_file)
    else:
        save_results_json(results, output_file)

    # Print summary
    print_summary(results)

if __name__ == '__main__':
    main()
