# Yapay Zeka Destekli SQL Öğretmeni

Bu proje, kullanıcıların doğal dil kullanarak SQL sorguları yazmasını, çalıştırmasını ve öğrenmesini sağlayan bir eğitim aracıdır.

## Özellikler
- **Doğal Dil Sorgulama:** "Son 30 günde en çok satış yapanları getir" gibi cümleleri SQL'e dönüştürür.
- **SQL Çalıştırma:** Üretilen SQL'i gerçek bir PostgreSQL veritabanında çalıştırır.
- **Explain Plan:** Sorgunun veritabanı tarafından nasıl yürütüldüğünü gösterir.
- **Performans Önerileri:** Sorguyu iyileştirmek için AI destekli ipuçları sunar.
- **Hata Analizi:** Yanlış sorguların neden hatalı olduğunu açıklar.
- **Mini Görevler:** Kullanıcıya pratik yapması için günlük görevler sunar.

## Teknolojiler
- **Backend:** Java 17, Spring Boot, Spring AI, JOOQ, PostgreSQL.
- **Frontend:** React, TypeScript, Vite, Tailwind CSS (Lucide Icons).

## Kurulum ve Çalıştırma

### 1. Kolay Başlatma (Windows)
Proje kök dizinindeki `start.bat` dosyasına çift tıklayarak hem backend'i hem de frontend'i aynı anda başlatabilirsiniz.

### 2. Manuel Başlatma
Eğer sisteminizde Maven yüklü değilse, projeyle birlikte gelen Maven Wrapper'ı (`mvnw`) kullanmalısınız:

**Backend:**
```bash
cd backend
./mvnw spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm run dev
```

Uygulama `http://localhost:3000` adresinde çalışacaktır.
  API servisi ise `http://localhost:8081` adresindedir.

### 5. API Anahtarı
`backend/src/main/resources/application.properties` dosyasındaki `spring.ai.openai.api-key` kısmına OpenAI anahtarınızı ekleyin. Anahtar eklenene kadar uygulama örnek bir çıktı verecektir.

## Örnek Kullanım
Arama kutusuna şu soruları yazabilirsiniz:
- "Tüm kullanıcıları listele."
- "En pahalı 3 ürünü getir."
- "Hangi kullanıcı hangi ürünü ne zaman aldı?"
- "Son 30 gündeki toplam satış miktarını göster."
