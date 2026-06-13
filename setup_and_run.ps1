# AI SQL Tutor - Kurulum ve Çalıştırma Rehberi

Write-Host "--- AI SQL Tutor Kurulum Kontrolü ---" -ForegroundColor Cyan

# 1. Node.js Kontrolü
if (Get-Command node -ErrorAction SilentlyContinue) {
    Write-Host "[OK] Node.js yüklü." -ForegroundColor Green
} else {
    Write-Host "[HATA] Node.js yüklü değil! Lütfen https://nodejs.org/ adresinden indirin." -ForegroundColor Red
}

# 2. Java Kontrolü
if (Get-Command java -ErrorAction SilentlyContinue) {
    Write-Host "[OK] Java yüklü." -ForegroundColor Green
} else {
    Write-Host "[HATA] Java 17+ yüklü değil!" -ForegroundColor Red
    Write-Host "Lütfen şu adresten indirin: https://adoptium.net/" -ForegroundColor Cyan
    Write-Host "Kurulum sırasında 'Set JAVA_HOME' seçeneğini işaretlemeyi unutmayın." -ForegroundColor Yellow
}

# 3. Maven Kontrolü
if (Get-Command mvn -ErrorAction SilentlyContinue) {
    Write-Host "[OK] Maven yüklü." -ForegroundColor Green
} else {
    Write-Host "[UYARI] Maven yüklü değil. Backend'i çalıştırmak için Maven gereklidir." -ForegroundColor Yellow
}

Write-Host "`n--- Adımlar ---" -ForegroundColor Cyan
Write-Host "1. Frontend bağımlılıklarını yüklüyorum..."
cd frontend
npm install
cd ..

Write-Host "`n2. Backend'i çalıştırmak için (Yeni bir terminalde):" -ForegroundColor White
Write-Host "   cd backend"
Write-Host "   ./mvnw spring-boot:run"

Write-Host "`n3. Frontend'i çalıştırmak için (Yeni bir terminalde):" -ForegroundColor White
Write-Host "   cd frontend"
Write-Host "   npm run dev"

Write-Host "`nNOT: Veritabanı olarak H2 (bellek içi) kullanılacak şekilde ayarlandı. Harici bir PostgreSQL gerekmez." -ForegroundColor Gray
Write-Host "NOT 2: OpenAI API anahtarınızı 'backend/src/main/resources/application.properties' dosyasına eklemeyi unutmayın!" -ForegroundColor Yellow
