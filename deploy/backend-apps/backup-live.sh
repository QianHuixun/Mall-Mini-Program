#!/usr/bin/env bash
set -Eeuo pipefail

umask 077

APP_DIR="${APP_DIR:-/srv/lejia-apps}"
MYSQL_CONTAINER="${MYSQL_CONTAINER:-lejia-mysql}"
STAMP="$(date +%Y%m%d%H%M%S)"
RELATIVE_DIR="backups/full-v85-${STAMP}"
BACKUP_DIR="${APP_DIR}/${RELATIVE_DIR}"

cd "${APP_DIR}"
./verify.sh >/dev/null

mkdir -p "${BACKUP_DIR}"

dump_database() {
  local database="$1"
  local destination="${BACKUP_DIR}/${database}.sql"

  docker exec "${MYSQL_CONTAINER}" sh -ec '
    export MYSQL_PWD="$MYSQL_ROOT_PASSWORD"
    exec mysqldump \
      --user=root \
      --single-transaction \
      --quick \
      --routines \
      --triggers \
      --events \
      --hex-blob \
      --set-gtid-purged=OFF \
      "$1"
  ' sh "${database}" >"${destination}"
}

dump_database tofocus_account
dump_database tofocus_file
dump_database zyysc4

sha256sum \
  "${RELATIVE_DIR}/tofocus_account.sql" \
  "${RELATIVE_DIR}/tofocus_file.sql" \
  "${RELATIVE_DIR}/zyysc4.sql" \
  >"${BACKUP_DIR}/SHA256SUMS"

sha256sum -c "${BACKUP_DIR}/SHA256SUMS"
printf '完整备份完成：%s\n' "${BACKUP_DIR}"
