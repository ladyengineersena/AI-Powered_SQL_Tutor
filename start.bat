@echo off
setlocal EnableDelayedExpansion
chcp 65001 >nul
cd /d "%~dp0"

echo ==========================================
echo    AI SQL TUTOR - STABIL BASLATICI (V22)
echo ==========================================
echo.

:: 1. Sistem Kontrolleri
echo [1/4] Sistem Kontrolleri Yapiliyor...
set "JAVA_PATH=C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot"
if exist "!JAVA_PATH!\bin\java.exe" (
    set "JAVA_HOME=!JAVA_PATH!"
    set "PATH=!JAVA_HOME!\bin;!PATH!"
)

:: Node.js Kontrolü
node -v >nul 2>&1
if errorlevel 1 (
    echo [HATA] Node.js bulunamadi! Lutfen Node.js yukleyin.
    pause
    exit /b
)

:: 2. Port Temizliği (3011 ve 9101)
echo [2/4] Eski calismalar temizleniyor...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :3011') do taskkill /f /pid %%a >nul 2>&1
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :9101') do taskkill /f /pid %%a >nul 2>&1
echo [OK] Portlar hazir.

:: 3. Başlatma
echo [3/4] Uygulamalar baslatiliyor...

:: BACKEND
echo [BACKEND] Baslatiliyor (Port: 9101)...
start "SQL-Tutor-Backend" /D "%~dp0backend" cmd /k "chcp 65001 >nul & echo BACKEND PENCERESI - KAPATMAYIN! & echo. & .\mvnw.cmd spring-boot:run"

:: FRONTEND
echo [FRONTEND] Baslatiliyor (Port: 3011)...
start "SQL-Tutor-Frontend" /D "%~dp0frontend" cmd /k "chcp 65001 >nul & echo FRONTEND PENCERESI - KAPATMAYIN! & echo. & npm run dev"

:: BEKLE VE KONTROL ET
echo [BILGI] Servislerin hazir olmasi bekleniyor (90sn limit)...
set "count=0"
:wait_services
set /a count+=1
if !count! GTR 90 (
    echo.
    echo [HATA] Servisler zamaninda baslatilamadi.
    echo Lutfen acilan diger siyah pencereleri kontrol edin.
    pause
    exit /b
)
timeout /t 2 >nul

:: PowerShell ile kesin kontrol
powershell -Command "try { $c = New-Object System.Net.Sockets.TcpClient; $c.Connect('127.0.0.1', 9101); $c.Close(); exit 0 } catch { exit 1 }"
set "be_ready=%errorlevel%"

powershell -Command "try { $c = New-Object System.Net.Sockets.TcpClient; $c.Connect('127.0.0.1', 3011); $c.Close(); exit 0 } catch { exit 1 }"
set "fe_ready=%errorlevel%"

if %be_ready% equ 0 if %fe_ready% equ 0 (
    echo.
    echo [OK] Tüm servisler hazir!
    goto launch_browser
)
<nul set /p=.
goto wait_services

:launch_browser
:: Tarayıcıyı Aç
timeout /t 2 >nul
echo [BILGI] Tarayici aciliyor: http://127.0.0.1:3011
start http://127.0.0.1:3011

echo.
echo ==========================================
echo   PROJE BASARIYLA BASLATILDI!
echo ==========================================
echo.
echo Backend:  http://127.0.0.1:9101
echo Frontend: http://127.0.0.1:3011
pause
