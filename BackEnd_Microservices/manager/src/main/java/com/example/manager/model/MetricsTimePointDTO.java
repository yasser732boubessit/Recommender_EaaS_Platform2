package com.example.manager.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class MetricsTimePointDTO {
    private long timestamp;
    private Map<String, Double> metrics;
}

