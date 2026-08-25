#!/usr/bin/env bash
set -Eeuo pipefail

cd "$(dirname "$0")"

MIGRATION_ROOT="${MIGRATION_ROOT:-./preflight-migrations}"
PREFLIGHT_ID="${PREFLIGHT_ID:-$(date +%Y%m%d%H%M%S)}"

account_db="lejia_pf_account_${PREFLIGHT_ID}"
file_db="lejia_pf_file_${PREFLIGHT_ID}"
business_db="lejia_pf_business_${PREFLIGHT_ID}"

for name in "$account_db" "$file_db" "$business_db"; do
  if [[ ! "$name" =~ ^[a-zA-Z0-9_]+$ ]]; then
    echo "非法临时数据库名：$name" >&2
    exit 1
  fi
done

for path in account file business; do
  if [[ ! -d "$MIGRATION_ROOT/$path" ]]; then
    echo "缺少迁移目录：$MIGRATION_ROOT/$path" >&2
    exit 1
  fi
done

docker exec -i lejia-mysql sh -ec '
  MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec mysql -uroot
' <<SQL
CREATE DATABASE \`$account_db\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE \`$file_db\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE \`$business_db\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
SQL

run_migrations() {
  local label="$1"
  local database="$2"
  local directory="$3"
  local count=0

  while IFS=$'\t' read -r filename filepath; do
    count=$((count + 1))
    echo "[$label][$count] $filename"
    if ! docker exec -i lejia-mysql sh -ec '
      MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec mysql --default-character-set=utf8mb4 -uroot "$1"
    ' _ "$database" < "$filepath"; then
      echo "迁移预演失败：$label / $filename / $database" >&2
      return 1
    fi
  done < <(
    find "$directory" -maxdepth 1 -type f -name 'V*__*.sql' -printf '%f\t%p\n' | sort -V
  )

  echo "$label 预演完成，共执行 $count 个脚本。"
}

if [[ "${SKIP_ACCOUNT:-0}" == "1" ]]; then
  echo "account 迁移已按 SKIP_ACCOUNT=1 跳过；需要补充 V2 之前的账号基线库。"
else
  run_migrations account "$account_db" "$MIGRATION_ROOT/account"
fi
run_migrations file "$file_db" "$MIGRATION_ROOT/file"
run_migrations business "$business_db" "$MIGRATION_ROOT/business"

docker exec lejia-mysql sh -ec '
  MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec mysql -uroot --batch --skip-column-names -e "$1"
' _ "
SELECT CONCAT('$account_db tables=', COUNT(*)) FROM information_schema.tables WHERE table_schema='$account_db';
SELECT CONCAT('$file_db tables=', COUNT(*)) FROM information_schema.tables WHERE table_schema='$file_db';
SELECT CONCAT('$business_db tables=', COUNT(*)) FROM information_schema.tables WHERE table_schema='$business_db';
"

cat > preflight-result.env <<EOF
PREFLIGHT_ID=$PREFLIGHT_ID
PREFLIGHT_ACCOUNT_DB=$account_db
PREFLIGHT_FILE_DB=$file_db
PREFLIGHT_BUSINESS_DB=$business_db
EOF
chmod 600 preflight-result.env

echo "迁移 SQL 预演通过。临时数据库名称已写入 preflight-result.env。"
