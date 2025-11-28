#!/bin/bash
# Check the status of database migrations
# Shows which changesets have been applied and which are pending

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LIQUIBASE_DIR="$(dirname "$SCRIPT_DIR")"

cd "$LIQUIBASE_DIR"

echo "======================================"
echo "DetroitChow Migration Status"
echo "======================================"
echo ""

if [ ! -f "liquibase.local.properties" ]; then
    echo "ERROR: liquibase.local.properties not found!"
    exit 1
fi

mvn liquibase:status -Plocal
