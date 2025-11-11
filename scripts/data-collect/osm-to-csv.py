#!/usr/bin/env python3
"""
Convert Overpass API JSON output to CSV format.
Parses JSON elements and maps OSM fields to CSV columns.

Usage:
  Single file mode:  python osm_to_csv.py <input.json> <output.csv>
  Directory mode:    python osm_to_csv.py <output.csv>
                     (processes all .json files in current directory)
"""

import json
import csv
import sys
import time
import glob
from pathlib import Path


def get_nested_field(obj, field_path):
    """
    Extract a value from a nested object using dot notation.
    
    Args:
        obj: The object to extract from
        field_path: Path to field using dots for traversal (e.g., "center.lat")
    
    Returns:
        The value if found, empty string if not found or any traversal fails
    """
    if not isinstance(obj, dict):
        return ""
    
    parts = field_path.split(".")
    current = obj
    
    for part in parts:
        if isinstance(current, dict) and part in current:
            current = current[part]
        else:
            return ""
    
    return str(current) if current is not None else ""


def get_tag_field(tags, field_name):
    """
    Extract a value from the tags dictionary.
    Handles field names with colons (e.g., "addr:city").
    
    Args:
        tags: The tags dictionary
        field_name: The field name to extract
    
    Returns:
        The value if found, empty string if not found
    """
    if not isinstance(tags, dict):
        return ""
    
    value = tags.get(field_name)
    return str(value) if value is not None else ""


def get_phone(tags):
    """Get phone number with priority: contact:phone, then phone"""
    phone = get_tag_field(tags, "contact:phone")
    if phone:
        return phone
    return get_tag_field(tags, "phone")


def get_website(tags):
    """Get website with priority: website, then contact:website, then url"""
    website = get_tag_field(tags, "website")
    if website:
        return website
    website = get_tag_field(tags, "contact:website")
    if website:
        return website
    return get_tag_field(tags, "url")


def get_menu_url(tags):
    """Get menu URL with priority: menu:url, then website:menu"""
    menu_url = get_tag_field(tags, "menu:url")
    if menu_url:
        return menu_url
    return get_tag_field(tags, "website:menu")


def extract_row(element):
    """
    Extract a CSV row from an OSM element.
    
    Args:
        element: A single element from the Overpass API JSON
    
    Returns:
        A dictionary with CSV field names and values, or None if element should be skipped
    """
    # Only process node and way types
    element_type = element.get("type", "")
    if element_type not in ("node", "way"):
        return None
    
    tags = element.get("tags", {})
    
    # Extract lat/lng based on element type
    if element_type == "node":
        lat = get_nested_field(element, "lat")
        lng = get_nested_field(element, "lon")
    else:  # way
        lat = get_nested_field(element, "center.lat")
        lng = get_nested_field(element, "center.lon")
    
    row = {
        "osm_id": str(element.get("id", "")),
        "name": get_tag_field(tags, "name"),
        "housenumber": get_tag_field(tags, "addr:housenumber"),
        "street": get_tag_field(tags, "addr:street"),
        "city": get_tag_field(tags, "addr:city"),
        "state": get_tag_field(tags, "addr:state"),
        "postcode": get_tag_field(tags, "addr:postcode"),
        "country": get_tag_field(tags, "addr:country"),
        "amenity": get_tag_field(tags, "amenity"),
        "cuisine": get_tag_field(tags, "cuisine"),
        "phone": get_phone(tags),
        "lat": lat,
        "lng": lng,
        "opening_hours": get_tag_field(tags, "opening_hours"),
        "website": get_website(tags),
        "email": get_tag_field(tags, "email"),
        "facebook": get_tag_field(tags, "contact:facebook"),
        "twitter": get_tag_field(tags, "contact:twitter"),
        "instagram": get_tag_field(tags, "contact:instagram"),
        "menu_url": get_menu_url(tags),
    }
    
    return row


def convert_json_to_csv(input_file, output_file, append_mode=False):
    """
    Convert Overpass API JSON file to CSV format.
    
    Args:
        input_file: Path to input JSON file
        output_file: Path to output CSV file
        append_mode: If True, append to existing CSV file; if False, overwrite
    
    Returns:
        Tuple of (rows_written, rows_skipped)
    
    Raises:
        FileNotFoundError: If input file doesn't exist
        json.JSONDecodeError: If input file is not valid JSON
    """
    input_path = Path(input_file)
    output_path = Path(output_file)
    
    if not input_path.exists():
        raise FileNotFoundError(f"Input file not found: {input_path}")
    
    # Read JSON file
    try:
        with open(input_path, 'r', encoding='utf-8') as f:
            data = json.load(f)
    except json.JSONDecodeError as e:
        raise json.JSONDecodeError(f"Invalid JSON in {input_path}: {e.msg}", e.doc, e.pos)
    
    # Get elements array, default to empty list if not found
    elements = data.get("elements", [])
    
    if not isinstance(elements, list):
        print("Warning: 'elements' field is not an array, processing as empty", file=sys.stderr)
        elements = []
    
    # Field names in order for CSV output
    fieldnames = [
        "osm_id", "name", "housenumber", "street", "city", "state", 
        "postcode", "country", "amenity", "cuisine", "phone", "lat", "lng",
        "opening_hours", "website", "email", "facebook", "twitter", 
        "instagram", "menu_url"
    ]
    
    # Write CSV file
    rows_written = 0
    rows_skipped = 0
    
    # Determine file mode
    file_mode = 'a' if append_mode else 'w'
    
    try:
        with open(output_path, file_mode, newline='', encoding='utf-8') as f:
            writer = csv.DictWriter(f, fieldnames=fieldnames)
            
            for element in elements:
                try:
                    row = extract_row(element)
                    if row is not None:
                        writer.writerow(row)
                        rows_written += 1
                    else:
                        rows_skipped += 1
                except Exception as e:
                    print(f"Warning: Error processing element: {e}", file=sys.stderr)
                    rows_skipped += 1
    
    except IOError as e:
        raise IOError(f"Error writing to output file {output_path}: {e}")
    
    return rows_written, rows_skipped


def process_directory_mode(output_file):
    """
    Process all .json files in the current directory and append results to output_file.
    
    Args:
        output_file: Path to the output CSV file
    
    Returns:
        Tuple of (total_rows_written, total_rows_skipped, files_processed)
    """
    output_path = Path(output_file)
    
    # Delete output file if it exists
    if output_path.exists():
        output_path.unlink()
    
    # Find all .json files in current directory
    json_files = sorted(glob.glob("*.json"))
    
    if not json_files:
        print("Warning: No .json files found in current directory", file=sys.stderr)
        return 0, 0, 0
    
    total_rows_written = 0
    total_rows_skipped = 0
    files_processed = 0
    
    for json_file in json_files:
        try:
            rows_written, rows_skipped = convert_json_to_csv(json_file, output_file, append_mode=True)
            total_rows_written += rows_written
            total_rows_skipped += rows_skipped
            files_processed += 1
        except Exception as e:
            print(f"Error processing {json_file}: {e}", file=sys.stderr)
    
    return total_rows_written, total_rows_skipped, files_processed


def process_single_file_mode(input_file, output_file):
    """
    Process a single JSON file and write to output_file.
    
    Args:
        input_file: Path to the input JSON file
        output_file: Path to the output CSV file
    
    Returns:
        Tuple of (rows_written, rows_skipped)
    """
    output_path = Path(output_file)
    
    # Delete output file if it exists
    if output_path.exists():
        output_path.unlink()
    
    return convert_json_to_csv(input_file, output_file, append_mode=False)


def main():
    """Main entry point"""
    start_time = time.time()
    
    if len(sys.argv) == 2:
        # Directory mode: python osm_to_csv.py <output.csv>
        output_file = sys.argv[1]
        
        try:
            total_rows_written, total_rows_skipped, files_processed = process_directory_mode(output_file)
            elapsed_time = time.time() - start_time
            print(f"Script complete. Processed {files_processed} files, {total_rows_written} rows written, "
                  f"{total_rows_skipped} rows skipped. Time: {elapsed_time:.2f}s")
        except Exception as e:
            print(f"Error: {e}", file=sys.stderr)
            sys.exit(1)
    
    elif len(sys.argv) == 3:
        # Single file mode: python osm_to_csv.py <input.json> <output.csv>
        input_file = sys.argv[1]
        output_file = sys.argv[2]
        
        try:
            rows_written, rows_skipped = process_single_file_mode(input_file, output_file)
            elapsed_time = time.time() - start_time
            print(f"Script complete. {rows_written} rows written, {rows_skipped} rows skipped. Time: {elapsed_time:.2f}s")
        except Exception as e:
            print(f"Error: {e}", file=sys.stderr)
            sys.exit(1)
    
    else:
        print("Usage:", file=sys.stderr)
        print("  Single file mode:  python osm_to_csv.py <input.json> <output.csv>", file=sys.stderr)
        print("  Directory mode:    python osm_to_csv.py <output.csv>", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()