@echo off
chcp 65001 >nul
title 检查乐嘉后端连接

powershell.exe -NoProfile -ExecutionPolicy Bypass -Command ^
  "$ErrorActionPreference='Stop'; $targets=@(@('账号服务','http://127.0.0.1:21001/actuator/info'),@('文件服务','http://127.0.0.1:21003/actuator/info'),@('商城服务','http://127.0.0.1:23505/actuator/info')); $failed=$false; foreach($target in $targets){ try { $response=Invoke-WebRequest -UseBasicParsing -TimeoutSec 5 -Uri $target[1]; Write-Host ('[通过] {0}: HTTP {1}' -f $target[0],$response.StatusCode) -ForegroundColor Green } catch { Write-Host ('[失败] {0}: {1}' -f $target[0],$_.Exception.Message) -ForegroundColor Red; $failed=$true } }; if($failed){ exit 1 }"

echo.
if errorlevel 1 (
  echo 连接检查失败，请先运行 start-ssh-tunnel.cmd 并保持窗口打开。
) else (
  echo 三个后端服务均可从本机访问，可以打开 JMeter 测试。
)
pause
