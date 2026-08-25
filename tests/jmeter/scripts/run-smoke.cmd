@echo off
setlocal
chcp 65001 >nul

set "ROOT_DIR=%~dp0.."
set "JMETER_CMD="

if defined JMETER_HOME if exist "%JMETER_HOME%\bin\jmeter.bat" set "JMETER_CMD=%JMETER_HOME%\bin\jmeter.bat"
if not defined JMETER_CMD for /f "delims=" %%I in ('where jmeter.bat 2^>nul') do if not defined JMETER_CMD set "JMETER_CMD=%%I"
if not defined JMETER_CMD if exist "E:\Jmeter\apache-jmeter-5.6.3\bin\jmeter.bat" set "JMETER_CMD=E:\Jmeter\apache-jmeter-5.6.3\bin\jmeter.bat"
if not defined JMETER_CMD if exist "C:\apache-jmeter-5.6.3\bin\jmeter.bat" set "JMETER_CMD=C:\apache-jmeter-5.6.3\bin\jmeter.bat"
if not defined JMETER_CMD if exist "%USERPROFILE%\Downloads\apache-jmeter-5.6.3\bin\jmeter.bat" set "JMETER_CMD=%USERPROFILE%\Downloads\apache-jmeter-5.6.3\bin\jmeter.bat"

if not defined JMETER_CMD (
  echo 未找到 jmeter.bat。
  echo 请先设置 JMETER_HOME，例如：set JMETER_HOME=D:\tools\apache-jmeter-5.6.3
  pause
  exit /b 1
)

if not exist "%ROOT_DIR%\results" mkdir "%ROOT_DIR%\results"
set "STAMP=%DATE:~0,4%%DATE:~5,2%%DATE:~8,2%-%TIME:~0,2%%TIME:~3,2%%TIME:~6,2%"
set "STAMP=%STAMP: =0%"
set "JTL=%ROOT_DIR%\results\smoke-%STAMP%.jtl"
set "REPORT=%ROOT_DIR%\results\report-%STAMP%"

call "%JMETER_CMD%" -n ^
  -t "%ROOT_DIR%\01-lejia-smoke.jmx" ^
  -q "%ROOT_DIR%\config\dev.properties" ^
  -Jresult_file="%JTL%" ^
  -l "%JTL%" ^
  -e -o "%REPORT%"

if errorlevel 1 (
  echo 冒烟测试执行失败，请检查上方日志。
) else (
  echo 冒烟测试完成。
  echo JTL: %JTL%
  echo HTML 报告: %REPORT%\index.html
)
pause
endlocal
