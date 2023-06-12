REM Windows launcher for JILT
@echo off

setlocal ENABLEEXTENSIONS

REM   location of jar file
SET JILT_JAR="%~dp0jilt.jar"

REM check if we were started from windows explorer and provide a sensible message
echo %cmdcmdline% | find "cmd /c" >nul
if %ERRORLEVEL% NEQ 0 GOTO :start

SET PAUSE_ON_CLOSE=1
IF /I "%PROCESSOR_ARCHITECTURE%" NEQ "AMD64" GOTO :no64bit

echo JILT is a command line application, please follow the steps below
echo.  
echo 1) Open windows command prompt  
echo    e.g. on Windows Vista / 7, press start, search "cmd.exe" and press enter
echo.  
echo 2) Type "%~dp0%jilt.bat" to execute JILT
echo.
GOTO :no

:start
IF /I "%PROCESSOR_ARCHITECTURE%" NEQ "AMD64" GOTO :no64bit

:no64bit
echo JILT requires a 64 bit version of Windows

:no
IF DEFINED PAUSE_ON_CLOSE pause
exit /b 1

java -Dlists.dir="%~dp0lists" -jar %JILT_JAR% %*

:exit_zero
exit /b 0
