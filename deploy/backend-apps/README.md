# 乐嘉后端应用服务

该目录用于部署账号服务、文件服务和业务服务。基础设施必须已经运行于 Docker 网络 `lejia-infra`。

## 当前实施状态（2026-08-25）

- 已在 `li@dev-s1.lan.zhili-edu.com:/srv/lejia-apps` 完成正式部署，发布标识为 `c42e730f664c3df2`。
- 已完成完整备份临时库预检：账号 V21、文件 V4、商城 V85 均通过 Flyway 与应用启动校验。
- 正式库已完成初始化：`tofocus_account=V21`、`tofocus_file=V4`、`zyysc4=V85`。
- 账号、文件、业务三个容器均为 `healthy`，重启次数为 0，未发生 OOM；已注册到 Eureka。
- 应用端口 `21001`、`21003`、`23505` 均只绑定宿主机 `127.0.0.1`。
- 正式导入前的空库备份位于 `/srv/lejia-apps/backups/before-v85-20260825020748`，三份 SQL 的 SHA-256 校验均已通过。
- 部署后的完整数据库快照位于 `/srv/lejia-apps/backups/full-v85-20260825022230`，包含账号 V21、文件 V4、商城 V85，三份 SQL 的 SHA-256 校验均已通过。
- 业务 V34 的空库预演副本使用 `DATABASE()`，解决原脚本写死 `zyysc` 导致 V44 缺列的问题；正式 V85 运行校验使用未修改的 V1-V83 历史脚本。
- 三个可执行 JAR 均由 JDK 8、Maven 3.5.3 和本地私有 Maven 仓库从当前源码构建，并完成外层及全部内嵌 JAR 完整性校验。

当前正式环境已经运行，禁止再次执行 `promote-v85.sh`。该脚本自身也会在目标库非空时拒绝执行。后续日常发布只运行 `deploy.sh` 和 `verify.sh`；涉及数据库版本升级时，必须先按本节门禁另行预演和备份。

## 部署边界

- 所有应用端口仅绑定到宿主机 `127.0.0.1`。
- 密钥只保存在服务器 `/srv/lejia-apps/.env`，权限为 `0600`。
- 文件服务数据保存在 `/srv/lejia-apps/data/file`。
- 首次部署配置显式关闭支付、订单、结算、会员、京东和第三方同步任务。
- 按项目约定加载环境配置：账号和文件服务使用 `common,preProduct,running`，业务服务使用 `common,release,running`。
- 服务器 `/app/config/application-running.properties` 最后加载并覆盖旧环境连接；密码继续由 Compose 环境变量注入，不写入配置文件。
- `spring.rabbitmq.addresses` 必须在 running 配置中显式设为 `rabbitmq:5672`；仅设置 `host`/`port` 无法覆盖旧 `preProduct` 的聚合地址属性。
- `tofocus.prefix` 与 `spring.redis.prefix` 均固定为 `dev`，隔离旧环境的 `product` Redis 键和 RabbitMQ 队列。
- 三个容器使用可写且持久化的 `/app/runtime` 工作目录，供旧版 `ServerConfig` 保存实例 `serverId`；应用 JAR 和配置目录仍保持只读。
- 容器健康检查使用无需 OAuth 的 `/actuator/info`；`/actuator/health` 在账号服务中受资源服务器保护，会返回 401，不能作为 Docker 健康检查地址。

## 构建产物

已生成三个经过 JDK 8 构建和完整性验证的可执行 JAR：

```text
artifacts/account.jar
artifacts/file.jar
artifacts/business.jar
```

项目提供方给出的 Maven 打包方式为：

```bash
# 账号服务、文件服务
mvn clean package -P noLog

# 云商城业务服务
mvn clean package
```

文件服务坐标已按提供方版本调整为正式版：父版本 `3.5.14`、核心模块 `1.2.2`、服务模块 `1.8.3`。本机构建使用 `C:\Users\Lenovo\Desktop\repository` 中的私有制品；公开依赖中的截断文件已保留为 `.truncated.bak` 并由 Maven Central 补齐。

业务服务原源码使用 `javafx.util.Pair`，Temurin JDK 8 不包含 JavaFX。`RsaCoderUtils.genKeyPair()` 已改用 JDK 自带的 `Map.Entry`/`SimpleImmutableEntry`；项目内没有该方法调用方，RSA 加解密逻辑未改变。

禁止用 `target/classes` 手工拼装生产 JAR。缺少私有 Maven 依赖时，应恢复私服或取得正式构建产物。

## 数据库迁移门禁

上线目标库前，必须先在临时数据库预演。本次 V85 部署已经完成以下预演：

- 账号服务：20 条迁移，版本 V2 至 V21。
- 文件服务：4 条迁移，版本 V1 至 V4。
- 业务服务：83 条迁移，版本 V1 至 V83。

`flyway_schema_history`、Flyway checksum、JPA schema validate 和三个应用的隔离启动均已验证成功。

SQL 顺序预演：

```bash
cd /srv/lejia-apps
./preflight-migrations.sh
```

该脚本只创建带时间戳的临时数据库，不修改三个目标数据库。SQL 预演通过后，仍需由正式应用 JAR 验证 Flyway 历史表和 JPA schema validate。

注意：账号迁移从 V2 开始，V3 依赖旧基线表 `sys_merchant` 和 `sys_merchant_store`，因此不能从空库直接执行。本次部署以已收到的 V1-V21 完整账号备份为基线，并已在临时库完成 Flyway 校验。后续重新部署账号库时仍必须使用完整基线备份，不能仅从 V2 脚本起建空库。

使用与业务项目一致的 Flyway 7.2.1 进行精确预演：

```bash
./preflight-flyway.sh
```

该脚本只预演可以从空库初始化的文件库和业务库；账号库不会被误判为可初始化。

使用完整备份进行账号 V21、业务 V85 临时库预检：

```bash
./preflight-backups.sh
```

该脚本只创建 `lejia_dump_account_<时间戳>` 和 `lejia_dump_business_<时间戳>` 临时库。业务库使用未修改的 V1-V83 脚本校验原始 Flyway checksum，并显式允许备份中 V84、V85 两条未来迁移；目标数据库不会被修改。

当前业务服务运行时挂载 `validate-migrations/business` 中未修改的 V1-V83 脚本，并设置 `spring.flyway.ignore-future-migrations=true`，以兼容完整备份中已执行的 V84、V85。不得改回修正过 V34 checksum 的空库预检副本。

业务服务还通过 `tofocus.db.expected-version=85` 精确设置旧框架的二次数据库版本门禁。该配置只接受 V85；Flyway 仍会校验 V1-V83 原始 checksum，数据库低于或高于 V85 都不能通过应用启动检查。

全新空目标库在隔离预检全部通过后，正式导入使用：

```bash
./promote-v85.sh
./deploy.sh
```

`promote-v85.sh` 只允许三个正式目标库均为空时执行；导入前会把三个目标库备份到带时间戳的 `backups/before-v85-*` 目录。账号导入完整 V21，商城导入完整 V85，文件库保持为空并由文件服务首次启动执行 V1-V4。本次正式初始化已经完成，不得在现有正式库上重复运行该脚本。

业务 V34 历史脚本把数据库名写死为 `zyysc`。部署前执行 `./prepare-migrations.sh`，只修正服务器上的迁移副本为 `DATABASE()`；应用通过 `filesystem:/app/migrations` 使用外置迁移，不改写仓库中的历史 SQL。

## 生命周期

```bash
cd /srv/lejia-apps
./deploy.sh
./verify.sh
docker compose logs --tail=100
```

生成当前三个正式库的一致性完整备份：

```bash
cd /srv/lejia-apps
./backup-live.sh
```

脚本会先运行 `verify.sh`，只有三个应用健康、Eureka 注册完整且数据库精确处于账号 V21、文件 V4、商城 V85 时才开始备份。备份使用单事务导出，结果保存在 `backups/full-v85-<时间戳>`，权限受 `umask 077` 保护，并自动生成和复核 `SHA256SUMS`。

停止应用：

```bash
docker compose stop
```

停止应用不会删除数据库、文件和日志数据。
