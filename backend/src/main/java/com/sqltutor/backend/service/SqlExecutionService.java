package com.sqltutor.backend.service;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SqlExecutionService {

    private final DSLContext dsl;

    public SqlExecutionService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<Map<String, Object>> executeQuery(String sql) {
        try {
            System.out.println("[SQL-EXEC] Calistiriliyor: " + sql);
            Result<Record> result = dsl.fetch(sql);
            List<Map<String, Object>> resultsList = new ArrayList<>();
            for (Record record : result) {
                Map<String, Object> map = new HashMap<>();
                for (org.jooq.Field<?> field : record.fields()) {
                    map.put(field.getName(), record.get(field));
                }
                resultsList.add(map);
            }
            System.out.println("[SQL-EXEC] Basarili, satir sayisi: " + resultsList.size());
            return resultsList;
        } catch (Exception e) {
            System.err.println("[SQL-EXEC] HATA: " + e.getMessage());
            throw new RuntimeException("Sorgu calistirilirken hata olustu: " + e.getMessage(), e);
        }
    }

    public String getExplainPlan(String sql) {
        try {
            Result<Record> result = dsl.fetch("EXPLAIN " + sql);
            StringBuilder sb = new StringBuilder();
            for (Record record : result) {
                sb.append(record.get(0)).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "Could not generate explain plan: " + e.getMessage();
        }
    }
}
