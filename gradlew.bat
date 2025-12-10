@if "%DEBUG%"=="" @echo off
rem ============================================================================
rem  Gradle Startup Script for Windows (Improved & cleaned)
rem ============================================================================

rem Use local scope for variables
if "%OS%"=="Windows_NT" setlocal

rem Default JVM options (edit if needed)
set "DEFAULT_JVM_OPTS="

rem Resolve script directory
set "DIRNAME=%~dp0"
if "%DIRNAME%"=="" set "DIRNAME=."
set "APP_BASE_NAME=%~n0"
set "APP_HOME=%DIRNAME%"

rem --------------------------------------------------------------------------
rem  Locate Java
rem --------------------------------------------------------------------------
if defined JAVA_HOME goto FindJavaFromJAVA_HOME

set "JAVA_EXE=java.exe"
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%"=="0" goto Init

echo(
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo Please set JAVA_HOME to your Java installation directory.
goto Fail

:FindJavaFromJAVA_HOME
set "JAVA_HOME=%JAVA_HOME:"=%"
set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"

if exist "%JAVA_EXE%" goto Init

echo(
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo Please set JAVA_HOME to a valid JDK/JRE installation.
goto Fail

rem --------------------------------------------------------------------------
rem  Initialize & process command-line arguments
rem --------------------------------------------------------------------------
:Init
set "CMD_LINE_ARGS="

if not "%OS%"=="Windows_NT" goto ArgsLegacy
if "%@eval[2+2]"=="4" goto Args4NT

:ArgsLegacy
set "_SKIP=2"

:ArgsLegacyLoop
if "x%~1"=="x" goto Execute
set "CMD_LINE_ARGS=%*"
goto Execute

:Args4NT
set "CMD_LINE_ARGS=%$"

rem --------------------------------------------------------------------------
rem  Execute Gradle Wrapper
rem --------------------------------------------------------------------------
:Execute
set "CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar"

"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% ^
    "-Dorg.gradle.appname=%APP_BASE_NAME%" ^
    -classpath "%CLASSPATH%" ^
    org.gradle.wrapper.GradleWrapperMain %CMD_LINE_ARGS%

rem --------------------------------------------------------------------------
rem  Exit Handling
rem --------------------------------------------------------------------------
:End
if "%ERRORLEVEL%"=="0" goto MainEnd

:Fail
if not "%GRADLE_EXIT_CONSOLE%"=="" exit 1
exit /b 1

:MainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
