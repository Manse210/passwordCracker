@echo off
cd /d "%~dp0"
if not exist bin mkdir bin
javac -d bin src\*.java
if %errorlevel% equ 0 (
    echo. Compilation reussie dans bin\
    echo.
    echo Utilisation depuis la racine du projet :
    echo   java -cp bin Main -m DICO -h 098f6bcd4621d373cade4e832627b4f6
    echo   java -cp bin Main -m BRUTE -h 098f6bcd4621d373cade4e832627b4f6
) else (
    echo. Erreur de compilation
)
pause
