#!/usr/bin/env bash
set -Eeuo pipefail

obsolete=(
  lejia_pf_account_20260824074024
  lejia_pf_file_20260824074024
  lejia_pf_business_20260824074024
  lejia_pf_account_20260824074125
  lejia_pf_file_20260824074125
  lejia_pf_business_20260824074125
  lejia_fw_file_20260824074402
  lejia_fw_business_20260824074402
)

mysql_root() {
  docker exec lejia-mysql sh -ec '
    MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec mysql -uroot --batch --skip-column-names -e "$1"
  ' _ "$1"
}

echo "当前预演数据库："
mysql_root "SELECT SCHEMA_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME LIKE 'lejia\\_%' ORDER BY SCHEMA_NAME;"

if [[ "${1:-}" == "--cleanup" ]]; then
  for database in "${obsolete[@]}"; do
    if [[ ! "$database" =~ ^lejia_(pf|fw)_(account|file|business)_[0-9]{14}$ ]]; then
      echo "拒绝删除非法数据库名：$database" >&2
      exit 1
    fi
    mysql_root "DROP DATABASE IF EXISTS \`$database\`;"
    echo "已删除失败或过期预演库：$database"
  done
fi

echo "保留的预演数据库："
mysql_root "SELECT SCHEMA_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME LIKE 'lejia\\_%' ORDER BY SCHEMA_NAME;"

echo "目标数据库表数量："
mysql_root "
SELECT CONCAT('tofocus_account=', COUNT(*)) FROM information_schema.tables WHERE table_schema='tofocus_account';
SELECT CONCAT('tofocus_file=', COUNT(*)) FROM information_schema.tables WHERE table_schema='tofocus_file';
SELECT CONCAT('zyysc4=', COUNT(*)) FROM information_schema.tables WHERE table_schema='zyysc4';
"
