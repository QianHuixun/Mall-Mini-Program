#!/bin/bash
set -euo pipefail

cd "$(dirname "$0")"

if [ -e .env ]; then
  echo ".env already exists; keeping existing credentials"
  exit 0
fi

umask 077
lejia_mysql_root_password="$(openssl rand -hex 24)"
lejia_business_db_password="$(openssl rand -hex 24)"
lejia_account_db_password="$(openssl rand -hex 24)"
lejia_file_db_password="$(openssl rand -hex 24)"
lejia_redis_password="$(openssl rand -hex 24)"
lejia_rabbitmq_password="$(openssl rand -hex 24)"

printf '%s\n' \
  "MYSQL_ROOT_PASSWORD=${lejia_mysql_root_password}" \
  "BUSINESS_DB_NAME=zyysc4" \
  "BUSINESS_DB_USER=lejia_business" \
  "BUSINESS_DB_PASSWORD=${lejia_business_db_password}" \
  "ACCOUNT_DB_NAME=tofocus_account" \
  "ACCOUNT_DB_USER=lejia_account" \
  "ACCOUNT_DB_PASSWORD=${lejia_account_db_password}" \
  "FILE_DB_NAME=tofocus_file" \
  "FILE_DB_USER=lejia_file" \
  "FILE_DB_PASSWORD=${lejia_file_db_password}" \
  "REDIS_PASSWORD=${lejia_redis_password}" \
  "RABBITMQ_USER=lejia_app" \
  "RABBITMQ_PASSWORD=${lejia_rabbitmq_password}" \
  "RABBITMQ_VHOST=/lejia-dev" > .env.tmp

install -m 0600 .env.tmp .env
rm -f .env.tmp
echo "Created /srv/lejia-infra/.env with mode 0600"

