#!/usr/bin/env bash
set -Eeuo pipefail

cd "$(dirname "$0")"

for file in .env imports/account-v21.sql imports/business-v85.sql; do
  if [[ ! -f "$file" ]]; then
    echo "缺少正式导入文件：$file" >&2
    exit 1
  fi
done

set -a
# shellcheck disable=SC1091
source .env
set +a

[[ "${ACCOUNT_DB_NAME:-}" == "tofocus_account" ]] || { echo "账号目标库名不安全" >&2; exit 1; }
[[ "${FILE_DB_NAME:-}" == "tofocus_file" ]] || { echo "文件目标库名不安全" >&2; exit 1; }
[[ "${BUSINESS_DB_NAME:-}" == "zyysc4" ]] || { echo "商城目标库名不安全" >&2; exit 1; }

if grep -Eq '^(CREATE DATABASE|USE )' imports/account-v21.sql imports/business-v85.sql; then
  echo "完整备份包含数据库切换语句，拒绝导入" >&2
  exit 1
fi

mysql_root() {
  docker exec lejia-mysql sh -ec '
    MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec mysql -uroot --batch --skip-column-names -e "$1"
  ' _ "$1"
}

table_count() {
  mysql_root "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='$1';"
}

for database in "$ACCOUNT_DB_NAME" "$FILE_DB_NAME" "$BUSINESS_DB_NAME"; do
  count="$(table_count "$database")"
  if [[ "$count" != "0" ]]; then
    echo "目标库 $database 已有 $count 张表，拒绝覆盖" >&2
    exit 1
  fi
done

echo "停止隔离预检应用容器……"
if [[ -f .env.preflight ]]; then
  docker compose --env-file .env.preflight stop account file business
else
  docker compose stop account file business || true
fi

timestamp="$(date +%Y%m%d%H%M%S)"
backup_dir="backups/before-v85-$timestamp"
mkdir -p "$backup_dir"
chmod 700 backups "$backup_dir"

echo "备份三个正式目标库到 $backup_dir……"
for database in "$ACCOUNT_DB_NAME" "$FILE_DB_NAME" "$BUSINESS_DB_NAME"; do
  docker exec lejia-mysql sh -ec '
    MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec mysqldump -uroot \
      --single-transaction --routines --events --triggers --set-gtid-purged=OFF "$1"
  ' _ "$database" > "$backup_dir/$database.sql"
done
sha256sum "$backup_dir"/*.sql > "$backup_dir/SHA256SUMS"
chmod 600 "$backup_dir"/*

echo "导入账号 V21 完整备份……"
docker exec -i lejia-mysql sh -ec '
  MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec mysql -uroot --default-character-set=utf8mb4 "$1"
' _ "$ACCOUNT_DB_NAME" < imports/account-v21.sql

echo "导入商城 V85 完整备份……"
docker exec -i lejia-mysql sh -ec '
  MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec mysql -uroot --default-character-set=utf8mb4 "$1"
' _ "$BUSINESS_DB_NAME" < imports/business-v85.sql

account_tables="$(table_count "$ACCOUNT_DB_NAME")"
file_tables="$(table_count "$FILE_DB_NAME")"
business_tables="$(table_count "$BUSINESS_DB_NAME")"
account_version="$(mysql_root "SELECT MAX(CAST(version AS UNSIGNED)) FROM ${ACCOUNT_DB_NAME}.flyway_schema_history;")"
business_version="$(mysql_root "SELECT MAX(CAST(version AS UNSIGNED)) FROM ${BUSINESS_DB_NAME}.flyway_schema_history;")"

[[ "$account_tables" == "20" && "$account_version" == "21" ]] || {
  echo "账号正式库导入结果异常：tables=$account_tables version=$account_version" >&2
  exit 1
}
[[ "$file_tables" == "0" ]] || {
  echo "文件正式库应保持为空，等待应用执行 V1-V4：tables=$file_tables" >&2
  exit 1
}
[[ "$business_tables" == "159" && "$business_version" == "85" ]] || {
  echo "商城正式库导入结果异常：tables=$business_tables version=$business_version" >&2
  exit 1
}

echo "正式库导入完成：account=20/V21, file=0/待迁移, business=159/V85"
echo "导入前备份：$backup_dir"
echo "下一步执行：./deploy.sh"
