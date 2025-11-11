#!/usr/bin/env python3
"""
Analyzer for Overpass API JSON files.
Parses JSON and reports analysis on the elements array.
Usage: python overpass_analyzer.py <json_file>
"""

import json
import sys
from collections import defaultdict


def get_all_field_paths(obj, prefix=""):
    """
    Recursively get all field paths in an object using dot notation for nested objects.
    Returns a set of field paths.
    
    Args:
        obj: Dictionary or object to extract fields from
        prefix: Current path prefix (for nested objects)
    
    Returns:
        Set of field path strings using dot notation (e.g., "tags.amenity")
    """
    fields = set()
    
    if isinstance(obj, dict):
        for key, value in obj.items():
            # Skip 'type' field as it's already used for grouping
            if prefix == "" and key == "type":
                continue
                
            current_path = f"{prefix}.{key}" if prefix else key
            fields.add(current_path)
            
            # If the value is a dict, recurse into it
            if isinstance(value, dict):
                nested_fields = get_all_field_paths(value, current_path)
                fields.update(nested_fields)
    
    return fields


def analyze_json(filename):
    """
    Analyze a JSON file from Overpass API.
    
    Args:
        filename: Path to the JSON file to analyze
    """
    # Load JSON file
    try:
        with open(filename, 'r') as f:
            data = json.load(f)
    except FileNotFoundError:
        print(f"Error: File '{filename}' not found in current directory")
        sys.exit(1)
    except json.JSONDecodeError as e:
        print(f"Error: File '{filename}' is not valid JSON: {e}")
        sys.exit(1)
    
    # Verify elements array exists
    if "elements" not in data:
        print("Error: No 'elements' array found in JSON")
        sys.exit(1)
    
    elements = data["elements"]
    
    # Report 1: Total objects in elements array
    print(f"Total elements: {len(elements)}\n")
    
    # Group elements by type
    type_groups = defaultdict(list)
    for element in elements:
        elem_type = element.get("type", "unknown")
        type_groups[elem_type].append(element)
    
    # Report 2 & 3: For each type, count and list unique fields
    for elem_type in sorted(type_groups.keys()):
        elements_of_type = type_groups[elem_type]
        count = len(elements_of_type)
        
        print(f"Type: {elem_type}, total count = {count}")
        
        # Collect all unique fields across all objects of this type
        unique_fields = set()
        for element in elements_of_type:
            fields = get_all_field_paths(element)
            unique_fields.update(fields)
        
        # Print unique fields sorted
        for field in sorted(unique_fields):
            print(f"\t{field}")
        
        print()  # Blank line between types


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python overpass_analyzer.py <json_file>")
        print("\nExample: python overpass_analyzer.py restaurants.json")
        sys.exit(1)
    
    filename = sys.argv[1]
    analyze_json(filename)