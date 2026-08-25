#!/usr/bin/env bash
set -Eeuo pipefail

cd "$(dirname "$0")"

echo "[1/6] 容器健康状态"
docker compose ps

echo "[2/6] MySQL 数据库"
docker compose exec -T mysql sh -ec \
  'MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec mysql -uroot --batch --skip-column-names -e "$1"' \
  _ "SELECT SCHEMA_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME IN ('zyysc4', 'tofocus_account', 'tofocus_file') ORDER BY SCHEMA_NAME"

echo "[3/6] Redis"
docker compose exec -T redis sh -ec '
  test "$(redis-cli -a "$REDIS_PASSWORD" ping 2>/dev/null)" = "PONG"
'
echo "PONG"

echo "[4/6] RabbitMQ"
docker compose exec -T rabbitmq rabbitmq-diagnostics -q ping
docker compose exec -T rabbitmq rabbitmqctl -q list_vhosts name

echo "[5/6] Elasticsearch"
curl -fsS http://127.0.0.1:9200/_cluster/health?pretty

echo "[6/6] Eureka"
curl -fsS http://127.0.0.1:20000/actuator/info
echo

echo "基础设施验收通过。"
