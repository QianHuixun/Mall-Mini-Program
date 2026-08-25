#!/usr/bin/env bash
set -Eeuo pipefail

APP_DIR="${APP_DIR:-/srv/lejia-apps}"
INFRA_DIR="${INFRA_DIR:-/srv/lejia-infra}"
MYSQL_CONTAINER="${MYSQL_CONTAINER:-lejia-mysql}"
FLYWAY_IMAGE="${FLYWAY_IMAGE:-flyway/flyway:7.2.1}"
PREFLIGHT_ID="${PREFLIGHT_ID:-$(date +%Y%m%d%H%M%S)}"

ACCOUNT_DUMP="${APP_DIR}/imports/account-v21.sql"
BUSINESS_DUMP="${APP_DIR}/imports/business-v85.sql"
ACCOUNT_MIGRATIONS="${APP_DIR}/preflight-migrations/account"
BUSINESS_MIGRATIONS="${APP_DIR}/validate-migrations/business"

ACCOUNT_DB="lejia_dump_account_${PREFLIGHT_ID}"
BUSINESS_DB="lejia_dump_business_${PREFLIGHT_ID}"

case "${PREFLIGHT_ID}" in
  (*[!0-9]*) echo "PREFLIGHT_ID must contain digits only" >&2; exit 2 ;;
esac

for path in "${ACCOUNT_DUMP}" "${BUSINESS_DUMP}"; do
  test -f "${path}" || { echo "Missing dump: ${path}" >&2; exit 2; }
done

for path in "${ACCOUNT_MIGRATIONS}" "${BUSINESS_MIGRATIONS}"; do
  test -d "${path}" || { echo "Missing migrations: ${path}" >&2; exit 2; }
done

set -a
# shellcheck disable=SC1091
. "${INFRA_DIR}/.env"
set +a

mysql_exec() {
  docker exec -e MYSQL_PWD="${MYSQL_ROOT_PASSWORD}" "${MYSQL_CONTAINER}" \
    mysql --batch --skip-column-names -uroot "$@"
}

create_database() {
  local database="$1"
  mysql_exec -e "CREATE DATABASE \`${database}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
}

import_dump() {
  local database="$1"
  local dump="$2"
  docker exec -i -e MYSQL_PWD="${MYSQL_ROOT_PASSWORD}" "${MYSQL_CONTAINER}" \
    mysql -uroot "${database}" < "${dump}"
}

flyway_validate() {
  local database="$1"
  local migrations="$2"
  shift 2
  docker run --rm --network lejia-infra \
    -v "${migrations}:/flyway/sql:ro" \
    "${FLYWAY_IMAGE}" \
    -url="jdbc:mysql://mysql:3306/${database}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai" \
    -user=root \
    -password="${MYSQL_ROOT_PASSWORD}" \
    "$@" validate
}

echo "Creating temporary databases"
create_database "${ACCOUNT_DB}"
create_database "${BUSINESS_DB}"

echo "Importing account V21 backup"
import_dump "${ACCOUNT_DB}" "${ACCOUNT_DUMP}"

echo "Importing business V85 backup"
import_dump "${BUSINESS_DB}" "${BUSINESS_DUMP}"

account_tables="$(mysql_exec -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='${ACCOUNT_DB}';")"
business_tables="$(mysql_exec -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='${BUSINESS_DB}';")"
account_version="$(mysql_exec "${ACCOUNT_DB}" -e "SELECT MAX(CAST(version AS UNSIGNED)) FROM flyway_schema_history WHERE success=1;")"
business_version="$(mysql_exec "${BUSINESS_DB}" -e "SELECT MAX(CAST(version AS UNSIGNED)) FROM flyway_schema_history WHERE success=1;")"

test "${account_tables}" -eq 20
test "${business_tables}" -eq 159
test "${account_version}" -eq 21
test "${business_version}" -eq 85

echo "Validating account backup against V2-V21 migrations"
flyway_validate "${ACCOUNT_DB}" "${ACCOUNT_MIGRATIONS}"

echo "Validating business V85 backup against original V1-V83 migrations"
flyway_validate "${BUSINESS_DB}" "${BUSINESS_MIGRATIONS}" -ignoreFutureMigrations=true

state_file="${APP_DIR}/.last-backup-preflight"
{
  printf 'ACCOUNT_DB=%s\n' "${ACCOUNT_DB}"
  printf 'BUSINESS_DB=%s\n' "${BUSINESS_DB}"
  printf 'ACCOUNT_TABLES=%s\n' "${account_tables}"
  printf 'BUSINESS_TABLES=%s\n' "${business_tables}"
  printf 'ACCOUNT_VERSION=%s\n' "${account_version}"
  printf 'BUSINESS_VERSION=%s\n' "${business_version}"
} > "${state_file}"
chmod 600 "${state_file}"

echo "Backup preflight passed"
echo "ACCOUNT_DB=${ACCOUNT_DB} tables=${account_tables} version=${account_version}"
echo "BUSINESS_DB=${BUSINESS_DB} tables=${business_tables} version=${business_version}"
