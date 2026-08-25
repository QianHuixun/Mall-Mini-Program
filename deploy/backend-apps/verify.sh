#!/usr/bin/env bash
set -Eeuo pipefail

cd "$(dirname "$0")"

echo "[1/5] 容器状态"
docker compose ps

echo "[2/5] 应用健康端点"
curl -fsS http://127.0.0.1:21001/actuator/info
echo
curl -fsS http://127.0.0.1:21003/actuator/info
echo
curl -fsS http://127.0.0.1:23505/actuator/info
echo

echo "[3/5] Eureka 注册"
registry="$(curl -fsS -H 'Accept: application/json' http://127.0.0.1:20000/eureka/apps)"
for app in ACCOUNT FILE ZYYSC-SERVER; do
  grep -qi "\"name\"[[:space:]]*:[[:space:]]*\"$app\"" <<<"$registry"
  echo "$app registered"
done

echo "[4/5] Flyway 版本"
docker exec lejia-mysql sh -ec '
  export MYSQL_PWD="$MYSQL_ROOT_PASSWORD"
  account="$(mysql -uroot --batch --skip-column-names -e "SELECT MAX(CAST(version AS UNSIGNED)) FROM tofocus_account.flyway_schema_history;")"
  file="$(mysql -uroot --batch --skip-column-names -e "SELECT MAX(CAST(version AS UNSIGNED)) FROM tofocus_file.flyway_schema_history;")"
  business="$(mysql -uroot --batch --skip-column-names -e "SELECT MAX(CAST(version AS UNSIGNED)) FROM zyysc4.flyway_schema_history;")"
  test "$account" = "21"
  test "$file" = "4"
  test "$business" = "85"
  printf "tofocus_account=V%s\ntofocus_file=V%s\nzyysc4=V%s\n" "$account" "$file" "$business"
'

echo "[5/5] 安全监听"
ss -lnt '( sport = :21001 or sport = :21003 or sport = :23505 )'

echo "应用服务验收通过。"
