@echo off
setlocal enabledelayedexpansion

REM ============================================================
REM Yazanaki Mod — Build Script
REM
REM Usage:
REM   build-all.bat           — build all versions
REM   build-all.bat 1.21.11  — build one specific version
REM   build-all.bat 26.1     — build one specific version
REM ============================================================

if not "%1"=="" (
    call :build %1
    goto :done
)

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