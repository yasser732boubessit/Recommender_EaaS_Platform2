package com.example.manager.model;

import java.util.List;

public class EvaluationRequest {
    private int k;
    private List<Long> recommended;
    private List<Long> relevant;

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public List<Long> getRecommended() {
        return recommended;
    }

    public void setRecommended(List<Long> recommended) {
        this.recommended = recommended;
    }

    public List<Long> getRelevant() {
        return relevant;
    }

    public void setRelevant(List<Long> relevant) {
        this.relevant = relevant;
    }
}
