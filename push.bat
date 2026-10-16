@echo off
echo ========================================================
echo   Dang day (push) branch feature/redesign-pip len GitLab...
echo ========================================================
cd /d "%~dp0"
git push -u origin feature/redesign-pip
echo.
echo ========================================================
pause
