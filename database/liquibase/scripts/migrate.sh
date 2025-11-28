#!/bin/bash
# DetroitChow Database Migration Script
# Applies all pending Liquibase migrations to the database

set -e  # Exit on error

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LIQUIBASE_DIR="$(dirname "$SCRIPT_DIR")"

cd "$LIQUIBASE_DIR"

echo "======================================"
echo "DetroitChow Database Migration"
echo "======================================"
echo ""

# Check if liquibase.local.properties exists
if [ ! -f "liquibase.local.properties" ]; then
    echo "ERROR: liquibase.local.properties not found!"
    echo "Please copy liquibase.properties to liquibase.local.properties and configure your database connection."
    exit 1
fi

echo "Running migrations using Maven..."
echo ""

# Run Liquibase update via Maven
mvn liquibase:update -Plocal

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ Migrations completed successfully!"
else
    echo ""
    echo "✗ Migration failed!"
    exit 1
fi
