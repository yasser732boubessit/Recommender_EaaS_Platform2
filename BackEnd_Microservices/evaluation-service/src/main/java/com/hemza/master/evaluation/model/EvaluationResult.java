package com.hemza.master.evaluation.model;

import java.util.Map;

public class EvaluationResult {
    private Map<String, Double> metrics;

    public EvaluationResult(Map<String, Double> metrics) {
        this.metrics = metrics;
    }

    public Map<String, Double> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Double> metrics) {
        this.metrics = metrics;
    }
}
