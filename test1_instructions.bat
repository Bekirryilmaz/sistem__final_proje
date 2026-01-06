@echo off
REM ============================================
REM TEST 1: tolerance=2, 4 üye, 1000 mesaj
REM ============================================

echo ============================================
echo   TEST 1 SENARYOSU
echo ============================================
echo   Tolerance: 2
echo   Lider: 1 adet
echo   Uye: 4 adet
echo   Mesaj: 1000 adet
echo ============================================
echo.

REM tolerance.conf'u ayarla
echo tolerance=2 > tolerance.conf
echo [TEST1] tolerance.conf guncellendi (tolerance=2)

echo.
echo [TEST1] Asagidaki adimlari SIRAYLA farkli terminal pencerelerinde calistirin:
echo.
echo ADIM 1 - Lider'i baslat:
echo   run_leader.bat
echo.
echo ADIM 2 - 4 adet uye baslat (her biri ayri terminal):
echo   run_member.bat --id member_1 --port 9100
echo   run_member.bat --id member_2 --port 9101
echo   run_member.bat --id member_3 --port 9102
echo   run_member.bat --id member_4 --port 9103
echo.
echo ADIM 3 - 1000 mesaj gonder:
echo   run_client.bat --mode bulk --count 1000
echo.
echo ADIM 4 - Mesajlari oku:
echo   run_client.bat --mode get --get-start 1 --get-end 100
echo.
echo ADIM 5 - Crash testi:
echo   Bir uye terminalini Ctrl+C ile kapatin
echo   Tekrar GET islemi yapin - sistem calismali
echo.
echo ============================================
pause
