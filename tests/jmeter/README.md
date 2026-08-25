# 乐嘉后端 JMeter 测试包

本目录包含四套测试计划：

1. `01-lejia-smoke.jmx`：无需登录的只读冒烟测试。
2. `02-lejia-auth.jmx`：使用专用账号执行 OAuth 认证和登录后只读接口测试。
3. `03-p01-home-performance.jmx`：P-01 商城首页并发访问性能基线。
4. `04-s01-api-security.jmx`：S-01 商城接口非破坏性安全检查。

## 1. 测试边界

本测试包适用于 Apache JMeter 5.6.3，只执行只读冒烟测试，不需要登录账号，不上传文件，不创建订单，也不会修改数据库。

覆盖接口：

1. 账号服务 `GET /actuator/info`，预期 HTTP 200。
2. 文件服务 `GET /actuator/info`，预期 HTTP 200。
3. 商城服务 `GET /actuator/info`，预期 HTTP 200。
4. 账号服务 `GET /v2/user/info`，未登录时预期 HTTP 401 和 `unauthorized`。
5. 商城服务 `POST /v1/app/market/goods/query`，预期 HTTP 200 和 `success=true`。
6. 商品列表返回商品 `pkey` 时，自动调用 `POST /v1/app/market/goods/get`；没有商品数据时生成一条成功的“无数据已跳过”记录。

当前部署的应用端口只绑定服务器 `127.0.0.1`，所以必须先建立 SSH 隧道。

## 2. 第一次运行

### 2.1 建立 SSH 隧道

双击：

```text
scripts/start-ssh-tunnel.cmd
```

根据 SSH 提示完成登录，然后保持该窗口打开。窗口关闭后，JMeter 将无法连接服务器。

如果出现“Address already in use”，说明本机的 21001、21003 或 23505 端口已被占用。先关闭占用程序，再重新启动隧道。

### 2.2 检查连接

双击：

```text
scripts/check-connection.cmd
```

应看到：

```text
[通过] 账号服务: HTTP 200
[通过] 文件服务: HTTP 200
[通过] 商城服务: HTTP 200
```

如果失败，先确认 SSH 隧道窗口仍然运行，再检查 VPN、局域网和 SSH 登录。

### 2.3 在 JMeter UI 中打开

1. 打开 JMeter 5.6.3。
2. 点击顶部菜单“文件”→“打开”。
3. 选择 `01-lejia-smoke.jmx`。
4. 左侧会出现“乐嘉后端第一版冒烟测试”。
5. 展开“01-只读冒烟测试线程组”。
6. 点击工具栏绿色三角按钮开始。
7. 测试完成后点击“查看结果树”和“汇总报告”。

默认只有 1 个线程、循环 1 次，适合在 UI 中调试。不要在“测试计划”中直接提高到高并发。

## 3. 如何看结果

“查看结果树”中的绿色请求表示该请求和全部断言通过。红色请求表示连接、HTTP 状态码、业务结果或响应时间至少有一项失败。

重点检查：

- `01-账号服务健康检查`：响应码 200。
- `02-文件服务健康检查`：响应码 200。
- `03-商城服务健康检查`：响应码 200。
- `04-未登录访问用户信息-预期401`：响应码 401，但测试结果应为绿色，因为 401 正是预期结果。
- `05-商城商品列表`：响应正文包含 `"success":true`。
- `06-商城商品详情-动态ID`：列表有商品时出现。
- `06-商城商品详情-无数据已跳过`：列表没有商品时出现，这不是错误。

失败时选中对应请求，查看右侧：

1. “取样器结果”：检查 URL、响应码和耗时。
2. “请求”：检查提交参数。
3. “响应数据”：检查后端 JSON。
4. “断言结果”：查看具体失败原因。

## 4. 环境参数

命令行执行使用 `config/dev.properties`。JMX 在 UI 中也带有相同默认值：

```properties
account_host=127.0.0.1
account_port=21001
file_host=127.0.0.1
file_port=21003
business_host=127.0.0.1
business_port=23505
threads=1
loops=1
max_response_ms=5000
```

响应超过 5 秒会触发响应时间断言失败。连接超时为 3 秒，响应超时为 10 秒。

## 5. 命令行运行和 HTML 报告

完成 UI 调试后，可以双击：

```text
scripts/run-smoke.cmd
```

脚本会依次尝试：

1. 环境变量 `JMETER_HOME`。
2. 系统 `PATH` 中的 `jmeter.bat`。
3. 当前机器上的 `E:\Jmeter\apache-jmeter-5.6.3\bin\jmeter.bat`。
4. `C:\apache-jmeter-5.6.3\bin\jmeter.bat`。
5. `%USERPROFILE%\Downloads\apache-jmeter-5.6.3\bin\jmeter.bat`。

如果仍找不到，请在命令行中设置实际目录：

```cmd
set JMETER_HOME=D:\你的目录\apache-jmeter-5.6.3
```

结果保存在 `results` 目录，包括 JTL 文件和 HTML 报告。测试结果和日志已加入 `.gitignore`。

## 6. 安全规则

- 第一版线程数必须保持较低，只用于冒烟验证。
- 不要给 JMX 添加正式账号、密码、Token 或 OAuth Secret。
- 不要在当前测试计划中加入支付、下单、退款、短信或文件上传请求。
- 如需扩展登录和文件测试，使用独立测试账号、测试数据和单独的 JMX 文件。
- 测试结束后关闭 SSH 隧道窗口，释放本地转发端口。

## 7. 已知数据情况

2026-08-25 实测商品列表接口返回：

```json
{"code":"998","success":true}
```

当前响应没有商品数据，所以测试计划不会强制商品详情必须执行。后续数据库增加可售商品后，JSON Extractor 会通过 `$..pkey` 自动提取第一个商品 ID 并执行详情请求。

## 8. 第二版认证测试

### 8.1 专用测试账号

已创建两名专用测试用户，初始密码均为 `123456`：

| 登录名 | 显示名称 | 手机号 |
| --- | --- | --- |
| `tf_10263` | Junxun | `18054012708` |
| `tf_10264` | YiTongXue | `17079424009` |

两个账号均绑定角色 `JMeter ReadOnly`，角色标识为 `zyysc_a56a0068369ea7d1`。当前角色仅保留 `zy_login` 登录权限，不包含用户、角色、组织、商品、订单等管理权限。

账号数据保存在本机 `data/users.local.csv`，OAuth 客户端参数保存在本机 `config/auth.local.properties`。这两个文件已加入 `.gitignore`，不要提交、截图或转发其中的密码和客户端密钥。可复制对应的 `*.example.*` 文件创建新的本地配置。

### 8.2 在 JMeter UI 中运行

1. 双击 `scripts/start-ssh-tunnel.cmd`，保持 SSH 隧道窗口打开。
2. 双击 `scripts/open-auth-ui.cmd`。该脚本会加载本机认证配置并打开 `02-lejia-auth.jmx`。
3. 展开“02-专用账号认证线程组”，点击工具栏绿色三角按钮。
4. 在“查看结果树”中确认 6 条请求全部为绿色，在“汇总报告”中确认错误率为 0%。

不要直接双击 JMX 或只用 JMeter 菜单打开它，否则本机 OAuth 参数可能没有加载。需要修改账号时，编辑 `data/users.local.csv`，每行一个账号。

第二版默认并行运行 2 个线程，每个线程从 CSV 读取一个账号并执行：

1. `POST /oauth/token` 获取访问令牌。
2. `GET /v2/user/info` 验证账号服务身份。
3. `POST /v1/get/ideninfo` 验证商城只读身份，并断言 `success=true`、`identity=1`。

### 8.3 命令行运行

建立 SSH 隧道后，双击：

```text
scripts/run-auth.cmd
```

脚本会生成 JTL、JMeter 日志和 HTML 报告。2026-08-25 已使用 JMeter 5.6.3 实测：6 个请求全部成功，错误率 0%。

### 8.4 当前登录限制

商城统一登录接口 `/v1/login` 当前会因应用内部 SSH 公钥读取失败而返回错误。因此第二版暂时直接调用账号服务 `/oauth/token`，再携带 Bearer Token 访问受保护接口。这可以验证账号、密码、OAuth、Token 和登录后鉴权链路，但还不能证明 `/v1/login` 已恢复。

修复商城服务的 SSH 公钥配置后，应增加独立用例覆盖 `/v1/login`，再将其纳入正式回归测试。

## 9. P-01 商城首页并发访问性能

先启动 SSH 隧道。需要在 UI 中查看计划时双击：

~~~text
scripts/open-p01-ui.cmd
~~~

正式性能测试不要使用 UI，双击以下脚本以非 GUI 模式运行并生成 HTML 报告：

~~~text
scripts/run-p01.cmd
~~~

默认参数位于 `config/p01-performance.properties`：

~~~properties
perf_threads=20
perf_ramp_time=20
perf_duration=120
perf_think_base_ms=100
perf_think_random_ms=200
perf_max_response_ms=2000
~~~

测试覆盖首页归属配置、分类列表、专区商品和商品列表，共 4 个只读接口。性能测试时不要启用“查看结果树”，否则监听器会消耗大量内存并影响结果。

当前验收目标：错误率不超过 1%，平均不超过 800 ms，P95 不超过 1,500 ms，P99 不超过 2,500 ms。

## 10. S-01 商城接口安全性检查

先启动 SSH 隧道，然后双击：

~~~text
scripts/open-s01-ui.cmd
~~~

点击绿色启动按钮后，在“查看结果树”中查看每项断言。红色结果代表发现安全风险或接口行为与安全预期不一致。

命令行执行并生成 HTML 报告：

~~~text
scripts/run-s01.cmd
~~~

S-01 只执行非破坏性检查：匿名访问、伪造 Token、敏感 Actuator 暴露、HTTP 方法限制、非法参数、缺失参数、CORS 和安全响应头。不执行密码爆破、SQL 写入、恶意文件上传、缓存重置或日志级别修改。

2026-08-25 实测结果及风险分级见 `P01-S01-测试报告.md`。
