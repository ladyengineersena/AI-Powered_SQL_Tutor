package com.sqltutor.backend.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private final ChatModel chatModel;
    
    @org.springframework.beans.factory.annotation.Value("${spring.ai.openai.api-key}")
    private String apiKey;

    private final String SCHEMA_CONTEXT = """
        Database schema:
        - users (id, name, email, created_at)
        - products (id, name, price, category)
        - sales (id, user_id, product_id, amount, sale_date)
        
        Always return only the SQL query for the natural language input. 
        If the query is complex, ensure it is optimized for PostgreSQL.
        """;

    public AiService(org.springframework.beans.factory.ObjectProvider<ChatModel> chatModelProvider) {
        this.chatModel = chatModelProvider.getIfAvailable();
    }

    private String normalize(String input) {
        if (input == null) return "";
        return input.toLowerCase()
                .replace("ı", "i")
                .replace("İ", "i")
                .replace("ğ", "g")
                .replace("Ğ", "g")
                .replace("ü", "u")
                .replace("Ü", "u")
                .replace("ş", "s")
                .replace("Ş", "s")
                .replace("ö", "o")
                .replace("Ö", "o")
                .replace("ç", "c")
                .replace("Ç", "c");
    }

    public String generateSql(String nlQuery) {
        if (chatModel == null || "dummy-key-to-prevent-crash".equals(apiKey)) {
            String query = normalize(nlQuery);
            System.out.println("[AI-MOCK] Normalize edilmis sorgu: " + query);
            
            // Daha akıllı Mock mantığı (Normalize edilmiş kelimelerle)
            if (query.contains("30 gun") || query.contains("30 gunde") || query.contains("son ay")) {
                // Ürün bazlı sorgular
                if (query.contains("urun") || query.contains("product")) {
                    if (query.contains("en cok")) {
                        return """
                            SELECT p.name as "Ürün", SUM(s.amount) as "Toplam Satış" 
                            FROM products p 
                            JOIN sales s ON p.id = s.product_id 
                            WHERE s.sale_date >= DATEADD('DAY', -30, CURRENT_TIMESTAMP)
                            GROUP BY p.name 
                            ORDER BY "Toplam Satış" DESC
                            LIMIT 1;
                            """;
                    }
                    if (query.contains("en az")) {
                        return """
                            SELECT p.name as "Ürün", SUM(s.amount) as "Toplam Satış" 
                            FROM products p 
                            JOIN sales s ON p.id = s.product_id 
                            WHERE s.sale_date >= DATEADD('DAY', -30, CURRENT_TIMESTAMP)
                            GROUP BY p.name 
                            ORDER BY "Toplam Satış" ASC
                            LIMIT 1;
                            """;
                    }
                }
                // Kullanıcı bazlı sorgular
                if (query.contains("kullanici") || query.contains("user") || query.contains("uye") || query.contains("musteri")) {
                    if (query.contains("en cok")) {
                        return """
                            SELECT u.name as "Kullanıcı", SUM(s.amount) as "Toplam Satış" 
                            FROM users u 
                            JOIN sales s ON u.id = s.user_id 
                            WHERE s.sale_date >= DATEADD('DAY', -30, CURRENT_TIMESTAMP)
                            GROUP BY u.name 
                            ORDER BY "Toplam Satış" DESC
                            LIMIT 1;
                            """;
                    }
                    if (query.contains("en az")) {
                        return """
                            SELECT u.name as "Kullanıcı", SUM(s.amount) as "Toplam Satış" 
                            FROM users u 
                            JOIN sales s ON u.id = s.user_id 
                            WHERE s.sale_date >= DATEADD('DAY', -30, CURRENT_TIMESTAMP)
                            GROUP BY u.name 
                            ORDER BY "Toplam Satış" ASC
                            LIMIT 1;
                            """;
                    }
                }
                if (query.contains("satis") || query.contains("ciro")) {
                    return """
                        SELECT u.name as "Kullanıcı", SUM(s.amount) as "Toplam Satış" 
                        FROM users u 
                        JOIN sales s ON u.id = s.user_id 
                        WHERE s.sale_date >= DATEADD('DAY', -30, CURRENT_TIMESTAMP)
                        GROUP BY u.name 
                        ORDER BY "Toplam Satış" DESC;
                        """;
                }
            }
            
            if (query.contains("en ucuz") || query.contains("dusuk fiyat") || query.contains("ucuz urun")) {
                return "SELECT name, price, category FROM products ORDER BY price ASC LIMIT 1;";
            }

            if (query.contains("en pahali") || query.contains("yuksek fiyat") || query.contains("pahali urun")) {
                return "SELECT name, price, category FROM products ORDER BY price DESC LIMIT 1;";
            }

            if (query.contains("toplam satis") || query.contains("toplami") || query.contains("toplam ciro")) {
                 return "SELECT SUM(amount) as \"Toplam Ciro\" FROM sales;";
            }

            if (query.contains("kullanici") || query.contains("uye") || query.contains("user") || query.contains("musteri")) {
                 return "SELECT id, name, email, created_at FROM users;";
            }

            if (query.contains("urun") || query.contains("product") || query.contains("stok")) {
                 return "SELECT id, name, price, category FROM products;";
            }

            if (query.contains("satis") || query.contains("siparis") || query.contains("sale") || query.contains("islem")) {
                 return "SELECT s.id, u.name as \"Müşteri\", p.name as \"Ürün\", s.amount, s.sale_date FROM sales s JOIN users u ON s.user_id = u.id JOIN products p ON s.product_id = p.id;";
            }
             
            if (query.contains("kategori") || query.contains("category") || query.contains("tur")) {
                 return "SELECT category, COUNT(*) as \"Ürün Sayısı\", AVG(price) as \"Ortalama Fiyat\" FROM products GROUP BY category;";
            }
            
            return "SELECT * FROM users; -- [MOCK] Sorgu tam anlaşılamadı ama temel tablo getirildi.";
        }
        try {
            String template = """
                {schema}
                User input: {query}
                Return only the SQL query.
                """;
            PromptTemplate promptTemplate = new PromptTemplate(template);
            Prompt prompt = promptTemplate.create(Map.of("schema", SCHEMA_CONTEXT, "query", nlQuery));
            return chatModel.call(prompt).getResult().getOutput().getContent().replace("```sql", "").replace("```", "").trim();
        } catch (Exception e) {
            return "SELECT * FROM users; -- AI Hatası: " + e.getMessage();
        }
    }

    public String getPerformanceSuggestions(String sql) {
        if (chatModel == null || "dummy-key-to-prevent-crash".equals(apiKey)) {
            return "1. Tablolarda indeks kullanımını kontrol edin.\n2. Gereksiz sütunları (SELECT *) yerine isimle çağırın.\n3. Join işlemlerinde foreign key sütunlarını kullandığınızdan emin olun.";
        }
        try {
            String template = """
                Analyze this SQL query for performance improvements in PostgreSQL:
                {sql}
                Provide 2-3 concise suggestions.
                """;
            PromptTemplate promptTemplate = new PromptTemplate(template);
            Prompt prompt = promptTemplate.create(Map.of("sql", sql));
            return chatModel.call(prompt).getResult().getOutput().getContent();
        } catch (Exception e) {
            return "Öneri alınamadı: " + e.getMessage();
        }
    }

    public String explainError(String nlQuery, String error) {
        if (chatModel == null || "dummy-key-to-prevent-crash".equals(apiKey)) {
            return "Veritabanı hatası oluştu: " + error + ". Lütfen SQL sözdizimini kontrol edin veya tabloların mevcut olduğundan emin olun.";
        }
        try {
            String template = """
                The user wanted to: {query}
                But the execution failed with error: {error}
                Explain why it failed and what was wrong with the generated SQL.
                """;
            PromptTemplate promptTemplate = new PromptTemplate(template);
            Prompt prompt = promptTemplate.create(Map.of("query", nlQuery, "error", error));
            return chatModel.call(prompt).getResult().getOutput().getContent();
        } catch (Exception e) {
            return "Hata analizi yapılamadı: " + e.getMessage();
        }
    }

    public List<String> generateMiniTasks() {
        if (chatModel == null || "dummy-key-to-prevent-crash".equals(apiKey)) return Arrays.asList("1. Tüm kullanıcıları getir", "2. Satışları listele", "3. En pahalı ürünleri bul");
        try {
            String template = """
                Generate 3 mini SQL tasks/challenges for a student to learn SQL in Turkish.
                Example: "Find all products with price > 100".
                Return as a simple list in Turkish.
                """;
            Prompt prompt = new Prompt(template);
            String content = chatModel.call(prompt).getResult().getOutput().getContent();
            return Arrays.asList(content.split("\n"));
        } catch (Exception e) {
            return Arrays.asList("1. Tüm kullanıcıları getir", "2. Satışları listele", "3. En pahalı ürünleri bul");
        }
    }
}
