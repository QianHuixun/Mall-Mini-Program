#!/usr/bin/env bash
set -Eeuo pipefail

cd "$(dirname "$0")"

INFRA_ENV="${INFRA_ENV:-/srv/lejia-infra/.env}"
PREFLIGHT_ID="${PREFLIGHT_ID:-$(date +%Y%m%d%H%M%S)}"
FLYWAY_IMAGE="${FLYWAY_IMAGE:-flyway/flyway:7.2.1}"

if [[ ! -f "$INFRA_ENV" ]]; then
  echo "基础设施密钥文件不存在：$INFRA_ENV" >&2
  exit 1
fi

set -a
# shellcheck disable=SC1090
source "$INFRA_ENV"
set +a

file_db="lejia_fw_file_${PREFLIGHT_ID}"
business_db="lejia_fw_business_${PREFLIGHT_ID}"

docker exec -i lejia-mysql sh -ec '
  MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec mysql -uroot
' <<SQL
CREATE DATABASE \`$file_db\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE \`$business_db\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
SQL

run_flyway() {
  local label="$1"
  local database="$2"
  local directory="$3"

  echo "[$label] Flyway migrate -> $database"
  docker run --rm \
    --network lejia-infra \
    --volume "$directory:/flyway/sql:ro" \
    --env "FLYWAY_URL=jdbc:mysql://mysql:3306/$database?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai" \
    --env FLYWAY_USER=root \
    --env "FLYWAY_PASSWORD=$MYSQL_ROOT_PASSWORD" \
    "$FLYWAY_IMAGE" \
    -connectRetries=12 migrate
}

run_flyway file "$file_db" "$PWD/preflight-migrations/file"
run_flyway business "$business_db" "$PWD/preflight-migrations/business"

docker exec lejia-mysql sh -ec '
  MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec mysql -uroot --batch --skip-column-names -e "$1"
' _ "
SELECT CONCAT('$file_db migrations=', COUNT(*)) FROM $file_db.flyway_schema_history WHERE success=1;
SELECT CONCAT('$business_db migrations=', COUNT(*)) FROM $business_db.flyway_schema_history WHERE success=1;
SELECT CONCAT('$file_db tables=', COUNT(*)) FROM information_schema.tables WHERE table_schema='$file_db';
SELECT CONCAT('$business_db tables=', COUNT(*)) FROM information_schema.tables WHERE table_schema='$business_db';
"

cat > preflight-flyway-result.env <<EOF
PREFLIGHT_ID=$PREFLIGHT_ID
PREFLIGHT_FILE_DB=$file_db
PREFLIGHT_BUSINESS_DB=$business_db
EOF
chmod 600 preflight-flyway-result.env

echo "文件与业务数据库的 Flyway 预演通过。账号数据库仍等待基线 SQL。"
