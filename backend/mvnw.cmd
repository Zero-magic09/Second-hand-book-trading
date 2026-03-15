@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup batch script
@REM ----------------------------------------------------------------------------

@echo off
@setlocal

set WRAPPER_JAR="%~dp0\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

if exist "%~dp0\.mvn\wrapper\maven-wrapper.jar" goto execute

echo Downloading Maven Wrapper...
powershell -Command "(New-Object Net.WebClient).DownloadFile('%WRAPPER_URL%', '%WRAPPER_JAR%')"

:execute
set MAVEN_OPTS=-Dfile.encoding=UTF-8
java -jar %WRAPPER_JAR% %*

@endlocal
