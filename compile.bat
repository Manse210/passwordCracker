@echo off
cd /d "%~dp0src"
javac *.java
if %errorlevel% equ 0 (
    echo. ✅ Compilation reussie
    echo.
    echo Utilisation depuis la racine du projet :
    echo   java -cp src Main -m DICO -h 098f6bcd4621d373cade4e832627b4f6
    echo   java -cp src Main -m BRUTE -h 098f6bcd4621d373cade4e832627b4f6
) else (
    echo. ❌ Erreur de compilation
)
pause
