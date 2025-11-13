#!/usr/bin/env python3
import sys
import os
import csv

def main():
    # Validate arguments
    if len(sys.argv) != 3:
        print("Usage: python script.py <input_file> <output_file>")
        sys.exit(1)
    
    input_file = sys.argv[1]
    output_file = sys.argv[2]
    
    # Check if input file exists
    if not os.path.exists(input_file):
        print(f"Error: Input file '{input_file}' not found")
        sys.exit(1)
    
    # Delete output file if it exists
    if os.path.exists(output_file):
        os.remove(output_file)
    
    try:
        # Read input file and generate INSERT statements
        insert_statements = []
        
        with open(input_file, 'r') as infile:
            reader = csv.reader(infile)
            for row in reader:
                # Skip empty rows
                if not row or len(row) < 2:
                    continue
                
                locationid = row[0].strip()
                menu_link = row[1].strip()
                
                # Create INSERT statement with single quotes
                insert_stmt = f"insert into detroitchow.menus(locationid, menu_link) values ('{locationid}', '{menu_link}');"
                insert_statements.append(insert_stmt)
        
        # Write INSERT statements to output file
        with open(output_file, 'w') as outfile:
            for stmt in insert_statements:
                outfile.write(stmt + '\n')
        
        print(f"Successfully created '{output_file}' with {len(insert_statements)} INSERT statements")
    
    except Exception as e:
        print(f"Error processing files: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()