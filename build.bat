@echo off
setlocal
cd /d "%~dp0"
if not exist "build\classes\emartproject\images" mkdir "build\classes\emartproject\images"
if not exist "dist" mkdir "dist"
javac -encoding UTF-8 -d "build\classes" src\emartproject\*.java
if errorlevel 1 exit /b 1
copy /Y src\emartproject\images\*.png build\classes\emartproject\images\ >nul
if errorlevel 1 exit /b 1
jar cfe dist\EMartProject.jar emartproject.EMartProject -C build\classes .
if errorlevel 1 exit /b 1
echo Built dist\EMartProject.jar
endlocal
