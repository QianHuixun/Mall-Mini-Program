@echo off
setlocal
chcp 65001 >nul

set "ROOT_DIR=%~dp0.."
set "JMETER_CMD="
if defined JMETER_HOME if exist "%JMETER_HOME%\bin\jmeter.bat" set "JMETER_CMD=%JMETER_HOME%\bin\jmeter.bat"
if not defined JMETER_CMD for /f "delims=" %%I in ('where jmeter.bat 2^>nul') do if not defined JMETER_CMD set "JMETER_CMD=%%I"
if not defined JMETER_CMD if exist "E:\Jmeter\apache-jmeter-5.6.3\bin\jmeter.bat" set "JMETER_CMD=E:\Jmeter\apache-jmeter-5.6.3\bin\jmeter.bat"
if not defined JMETER_CMD if exist "C:\apache-jmeter-5.6.3\bin\jmeter.bat" set "JMETER_CMD=C:\apache-jmeter-5.6.3\bin\jmeter.bat"
if not defined JMETER_CMD (
  echo 未找到 jmeter.bat，请设置 JMETER_HOME。
  pause
  exit /b 1
)

if not exist "%ROOT_DIR%\results" mkdir "%ROOT_DIR%\results"
set "STAMP=%DATE:~0,4%%DATE:~5,2%%DATE:~8,2%-%TIME:~0,2%%TIME:~3,2%%TIME:~6,2%"
set "STAMP=%STAMP: =0%"
set "JTL=%ROOT_DIR%\results\s01-%STAMP%.jtl"
set "REPORT=%ROOT_DIR%\results\s01-report-%STAMP%"

call "%JMETER_CMD%" -n ^
  -t "%ROOT_DIR%\04-s01-api-security.jmx" ^
  -q "%ROOT_DIR%\config\s01-security.properties" ^
  -l "%JTL%" ^
  -e -o "%REPORT%"

echo S-01 已执行。红色断言代表发现风险，不代表脚本故障。
echo JTL: %JTL%
echo HTML 报告: %REPORT%\index.html
pause
endlocal
