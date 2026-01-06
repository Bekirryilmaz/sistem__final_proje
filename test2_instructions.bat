@echo off
REM ============================================
REM TEST 2: tolerance=3, 6 üye, 9000 mesaj
REM ============================================

echo ============================================
echo   TEST 2 SENARYOSU
echo ============================================
echo   Tolerance: 3
echo   Lider: 1 adet
echo   Uye: 6 adet
echo   Mesaj: 9000 adet
echo ============================================
echo.

REM tolerance.conf'u ayarla
echo tolerance=3 > tolerance.conf
echo [TEST2] tolerance.conf guncellendi (tolerance=3)

echo.
echo [TEST2] Asagidaki adimlari SIRAYLA farkli terminal pencerelerinde calistirin:
echo.
echo ADIM 1 - Lider'i baslat:
echo   run_leader.bat
echo.
echo ADIM 2 - 6 adet uye baslat (her biri ayri terminal):
echo   run_member.bat --id member_1 --port 9100
echo   run_member.bat --id member_2 --port 9101
echo   run_member.bat --id member_3 --port 9102
echo   run_member.bat --id member_4 --port 9103
echo   run_member.bat --id member_5 --port 9104
echo   run_member.bat --id member_6 --port 9105
echo.
echo ADIM 3 - 9000 mesaj gonder:
echo   run_client.bat --mode bulk --count 9000
echo.
echo ADIM 4 - Mesajlari oku:
echo   run_client.bat --mode get --get-start 1 --get-end 500
echo.
echo ADIM 5 - Crash testi (1 uye):
echo   Bir uye terminalini Ctrl+C ile kapatin
echo   Tekrar GET islemi yapin - sistem calismali
echo.
echo ADIM 6 - Crash testi (2 uye):
echo   Bir uye daha kapatin (toplam 2 uye kapali)
echo   Tekrar GET islemi yapin - sistem calismali
echo.
echo NOT: 3 veya daha fazla uye kapanirsa bazi mesajlar
echo      erisilemez hale gelir (tolerance=3 oldugu icin)
echo.
echo ============================================
pause
