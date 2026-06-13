# AI SQL Tutor - Robust Runner

Write-Host "--- Sistem Hazirlaniyor ---" -ForegroundColor Cyan

# 1. Java Tespiti
$javaPath = ""
$potentialPaths = @(
    "C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot",
    "C:\Program Files\Eclipse Adoptium",
    "C:\Program Files\Java",
    "C:\Program Files (x86)\Java"
)

foreach ($path in $potentialPaths) {
    if (Test-Path $path) {
        if ($path -like "*jdk-17*") {
            $javaPath = $path
            break
        }
        $subDirs = Get-ChildItem $path -Directory | Where-Object { $_.Name -like "jdk-17*" }
        if ($subDirs) {
            $javaPath = $subDirs[0].FullName
            break
        }
    }
}

if (-not $javaPath -and $env:JAVA_HOME) {
    $javaPath = $env:JAVA_HOME
}

if (-not $javaPath) {
    Write-Host "[HATA] Java 17 bulunamadi!" -ForegroundColor Red
    Write-Host "Lütfen 'java_kurulum.md' dosyasindaki talimatlari uygulayin." -ForegroundColor Yellow
    exit 1
}

Write-Host "[OK] Java bulundu: $javaPath" -ForegroundColor Green
$env:JAVA_HOME = $javaPath
$env:PATH = "$javaPath\bin;$env:PATH"

# 2. Port Temizliği (8081 ve 3000)
Write-Host "--- Port Kontrolü ---" -ForegroundColor Cyan
function Stop-ProcessOnPort($port) {
    $process = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($process) {
        Write-Host "Port $port kullanimda (PID: $($process.OwningProcess)), kapatiliyor..." -ForegroundColor Yellow
        Stop-Process -Id $process.OwningProcess -Force -ErrorAction SilentlyContinue
    }
}

Stop-ProcessOnPort 8081
Stop-ProcessOnPort 3000

# 3. Backend Baslatma
Write-Host "--- Backend Baslatiliyor ---" -ForegroundColor Cyan
$backendDir = Join-Path $PSScriptRoot "backend"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendDir'; `$env:JAVA_HOME='$javaPath'; `$env:PATH='$javaPath\bin;`$env:PATH'; ./mvnw.cmd spring-boot:run"

# 4. Frontend Baslatma
Write-Host "--- Frontend Baslatiliyor ---" -ForegroundColor Cyan
$frontendDir = Join-Path $PSScriptRoot "frontend"
if (-not (Test-Path (Join-Path $frontendDir "node_modules"))) {
    Write-Host "[BILGI] Modüller eksik, yukleniyor (bu ilk seferde birkac dakika sürebilir)..." -ForegroundColor Yellow
    Push-Location $frontendDir
    npm install
    Pop-Location
}
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$frontendDir'; npm run dev"

Write-Host "`n==========================================" -ForegroundColor Green
Write-Host "SISTEM BASLATILDI!" -ForegroundColor Green
Write-Host "WEB ARAYÜZÜ: http://localhost:3000" -ForegroundColor White
Write-Host "API SERVISI: http://localhost:8081" -ForegroundColor White
Write-Host "==========================================" -ForegroundColor Green
Write-Host "`nLütfen acilan pencereleri kapatmayin."
Write-Host "Backend penceresinde 'Started SqlTutorApplication' yazana kadar bekleyin."
pause
