package com.sqltutor.backend.controller;

import com.sqltutor.backend.dto.SqlQueryRequest;
import com.sqltutor.backend.dto.SqlQueryResponse;
import com.sqltutor.backend.service.AiService;
import com.sqltutor.backend.service.SqlExecutionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tutor")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class TutorController {

    private final AiService aiService;
    private final SqlExecutionService sqlExecutionService;

    public TutorController(AiService aiService, SqlExecutionService sqlExecutionService) {
        this.aiService = aiService;
        this.sqlExecutionService = sqlExecutionService;
    }

    @PostMapping(value = "/query", produces = "application/json;charset=UTF-8")
    public SqlQueryResponse handleQuery(@RequestBody SqlQueryRequest request) {
        System.out.println("Gelen Sorgu: " + request.getNaturalLanguageQuery());
        String sql = "";
        try {
            sql = aiService.generateSql(request.getNaturalLanguageQuery());
            System.out.println("Üretilen SQL: " + sql);
            List<Map<String, Object>> results = sqlExecutionService.executeQuery(sql);
            System.out.println("Sonuç sayısı: " + (results != null ? results.size() : 0));
            String explainPlan = sqlExecutionService.getExplainPlan(sql);
            String suggestions = aiService.getPerformanceSuggestions(sql);
            
            return SqlQueryResponse.builder()
                    .generatedSql(sql)
                    .results(results)
                    .explainPlan(explainPlan)
                    .performanceSuggestions(suggestions)
                    .miniTasks(aiService.generateMiniTasks())
                    .build();
        } catch (Exception e) {
            System.err.println("Sorgu hatası: " + e.getMessage());
            String errorExplanation = aiService.explainError(request.getNaturalLanguageQuery(), e.getMessage());
            return SqlQueryResponse.builder()
                    .generatedSql(sql)
                    .errorExplanation(errorExplanation)
                    .miniTasks(aiService.generateMiniTasks())
                    .build();
        }
    }

    @GetMapping(value = "/tasks", produces = "application/json;charset=UTF-8")
    public List<String> getTasks() {
        return aiService.generateMiniTasks();
    }

    @GetMapping("/health")
    public String health() {
        return "Backend is running on port 9101";
    }
}
