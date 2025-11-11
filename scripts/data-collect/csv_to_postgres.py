#!/usr/bin/env python3
"""
Load CSV data into PostgreSQL database with duplicate key handling.
Gracefully skips duplicate key constraint violations and continues processing.
"""

import csv
import sys
import time
import psycopg2
from psycopg2 import sql, Error
from pathlib import Path


# Database connection parameters
DB_CONFIG = {
    "host": "localhost",
    "port": 5432,
    "user": "detroitchow_owner",
    "password": "test",
    "database": "detroitchow"  # Update this to your actual database name
}

# Table and schema
SCHEMA = "detroitchow"
TABLE = "osm_locations_stage"


def create_connection():
    """
    Create a connection to the PostgreSQL database.
    
    Returns:
        A psycopg2 connection object
    
    Raises:
        psycopg2.Error: If connection fails
    """
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        return conn
    except Error as e:
        raise Error(f"Error connecting to PostgreSQL database: {e}")


def insert_row(conn, row_data):
    """
    Insert a single row into the database.
    
    Args:
        conn: psycopg2 connection object
        row_data: Dictionary with CSV column values
    
    Returns:
        Tuple of (success: bool, error_message: str or None)
    """
    try:
        with conn.cursor() as cur:
            # Map CSV columns to database columns
            columns = [
                "osm_id", "name", "housenumber", "street", "city", "state",
                "postcode", "country", "amenity", "cuisine", "phone", "lat",
                "lng", "opening_hours", "website", "email", "facebook",
                "twitter", "instagram", "menu_url"
            ]
            
            # Build values tuple in the correct order
            values = [row_data.get(col, None) or None for col in columns]
            
            # Build INSERT statement
            placeholders = ",".join(["%s"] * len(columns))
            col_names = ",".join(columns)
            
            insert_query = sql.SQL(
                "INSERT INTO {}.{} ({}) VALUES ({})"
            ).format(
                sql.Identifier(SCHEMA),
                sql.Identifier(TABLE),
                sql.SQL(col_names),
                sql.SQL(placeholders)
            )
            
            cur.execute(insert_query, values)
            conn.commit()
            return True, None
    
    except psycopg2.IntegrityError as e:
        conn.rollback()
        # Check if it's a duplicate key error
        if "unique constraint" in str(e).lower() or "duplicate key" in str(e).lower():
            return False, "DUPLICATE_KEY"
        else:
            return False, str(e)
    
    except Error as e:
        conn.rollback()
        return False, str(e)


def load_csv_to_postgres(csv_file, db_config=None):
    """
    Load CSV file into PostgreSQL database.
    
    Args:
        csv_file: Path to the CSV file
        db_config: Optional database configuration dict (uses defaults if not provided)
    
    Returns:
        Tuple of (rows_inserted, rows_skipped_duplicate, rows_skipped_error)
    """
    if db_config:
        DB_CONFIG.update(db_config)
    
    csv_path = Path(csv_file)
    
    if not csv_path.exists():
        raise FileNotFoundError(f"CSV file not found: {csv_path}")
    
    # Connect to database
    try:
        conn = create_connection()
    except Error as e:
        raise Error(f"Failed to connect to database: {e}")
    
    rows_inserted = 0
    rows_skipped_duplicate = 0
    rows_skipped_error = 0
    total_rows = 0
    
    try:
        with open(csv_path, 'r', encoding='utf-8') as f:
            # Note: CSV has no header row, so we need to map by column order
            fieldnames = [
                "osm_id", "name", "housenumber", "street", "city", "state",
                "postcode", "country", "amenity", "cuisine", "phone", "lat",
                "lng", "opening_hours", "website", "email", "facebook",
                "twitter", "instagram", "menu_url"
            ]
            
            reader = csv.DictReader(f, fieldnames=fieldnames)
            
            for row in reader:
                total_rows += 1
                
                try:
                    # Clean up empty strings to None for NULL values
                    cleaned_row = {k: v if v.strip() else None for k, v in row.items()}
                    
                    success, error_msg = insert_row(conn, cleaned_row)
                    
                    if success:
                        rows_inserted += 1
                    elif error_msg == "DUPLICATE_KEY":
                        rows_skipped_duplicate += 1
                    else:
                        print(f"Warning: Row {total_rows} error: {error_msg}", file=sys.stderr)
                        rows_skipped_error += 1
                
                except Exception as e:
                    print(f"Warning: Error processing row {total_rows}: {e}", file=sys.stderr)
                    rows_skipped_error += 1
        
    except IOError as e:
        raise IOError(f"Error reading CSV file: {e}")
    
    finally:
        conn.close()
    
    return rows_inserted, rows_skipped_duplicate, rows_skipped_error


def main():
    """Main entry point"""
    start_time = time.time()
    
    if len(sys.argv) < 2:
        print("Usage: python csv_to_postgres.py <csv_file> [database_name]", file=sys.stderr)
        sys.exit(1)
    
    csv_file = sys.argv[1]
    
    # Optional: override database name
    if len(sys.argv) >= 3:
        DB_CONFIG["database"] = sys.argv[2]
    
    try:
        rows_inserted, rows_skipped_duplicate, rows_skipped_error = load_csv_to_postgres(csv_file)
        
        elapsed_time = time.time() - start_time
        total_processed = rows_inserted + rows_skipped_duplicate + rows_skipped_error
        
        print(f"Script complete. Processed {total_processed} rows, "
              f"{rows_inserted} inserted, {rows_skipped_duplicate} skipped (duplicates), "
              f"{rows_skipped_error} skipped (errors). Time: {elapsed_time:.2f}s")
    
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()