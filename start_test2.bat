@echo off
REM ============================================
REM TEST 2 - Otomatik Başlatma
REM ============================================

echo ============================================
echo   TEST 2 - OTOMATIK BASLATMA
echo ============================================
echo   Tolerance: 3
echo   6 uye baslatiliyor...
echo ============================================

REM tolerance.conf'u ayarla
echo tolerance=3 > tolerance.conf

REM Lider'i başlat
echo [TEST2] Lider baslatiliyor...
start "LEADER" cmd /k "cd /d %~dp0 && java -jar target\leader.jar --config "%~dp0tolerance.conf""

REM Biraz bekle
timeout /t 3 /nobreak > nul

REM 6 üye başlat
echo [TEST2] Uyeler baslatiliyor...
start "MEMBER_1" cmd /k "java -jar target\member.jar --id member_1 --port 9100"
start "MEMBER_2" cmd /k "java -jar target\member.jar --id member_2 --port 9101"
start "MEMBER_3" cmd /k "java -jar target\member.jar --id member_3 --port 9102"
start "MEMBER_4" cmd /k "java -jar target\member.jar --id member_4 --port 9103"
start "MEMBER_5" cmd /k "java -jar target\member.jar --id member_5 --port 9104"
start "MEMBER_6" cmd /k "java -jar target\member.jar --id member_6 --port 9105"

REM Üyelerin bağlanması için bekle
timeout /t 3 /nobreak > nul

echo.
echo [TEST2] Sistem hazir!
echo.
echo Simdi asagidaki komutu calistirarak test yapabilirsiniz:
echo   run_client.bat --mode bulk --count 9000
echo.
echo Veya interaktif mod:
echo   run_client.bat --mode interactive
echo.
pause
