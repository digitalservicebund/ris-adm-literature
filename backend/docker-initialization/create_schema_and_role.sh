#!/bin/sh

set -e  # Exit immediately on non-zero exit codes
set -o pipefail  # Catch errors in pipelines

#################
### FUNCTIONS
#################

# Function to check if a role exists, if not create it
create_role_if_not_exists() {
    local role_name="$1"
    local role_password="$2"
    [ "$(psql -U ${POSTGRES_USER} postgres -Atc "SELECT 1 FROM pg_roles WHERE rolname = '${role_name}';")" = "1" ] || \
    psql -U ${POSTGRES_USER} postgres -c "CREATE ROLE ${role_name} LOGIN PASSWORD '${role_password}';"
}

# Generalized function to set privileges on a schema for a user, including future tables and objects
set_schema_privileges() {
    local db_name="$1"
    local schema="$2"
    local user="$3"
    local privileges="$4"  # e.g. "ALL" for full access, "SELECT" for read-only
    local objects_privileges="${5:-"$privileges"}" # Default to the same privileges on objects
    psql -U ${POSTGRES_USER} "${db_name}" -ac "REVOKE ALL ON SCHEMA ${schema} FROM public;"
    psql -U ${POSTGRES_USER} "${db_name}" -ac "GRANT ${privileges} ON SCHEMA ${schema} TO ${user};"
    psql -U ${POSTGRES_USER} "${db_name}" -ac "GRANT ${objects_privileges} ON ALL TABLES IN SCHEMA ${schema} TO ${user};"
    psql -U ${POSTGRES_USER} "${db_name}" -ac "GRANT ${objects_privileges} ON ALL SEQUENCES IN SCHEMA ${schema} TO ${user};"
    psql -U ${POSTGRES_USER} "${db_name}" -ac "GRANT ${objects_privileges} ON ALL FUNCTIONS IN SCHEMA ${schema} TO ${user};"

    # Apply default privileges for future objects created by the user
    psql -U ${POSTGRES_USER} "${db_name}" -ac "ALTER DEFAULT PRIVILEGES FOR ROLE ${user} IN SCHEMA ${schema} GRANT ${objects_privileges} ON TABLES TO ${user};"
    psql -U ${POSTGRES_USER} "${db_name}" -ac "ALTER DEFAULT PRIVILEGES FOR ROLE ${user} IN SCHEMA ${schema} GRANT ${objects_privileges} ON SEQUENCES TO ${user};"
    psql -U ${POSTGRES_USER} "${db_name}" -ac "ALTER DEFAULT PRIVILEGES FOR ROLE ${user} IN SCHEMA ${schema} GRANT ${objects_privileges} ON FUNCTIONS TO ${user};"
}

# Grant read-only access to a schema for a specific user for objects created by another user
grant_read_access() {
    local db_name="$1"
    local schema="$2"
    local read_user="$3"
    local owner_user="$4"

    # Grant usage on the schema
    psql -U ${POSTGRES_USER} "${db_name}" -ac "GRANT USAGE ON SCHEMA ${schema} TO ${read_user};"

    # Grant SELECT on all existing tables, sequences, and functions
    psql -U ${POSTGRES_USER} "${db_name}" -ac "GRANT SELECT ON ALL TABLES IN SCHEMA ${schema} TO ${read_user};"
    psql -U ${POSTGRES_USER} "${db_name}" -ac "GRANT SELECT ON ALL SEQUENCES IN SCHEMA ${schema} TO ${read_user};"
    psql -U ${POSTGRES_USER} "${db_name}" -ac "GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA ${schema} TO ${read_user};"

    # Grant SELECT on all future tables, sequences, and functions (default privileges)
    psql -U ${POSTGRES_USER} "${db_name}" -ac "ALTER DEFAULT PRIVILEGES FOR ROLE ${owner_user} IN SCHEMA ${schema} GRANT SELECT ON TABLES TO ${read_user};"
    psql -U ${POSTGRES_USER} "${db_name}" -ac "ALTER DEFAULT PRIVILEGES FOR ROLE ${owner_user} IN SCHEMA ${schema} GRANT SELECT ON SEQUENCES TO ${read_user};"
    psql -U ${POSTGRES_USER} "${db_name}" -ac "ALTER DEFAULT PRIVILEGES FOR ROLE ${owner_user} IN SCHEMA ${schema} GRANT EXECUTE ON FUNCTIONS TO ${read_user};"
}

# Function to create a schema if it does not exist
create_schema_if_not_exists() {
    local db_name="$1"
    local schema="$2"
    local authorization="$3"
    psql -U ${POSTGRES_USER} "${db_name}" -ac "CREATE SCHEMA IF NOT EXISTS ${schema} AUTHORIZATION ${authorization};"
}

# Function to handle schema setup and write-all permissions
setup_schema_and_permissions() {
    local db_name="$1"
    local schema="$2"
    local user="$3"
    create_schema_if_not_exists "${db_name}" "${schema}" "${user}"
    set_schema_privileges "${db_name}" "${schema}" "${user}" "ALL"
}

echo "Create lookup_tables role and schema"
create_role_if_not_exists "lookup_tables" "test"
setup_schema_and_permissions "ris_adm_literature" "lookup_tables" "lookup_tables"
grant_read_access "ris_adm_literature" "lookup_tables" "test" "lookup_tables"
