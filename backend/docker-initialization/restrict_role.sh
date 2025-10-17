#!/bin/sh

set -e  # Exit immediately on non-zero exit codes
set -o pipefail  # Catch errors in pipelines

echo "Remove superuser role from test role and create new postgres superuser"
psql -U ${POSTGRES_USER} "ris_adm_literature" -ac "create user postgres login password 'postgres' superuser inherit createdb createrole replication bypassrls";
psql -U ${POSTGRES_USER} "ris_adm_literature" -ac "alter user test nosuperuser noinherit nobypassrls";
