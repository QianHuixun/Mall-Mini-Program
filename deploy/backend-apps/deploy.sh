#!/usr/bin/env bash
set -Eeuo pipefail

cd "$(dirname "$0")"

for file in .env artifacts/account.jar artifacts/file.jar artifacts/business.jar; do
  if [[ ! -f "$file" ]]; then
    echo "缺少部署文件：$file" >&2
    exit 1
  fi
done

release_id="$(sha256sum artifacts/account.jar artifacts/file.jar artifacts/business.jar | sha256sum | cut -c1-16)"
sed -i "s/^APP_RELEASE_ID=.*/APP_RELEASE_ID=$release_id/" .env

docker compose config --quiet
docker compose build account file business

wait_healthy() {
  local container="$1"
  local retries="$2"
  local state

  for ((i=1; i<=retries; i++)); do
    state="$(docker inspect --format '{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}' "$container" 2>/dev/null || true)"
    if [[ "$state" == "healthy" ]]; then
      return 0
    fi
    if [[ "$state" == "unhealthy" || "$state" == "exited" || "$state" == "dead" ]]; then
      docker logs --tail=120 "$container" >&2 || true
      return 1
    fi
    sleep 5
  done

  docker logs --tail=120 "$container" >&2 || true
  return 1
}

docker compose up -d account
wait_healthy lejia-account 48

docker compose up -d file
wait_healthy lejia-file 48

docker compose up -d business
wait_healthy lejia-business 60

./verify.sh
