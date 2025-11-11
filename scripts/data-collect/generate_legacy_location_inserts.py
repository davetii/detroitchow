#!/usr/bin/env python3
"""
DetroitChow Location Data Importer
Converts JSON restaurant data to SQL INSERT statements
"""

import json
import sys


def escape_sql_string(value):
    """
    Safely escape a string value for SQL insertion.
    Handles NULL values, empty strings, and single quote escaping.
    """
    if value is None or value == '':
        return 'NULL'
    
    # Convert to string and escape single quotes by doubling them
    escaped = str(value).replace("'", "''")
    return f"'{escaped}'"


def format_insert_statement(record, record_num):
    """
    Generate a SQL INSERT statement from a JSON record.
    
    Args:
        record: Dictionary containing restaurant data
        record_num: Record number for error reporting
    
    Returns:
        SQL INSERT statement as a string
    """
    try:
        # Extract and map fields from JSON to SQL columns
        location_id = record.get('storeId')
        location_name = record.get('name')
        address1 = record.get('address')
        city = record.get('city')
        region = record.get('state')
        phone1 = record.get('phone')
        website = record.get('website')
        facebook = record.get('facebook')
        twitter = record.get('twitter')
        lat = record.get('lat')
        lng = record.get('lng')
        zip = record.get('zip')
        
        # Build the INSERT statement
        sql = f"""INSERT INTO detroitchow.locations (locationid, name, address1, city, region, phone1, website, facebook, twitter, lat, lng, zip) VALUES ({escape_sql_string(location_id)}, {escape_sql_string(location_name)}, {escape_sql_string(address1)}, {escape_sql_string(city)}, {escape_sql_string(region)}, {escape_sql_string(phone1)}, {escape_sql_string(website)}, {escape_sql_string(facebook)}, {escape_sql_string(twitter)}, {escape_sql_string(lat)}, {escape_sql_string(lng)}, {escape_sql_string(zip)});"""
        return sql
        
    except Exception as e:
        print(f"Error processing record #{record_num}: {e}", file=sys.stderr)
        print(f"Record data: {record}", file=sys.stderr)
        raise


def main():
    """Main execution function."""
    
    # Validate command-line arguments
    if len(sys.argv) != 3:
        print("Usage: python generate_location_inserts.py <input_json_file> <output_sql_file>", file=sys.stderr)
        print("Example: python generate_location_inserts.py restaurants.json import_locations.sql", file=sys.stderr)
        sys.exit(1)
    
    input_file = sys.argv[1]
    output_file = sys.argv[2]
    
    try:
        # Read and parse the JSON input file
        print(f"Reading JSON data from: {input_file}")
        with open(input_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        # Validate that we have an array
        if not isinstance(data, list):
            print("Error: JSON file must contain an array at the top level", file=sys.stderr)
            sys.exit(1)
        
        print(f"Found {len(data)} records to process")
        
        # Generate SQL INSERT statements
        print(f"Generating SQL INSERT statements...")
        sql_statements = []
        
        for idx, record in enumerate(data, start=1):
            sql = format_insert_statement(record, idx)
            sql_statements.append(sql)
            
            # Progress indicator for large datasets
            if idx % 100 == 0:
                print(f"  Processed {idx}/{len(data)} records...")
        
        # Write to output file
        print(f"Writing SQL statements to: {output_file}")
        with open(output_file, 'w', encoding='utf-8') as f:
            # Add header comment
            f.write("-- DetroitChow Location Import SQL\n")
            f.write(f"-- Generated from: {input_file}\n")
            f.write(f"-- Total records: {len(data)}\n")
            f.write("-- \n\n")
            
            # Write all INSERT statements
            for sql in sql_statements:
                f.write(sql)
                f.write("\n")  # Extra newline between statements for readability
        
        print(f"✓ Successfully generated {len(sql_statements)} INSERT statements")
        print(f"✓ Output written to: {output_file}")
        print(f"\nYou can now run: psql -d your_database -f {output_file}")
        
    except FileNotFoundError:
        print(f"Error: Input file '{input_file}' not found", file=sys.stderr)
        sys.exit(1)
    except json.JSONDecodeError as e:
        print(f"Error: Invalid JSON in input file: {e}", file=sys.stderr)
        sys.exit(1)
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()