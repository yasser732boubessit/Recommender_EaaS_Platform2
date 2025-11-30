package com.hemza.master.evaluation.metrics;

import java.util.List;

public abstract class Metric {
	protected int k;

	public Metric(int k) {
		this.k = k;
	}

	// Méthode à implémenter
	public abstract double compute(List<Long> recommended, List<Long> relevant);
}
	

