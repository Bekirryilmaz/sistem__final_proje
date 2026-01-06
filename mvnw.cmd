@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup batch script
@REM ----------------------------------------------------------------------------

@echo off
@setlocal

set MAVEN_PROJECTBASEDIR=%~dp0
set MAVEN_CMD_LINE_ARGS=%*

@REM Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
goto error

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%\bin\java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
goto error

:execute
@REM Download maven if not exists
set MAVEN_HOME=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven
set MAVEN_ZIP=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven.zip
set MAVEN_URL=https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip

if exist "%MAVEN_HOME%\bin\mvn.cmd" goto runMaven

echo Downloading Maven...
mkdir "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper" 2>nul

powershell -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile '%MAVEN_ZIP%' }"

if not exist "%MAVEN_ZIP%" (
    echo ERROR: Failed to download Maven
    goto error
)

echo Extracting Maven...
powershell -Command "& { Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%MAVEN_PROJECTBASEDIR%\.mvn\wrapper' -Force }"
move "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\apache-maven-3.9.6" "%MAVEN_HOME%" >nul

del "%MAVEN_ZIP%"

:runMaven
"%MAVEN_HOME%\bin\mvn.cmd" %MAVEN_CMD_LINE_ARGS%
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%

cmd /C exit /B %ERROR_CODE%
