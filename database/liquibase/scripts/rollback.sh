#!/bin/bash
# Rollback database migrations
# Usage: ./rollback.sh <count>
# Example: ./rollback.sh 1  (rolls back the last changeset)

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LIQUIBASE_DIR="$(dirname "$SCRIPT_DIR")"

cd "$LIQUIBASE_DIR"

if [ -z "$1" ]; then
    echo "Usage: $0 <count>"
    echo "Example: $0 1  (rollback last changeset)"
    exit 1
fi

ROLLBACK_COUNT=$1

echo "======================================"
echo "DetroitChow Database Rollback"
echo "Rolling back $ROLLBACK_COUNT changeset(s)"
echo "======================================"
echo ""

read -p "Are you sure you want to rollback $ROLLBACK_COUNT changeset(s)? (yes/no): " CONFIRM
if [ "$CONFIRM" != "yes" ]; then
    echo "Rollback cancelled."
    exit 0
fi

if [ ! -f "liquibase.local.properties" ]; then
    echo "ERROR: liquibase.local.properties not found!"
    exit 1
fi

mvn liquibase:rollback -Plocalif [ $? -eq 0 ]; then
    echo ""
    echo "✓ Rollback completed successfully!"
else
    echo ""
    echo "✗ Rollback failed!"
    exit 1
fi
