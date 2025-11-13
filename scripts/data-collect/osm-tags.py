#!/usr/bin/env python3
"""
Script to generate SQL insert statements for detroitchow.tags table.
Reads a CSV input file with locationid and tag(s), outputs SQL insert statements.

Usage: python3 generate_tags_insert.py <input_file> <output_file>
"""

import sys
import os
import csv


def main():
    # Validate arguments
    if len(sys.argv) != 3:
        print("Error: Expected 2 arguments")
        print("Usage: python3 generate_tags_insert.py <input_file> <output_file>")
        sys.exit(1)
    
    input_file = sys.argv[1]
    output_file = sys.argv[2]
    
    # Check if input file exists
    if not os.path.isfile(input_file):
        print(f"Error: Input file '{input_file}' does not exist")
        sys.exit(1)
    
    # Delete output file if it exists
    if os.path.isfile(output_file):
        os.remove(output_file)
    
    # Process the input file and generate insert statements
    insert_statements = []
    
    try:
        with open(input_file, 'r', newline='', encoding='utf-8') as infile:
            reader = csv.reader(infile)
            
            for row in reader:
                if len(row) < 2:
                    continue  # Skip malformed rows
                
                locationid = row[0].strip().strip('"')
                tag_string = row[1].strip().strip('"')
                
                # Skip if tag is NULL or "NULL"
                if tag_string.upper() == "NULL" or tag_string == "":
                    continue
                
                # Split tags by semicolon
                tags = [tag.strip() for tag in tag_string.split(';')]
                
                # Generate insert statement for each tag
                for tag in tags:
                    if tag:  # Only process non-empty tags
                        insert_stmt = f"insert into detroitchow.tags(locationid, tag) values ('{locationid}', '{tag}');"
                        insert_statements.append(insert_stmt)
    
    except Exception as e:
        print(f"Error reading input file: {e}")
        sys.exit(1)
    
    # Write insert statements to output file
    try:
        with open(output_file, 'w', encoding='utf-8') as outfile:
            for stmt in insert_statements:
                outfile.write(stmt + '\n')
        
        print(f"Success! Generated {len(insert_statements)} insert statements")
        print(f"Output written to: {output_file}")
    
    except Exception as e:
        print(f"Error writing output file: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()