@echo off
REM ============================================
REM Dağıtık Mesaj Sistemi - Üye Başlatma
REM ============================================

echo ============================================
echo   DAGITIK MESAJ SISTEMI - UYE
echo ============================================

set MEMBER_ID=member_1
set MEMBER_HOST=localhost
set MEMBER_PORT=9100
set LEADER_HOST=localhost
set LEADER_PORT=9001
set DISK_STRATEGY=ZERO_COPY

REM Argümanları işle
:parse_args
if "%1"=="" goto run
if "%1"=="--id" (
    set MEMBER_ID=%2
    shift
    shift
    goto parse_args
)
if "%1"=="--host" (
    set MEMBER_HOST=%2
    shift
    shift
    goto parse_args
)
if "%1"=="--port" (
    set MEMBER_PORT=%2
    shift
    shift
    goto parse_args
)
if "%1"=="--leader-host" (
    set LEADER_HOST=%2
    shift
    shift
    goto parse_args
)
if "%1"=="--leader-port" (
    set LEADER_PORT=%2
    shift
    shift
    goto parse_args
)
if "%1"=="--disk-strategy" (
    set DISK_STRATEGY=%2
    shift
    shift
    goto parse_args
)
shift
goto parse_args

:run
echo Member ID: %MEMBER_ID%
echo Member Address: %MEMBER_HOST%:%MEMBER_PORT%
echo Leader Address: %LEADER_HOST%:%LEADER_PORT%
echo Disk Strategy: %DISK_STRATEGY%
echo ============================================

java -jar target\member.jar --id %MEMBER_ID% --host %MEMBER_HOST% --port %MEMBER_PORT% --leader-host %LEADER_HOST% --leader-port %LEADER_PORT% --disk-strategy %DISK_STRATEGY%
