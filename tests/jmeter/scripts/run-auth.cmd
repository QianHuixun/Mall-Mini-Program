@echo off
setlocal
chcp 65001 >nul

set "ROOT_DIR=%~dp0.."
set "JMETER_CMD="

if defined JMETER_HOME if exist "%JMETER_HOME%\bin\jmeter.bat" set "JMETER_CMD=%JMETER_HOME%\bin\jmeter.bat"
if not defined JMETER_CMD if exist "E:\Jmeter\apache-jmeter-5.6.3\bin\jmeter.bat" set "JMETER_CMD=E:\Jmeter\apache-jmeter-5.6.3\bin\jmeter.bat"
if not defined JMETER_CMD for /f "delims=" %%I in ('where jmeter.bat 2^>nul') do if not defined JMETER_CMD set "JMETER_CMD=%%I"

if not defined JMETER_CMD (
  echo 未找到 jmeter.bat，请设置 JMETER_HOME。
  pause
  exit /b 1
)

if not exist "%ROOT_DIR%\results" mkdir "%ROOT_DIR%\results"
set "STAMP=%DATE:~0,4%%DATE:~5,2%%DATE:~8,2%-%TIME:~0,2%%TIME:~3,2%%TIME:~6,2%"
set "STAMP=%STAMP: =0%"
set "JTL=%ROOT_DIR%\results\auth-%STAMP%.jtl"
set "REPORT=%ROOT_DIR%\results\auth-report-%STAMP%"

call "%JMETER_CMD%" -n ^
  -t "%ROOT_DIR%\02-lejia-auth.jmx" ^
  -q "%ROOT_DIR%\config\auth.local.properties" ^
  -l "%JTL%" ^
  -e -o "%REPORT%"

if errorlevel 1 (
  echo 认证测试执行失败，请检查上方日志。
) else (
  echo 认证测试完成：%REPORT%\index.html
)
pause
endlocal
