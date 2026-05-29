@echo off
setlocal enabledelayedexpansion

REM ============================================================
REM Yazanaki Mod — Multi-Version Build Script
REM
REM Usage:
REM   build-all.bat           — build all versions
REM   build-all.bat 1.21.5   — build one specific version
REM ============================================================

if not "%1"=="" (
    call :build %1
    goto :done
)

call :build 1.21.4
if errorlevel 1 goto :fail
call :build 1.21.5
if errorlevel 1 goto :fail
call :build 1.21.6
if errorlevel 1 goto :fail
call :build 1.21.8
if errorlevel 1 goto :fail
call :build 1.21.9
if errorlevel 1 goto :fail
call :build 1.21.11
if errorlevel 1 goto :fail
call :build 26.1
if errorlevel 1 goto :fail

echo.
echo ============================================================
echo All versions built successfully. Jars are in build\libs\
echo ============================================================
goto :done

:build
set MC=%~1
echo.
echo ============================================================
echo  Building Minecraft %MC%
echo ============================================================

if "%MC%"=="1.21.4"  set LOOM=1.11-SNAPSHOT
if "%MC%"=="1.21.5"  set LOOM=1.11-SNAPSHOT
if "%MC%"=="1.21.6"  set LOOM=1.11-SNAPSHOT
if "%MC%"=="1.21.8"  set LOOM=1.11-SNAPSHOT
if "%MC%"=="1.21.9"  set LOOM=1.11-SNAPSHOT
if "%MC%"=="1.21.11" set LOOM=1.14-SNAPSHOT
if "%MC%"=="26.1"    set LOOM=1.15-SNAPSHOT

powershell -Command "(Get-Content gradle.properties) -replace '^loom_version=.*', 'loom_version=%LOOM%' | Set-Content gradle.properties"

call gradlew build -Pmc=%MC% --no-daemon
if errorlevel 1 (
    echo [FAILED] %MC%
    exit /b 1
)

echo [OK] %MC%
exit /b 0

:fail
echo Build failed — stopping.
exit /b 1

:done
endlocal