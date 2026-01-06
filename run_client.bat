@echo off
REM ============================================
REM Dağıtık Mesaj Sistemi - İstemci Başlatma
REM ============================================

echo ============================================
echo   DAGITIK MESAJ SISTEMI - ISTEMCI
echo ============================================

set LEADER_HOST=localhost
set LEADER_PORT=9000
set MODE=interactive
set COUNT=1000

REM Argümanları işle
:parse_args
if "%1"=="" goto run
if "%1"=="--host" (
    set LEADER_HOST=%2
    shift
    shift
    goto parse_args
)
if "%1"=="--port" (
    set LEADER_PORT=%2
    shift
    shift
    goto parse_args
)
if "%1"=="--mode" (
    set MODE=%2
    shift
    shift
    goto parse_args
)
if "%1"=="--count" (
    set COUNT=%2
    shift
    shift
    goto parse_args
)
shift
goto parse_args

:run
echo Leader Address: %LEADER_HOST%:%LEADER_PORT%
echo Mode: %MODE%
echo ============================================

java -jar target\client.jar --host %LEADER_HOST% --port %LEADER_PORT% --mode %MODE% --count %COUNT%
