# Java Kurulum Rehberi (Alternatif ve Hızlı Çözüm)

Tarayıcı üzerinden indirme hatası (ERR_ABORTED) alıyorsanız, Windows'un kendi paket yöneticisini (**winget**) kullanarak Java'yı saniyeler içinde kurabilirsiniz.

## 1. En Hızlı Çözüm: Winget ile Kurulum
Bir terminal (PowerShell veya Komut İstemi) açın ve şu komutu yapıştırın:

```powershell
winget install -e --id EclipseAdoptium.Temurin.17.JDK
```

Bu komut:
- Java 17'yi otomatik indirir.
- Kurulumu başlatır.
- PATH ve JAVA_HOME ayarlarını sizin yerinize yapmaya çalışır.

## 2. Kurulumdan Sonra
Kurulum bittiğinde **tüm açık terminal pencerelerini kapatın** ve yeni bir tane açın. Şu komutla kontrol edin:

```powershell
java -version
```

## 3. Eğer Winget Çalışmazsa (Manuel Linkler)
Eğer winget sisteminizde yoksa, şu alternatif güvenilir kaynaklardan birini deneyin:
1. [Amazon Corretto 17 (MSI İndir)](https://corretto.aws/downloads/latest/amazon-corretto-17-x64-windows-jdk.msi)
2. [Microsoft Build of OpenJDK 17](https://aka.ms/download-jdk/microsoft-jdk-17.0.10-windows-x64.msi)

## 4. Projeyi Başlatın
Java kurulduktan sonra projenin ana dizinindeki `start.bat` dosyasına çift tıklayarak sistemi ayağa kaldırabilirsiniz.
