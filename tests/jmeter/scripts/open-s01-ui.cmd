@echo off
setlocal EnableExtensions
set "ROOT_DIR=%~dp0.."
set "JMETER_CMD="
if defined JMETER_HOME if exist "%JMETER_HOME%\bin\jmeter.bat" set "JMETER_CMD=%JMETER_HOME%\bin\jmeter.bat"
if not defined JMETER_CMD if exist "E:\Jmeter\apache-jmeter-5.6.3\bin\jmeter.bat" set "JMETER_CMD=E:\Jmeter\apache-jmeter-5.6.3\bin\jmeter.bat"
if not defined JMETER_CMD for /f "delims=" %%I in ('where jmeter.bat 2^>nul') do if not defined JMETER_CMD set "JMETER_CMD=%%I"
if defined JMETER_CMD goto jmeter_found
echo ERROR: jmeter.bat was not found. Set JMETER_HOME first.
pause
exit /b 1

:jmeter_found
if not exist "%ROOT_DIR%\results" mkdir "%ROOT_DIR%\results"
echo Starting JMeter S-01 UI...
pushd "%ROOT_DIR%"
if errorlevel 1 goto path_failed
call "%JMETER_CMD%" -t "%ROOT_DIR%\04-s01-api-security.jmx" -q "%ROOT_DIR%\config\s01-security.properties" -j "%ROOT_DIR%\results\s01-ui.log"
set "EXIT_CODE=%ERRORLEVEL%"
popd
if "%EXIT_CODE%"=="0" goto success
echo ERROR: JMeter exited with code %EXIT_CODE%.
echo Log: %ROOT_DIR%\results\s01-ui.log
pause
endlocal & exit /b %EXIT_CODE%

:path_failed
echo ERROR: Cannot access the JMeter test directory.
pause
endlocal & exit /b 1

:success
endlocal & exit /b 0
