@echo off
chcp 65001 >nul
title 乐嘉后端 JMeter SSH 隧道

echo 正在建立 SSH 隧道，请保持此窗口打开...
echo 本地 21001 -^> 账号服务
echo 本地 21003 -^> 文件服务
echo 本地 23505 -^> 商城服务
echo.

ssh -o ExitOnForwardFailure=yes -o ServerAliveInterval=30 -o ServerAliveCountMax=3 -N ^
  -L 21001:127.0.0.1:21001 ^
  -L 21003:127.0.0.1:21003 ^
  -L 23505:127.0.0.1:23505 ^
  li@dev-s1.lan.zhili-edu.com

echo.
echo SSH 隧道已经结束。若不是手工关闭，请检查网络或 SSH 登录状态。
pause

