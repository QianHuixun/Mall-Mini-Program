# 乐嘉后端基础设施

该目录用于在开发服务器上部署 MySQL、Redis、RabbitMQ、Elasticsearch 和 Eureka。

## 安全边界

- 所有宿主机端口仅绑定到 `127.0.0.1`。
- 真实密码只保存在服务器 `/srv/lejia-infra/.env`，权限必须为 `0600`。
- 数据保存在服务器 `/srv/lejia-infra/data`，执行 `docker compose down` 不会删除数据。
- 禁止执行 `docker compose down -v` 或手工删除 `data` 目录。

## 生命周期

```bash
cd /srv/lejia-infra
docker compose config --quiet
docker compose up -d
docker compose ps
docker compose logs --tail=100
```

完整验收：

```bash
cd /srv/lejia-infra
./verify.sh
```

停止服务：

```bash
docker compose stop
```

恢复服务：

```bash
docker compose start
```
