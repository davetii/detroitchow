# DetroitChow Database Migrations

This directory contains all database schema management for the DetroitChow project using Liquibase.

## Directory Structure

```
liquibase/
├── README.md                          # This file
├── pom.xml                            # Maven configuration for Liquibase plugin
├── liquibase.properties               # Template properties file
├── liquibase.local.properties         # Local configuration (gitignored)
├── changelog/
│   ├── db.changelog-master.yaml       # Master changelog file
│   └── changesets/
│       └── 001-init-schema.yaml       # Initial schema creation
└── scripts/
    ├── migrate.sh                     # Run pending migrations
    ├── status.sh                      # Check migration status
    └── rollback.sh                    # Rollback migrations
```

## Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL 16+ (for production) or H2 (for testing)
- Local `liquibase.local.properties` file configured

## Setup

### 1. Create Local Configuration

Copy the template and configure for your environment:

```bash
cp liquibase.properties liquibase.local.properties
```

Edit `liquibase.local.properties` with your database credentials:

```properties
url=jdbc:postgresql://your-host:5432/detroitchow
username=your_username
password=your_password
```

**Important:** `liquibase.local.properties` is gitignored to prevent credential leakage.

### 2. Verify Setup

Check that Liquibase can connect to your database:

```bash
./scripts/status.sh
```

## Running Migrations

### Apply All Pending Migrations

```bash
./scripts/migrate.sh
```

Or using Maven directly:

```bash
mvn liquibase:update -Plocal
```

### Check Migration Status

See which changesets have been applied and which are pending:

```bash
./scripts/status.sh
```

Or:

```bash
mvn liquibase:status -Plocal
```

### Rollback Migrations

Rollback the last N changesets:

```bash
./scripts/rollback.sh 1  # Rollback last changeset
```

Or using Maven:

```bash
mvn liquibase:rollback -Plocalif you want to rollback to a specific tag:

```bash
mvn liquibase:rollback -Plocalif you want to rollback to a date:

```bash
mvn liquibase:rollback -Plocal
```

## Creating New Migrations

### 1. Create a New Changeset File

Create a new file in `changelog/changesets/`:

```bash
touch changelog/changesets/002-add-users-table.yaml
```

### 2. Define the Changeset

Example changeset structure:

```yaml
databaseChangeLog:
  - changeSet:
      id: 002-create-users-table
      author: your_name
      changes:
        - createTable:
            tableName: users
            schemaName: detroitchow
            columns:
              - column:
                  name: id
                  type: bigint
                  autoIncrement: true
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: username
                  type: varchar(100)
                  constraints:
                    nullable: false
                    unique: true
              - column:
                  name: email
                  type: varchar(255)
                  constraints:
                    nullable: false
              - column:
                  name: created_at
                  type: timestamp with time zone
                  defaultValueComputed: CURRENT_TIMESTAMP
```

### 3. Include in Master Changelog

Add the new changeset to `changelog/db.changelog-master.yaml`:

```yaml
databaseChangeLog:
  - include:
      file: changesets/001-init-schema.yaml
      relativeToChangelogFile: true
  - include:
      file: changesets/002-add-users-table.yaml
      relativeToChangelogFile: true
```

### 4. Test the Migration

```bash
./scripts/status.sh    # Verify changeset is detected
./scripts/migrate.sh   # Apply the migration
```

## Maven Profiles

### Local Profile (default)

Uses credentials from `liquibase.local.properties`:

```bash
mvn liquibase:update -Plocal
```

### Production Profile

Uses environment variables:

```bash
export DB_URL=jdbc:postgresql://prod-host:5432/detroitchow
export DB_USERNAME=prod_user
export DB_PASSWORD=prod_password

mvn liquibase:update -Pprod
```

## Best Practices

### 1. Never Modify Existing Changesets

Once a changeset has been applied to any environment, **never modify it**. Instead, create a new changeset to make changes.

### 2. Use Meaningful IDs

Changeset IDs should be descriptive:
- ✅ `001-create-locations-table`
- ✅ `002-add-google-places-integration`
- ❌ `changeset-1`
- ❌ `update-schema`

### 3. Include Rollback Logic

For complex changes, provide explicit rollback instructions:

```yaml
- changeSet:
    id: 003-add-column
    author: your_name
    changes:
      - addColumn:
          tableName: locations
          columns:
            - column:
                name: rating
                type: decimal(3,2)
    rollback:
      - dropColumn:
          tableName: locations
          columnName: rating
```

### 4. Test Rollbacks

Always test that your rollback works before deploying:

```bash
./scripts/migrate.sh    # Apply migration
./scripts/rollback.sh 1 # Rollback
./scripts/migrate.sh    # Re-apply
```

### 5. Use Database-Specific Changesets When Needed

For PostgreSQL-specific vs H2-specific changes:

```yaml
- changeSet:
    id: 004-create-extension
    author: your_name
    dbms: postgresql
    changes:
      - sql:
          sql: CREATE EXTENSION IF NOT EXISTS pg_trgm;
```

## Troubleshooting

### "Lock table is locked"

If Liquibase was interrupted, you may need to release the lock:

```bash
mvn liquibase:releaseLocks -Plocal
```

### "Checksum mismatch"

If you accidentally modified an applied changeset:

```bash
mvn liquibase:clearCheckSums -Plocal
```

**Warning:** Only use this if you understand the implications.

### Connection refused

Verify your database is running and credentials in `liquibase.local.properties` are correct:

```bash
psql -h narwhal -U detroitchow_owner -d detroitchow
```

## CI/CD Integration

### GitHub Actions Example

```yaml
- name: Run Database Migrations
  run: |
    cd database/liquibase
    mvn liquibase:update -Pprod
  env:
    DB_URL: ${{ secrets.DB_URL }}
    DB_USERNAME: ${{ secrets.DB_USERNAME }}
    DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
```

### Docker Example

```dockerfile
FROM maven:3.9-eclipse-temurin-21-alpine
WORKDIR /migrations
COPY database/liquibase .
CMD ["mvn", "liquibase:update", "-Pprod"]
```

## Additional Resources

- [Liquibase Documentation](https://docs.liquibase.com/)
- [Liquibase Best Practices](https://www.liquibase.org/get-started/best-practices)
- [Liquibase Maven Plugin](https://docs.liquibase.com/tools-integrations/maven/home.html)
