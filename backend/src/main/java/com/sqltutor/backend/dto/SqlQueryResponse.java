package com.sqltutor.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SqlQueryResponse {
    private String generatedSql;
    private List<Map<String, Object>> results;
    private String explainPlan;
    private String performanceSuggestions;
    private String errorExplanation;
    private List<String> miniTasks;
}
