@echo off
setlocal

set "BASE_DIR=%~dp0"
if "%BASE_DIR:~-1%"=="\" set "BASE_DIR=%BASE_DIR:~0,-1%"

set "WRAPPER_JAR=%BASE_DIR%\.mvn\wrapper\maven-wrapper.jar"

if not exist "%WRAPPER_JAR%" (
    echo Error: Maven wrapper jar not found at %WRAPPER_JAR%
    exit /b 1
)

"%JAVA_HOME%\bin\java.exe" %MAVEN_OPTS% -jar "%WRAPPER_JAR%" %*
endlocal