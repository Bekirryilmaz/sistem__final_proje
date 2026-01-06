@echo off
REM ============================================
REM Dağıtık Mesaj Sistemi - Derleme Scripti
REM ============================================

echo ============================================
echo   DAGITIK MESAJ SISTEMI - DERLEME
echo ============================================

REM Maven kontrolü
where mvn >nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo [BUILD] Sistem Maven kullaniliyor...
    call mvn clean package -DskipTests
) else (
    echo [BUILD] Maven bulunamadi, wrapper kullaniliyor...
    call mvnw.cmd clean package -DskipTests
)

if %ERRORLEVEL% neq 0 (
    echo [BUILD] Derleme BASARISIZ!
    pause
    exit /b 1
)

echo.
echo [BUILD] Derleme BASARILI!
echo.
echo Olusturulan JAR dosyalari:
echo   - target\leader.jar
echo   - target\member.jar
echo   - target\client.jar
echo.
echo ============================================
pause
