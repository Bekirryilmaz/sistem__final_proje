@echo off
REM ============================================
REM TEST 1 - Otomatik Başlatma (PowerShell gerekli)
REM ============================================

echo ============================================
echo   TEST 1 - OTOMATIK BASLATMA
echo ============================================
echo   Tolerance: 2
echo   4 uye baslatiliyor...
echo ============================================

REM tolerance.conf'u ayarla
echo tolerance=2 > tolerance.conf

REM Lider'i başlat
echo [TEST1] Lider baslatiliyor...
start "LEADER" cmd /k "cd /d %~dp0 && java -jar target\leader.jar --config "%~dp0tolerance.conf""

REM Biraz bekle
timeout /t 3 /nobreak > nul

REM 4 üye başlat
echo [TEST1] Uyeler baslatiliyor...
start "MEMBER_1" cmd /k "java -jar target\member.jar --id member_1 --port 9100"
start "MEMBER_2" cmd /k "java -jar target\member.jar --id member_2 --port 9101"
start "MEMBER_3" cmd /k "java -jar target\member.jar --id member_3 --port 9102"
start "MEMBER_4" cmd /k "java -jar target\member.jar --id member_4 --port 9103"

REM Üyelerin bağlanması için bekle
timeout /t 3 /nobreak > nul

echo.
echo [TEST1] Sistem hazir!
echo.
echo Simdi asagidaki komutu calistirarak test yapabilirsiniz:
echo   run_client.bat --mode bulk --count 1000
echo.
echo Veya interaktif mod:
echo   run_client.bat --mode interactive
echo.
pause
