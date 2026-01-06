@echo off
REM ============================================
REM Dağıtık Mesaj Sistemi - Lider Başlatma
REM ============================================

echo ============================================
echo   DAGITIK MESAJ SISTEMI - LIDER
echo ============================================

set CLIENT_PORT=9000
set GRPC_PORT=9001
set CONFIG=%~dp0tolerance.conf

REM Argümanları işle
:parse_args
if "%1"=="" goto run
if "%1"=="--client-port" (
    set CLIENT_PORT=%2
    shift
    shift
    goto parse_args
)
if "%1"=="--grpc-port" (
    set GRPC_PORT=%2
    shift
    shift
    goto parse_args
)
if "%1"=="--config" (
    set CONFIG=%2
    shift
    shift
    goto parse_args
)
shift
goto parse_args

:run
echo Client Port: %CLIENT_PORT%
echo gRPC Port: %GRPC_PORT%
echo Config: %CONFIG%
echo ============================================

java -jar target\leader.jar --client-port %CLIENT_PORT% --grpc-port %GRPC_PORT% --config %CONFIG%
