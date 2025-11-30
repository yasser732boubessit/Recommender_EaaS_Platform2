package com.example.manager.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@NoArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "historiques")
public class historique {

    
    @Id
    private String id;
    private String algorithm;
    private String expérimentation;
    private String windowSize;
    private String data;
    private String startDate;
    private String endDate;
    private int historiqueId;
    private int Top_k;


    private Map<String, Double> metrics;

    public Map<String, Double> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Double> metrics) {
        this.metrics = metrics;
    }


    public historique(Map<String, Double> metrics,String algorithm, String expérimentation, String data, String windowSize, String startDate, String endDate,     int historiqueId,int Top_k) {
        this.algorithm = algorithm;
        this.expérimentation = expérimentation;
        this.data = data;
        this.windowSize = windowSize;
        this.startDate = startDate;
        this.endDate = endDate;
        this.historiqueId = historiqueId;
        this.metrics = metrics;
        this.Top_k = Top_k;   
    }

    // Getters and   Setters for all fields
    public String getAlgorithm() {
        return algorithm;
    }
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    } 
    

    public String getexpérimentation() {
        return expérimentation;
    }

    public void setexpérimentation(String expérimentation) {
        this.expérimentation = expérimentation;
    }

    public String getdata() {
        return data;
    }

    public void setdata(String data) {
        this.data = data;
    }

    public String getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(String windowSize) {
        this.windowSize = windowSize;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getHistoriqueId() {
        return historiqueId;
    }

    public void setHistoriqueId(int historiqueId) {
        this.historiqueId = historiqueId;
    }



}
