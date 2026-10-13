@echo off
set "SCRIPT_DIR=%~dp0"
if exist "%SCRIPT_DIR%..\maven\apache-maven-3.9.9\bin\mvn.cmd" (
    call "%SCRIPT_DIR%..\maven\apache-maven-3.9.9\bin\mvn.cmd" %*
) else (
    call mvn %*
)
