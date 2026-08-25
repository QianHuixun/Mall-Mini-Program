#!/usr/bin/env bash
set -Eeuo pipefail

cd "$(dirname "$0")"

business_dir="${BUSINESS_MIGRATION_DIR:-./preflight-migrations/business}"
v34="$business_dir/V34__所有表新增字段.sql"

if [[ ! -f "$v34" ]]; then
  echo "缺少业务迁移文件：$v34" >&2
  exit 1
fi

business_count="$(find "$business_dir" -maxdepth 1 -type f -name 'V*__*.sql' | wc -l)"
if [[ "$business_count" != "83" ]]; then
  echo "业务迁移文件数量异常：期望 83，实际 $business_count" >&2
  exit 1
fi

# 历史 V34 把库名写死为 zyysc，在 zyysc4 或临时库中会静默跳过所有表。
# 只修改部署副本，保持仓库原始历史迁移不变。
sed -i "s/table_schema = 'zyysc'/table_schema = DATABASE()/g" "$v34"

if grep -q "table_schema = 'zyysc'" "$v34"; then
  echo "V34 数据库名修正失败" >&2
  exit 1
fi

echo "迁移部署副本已准备：V34 使用 DATABASE()。"
