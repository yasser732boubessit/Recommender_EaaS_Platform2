package com.hemza.master.evaluation.metrics;

import java.util.List;

public class F1AtK extends Metric {
	public F1AtK(int k) {
		super(k);
	}

	@Override
	public double compute(List<Long> recommended, List<Long>relevant) {
		double precision = new PrecisionAtK(k).compute(recommended, relevant);
		double recall = new RecallAtK(k).compute(recommended, relevant);

		if (precision + recall == 0.0) {
			return 0.0;
		} else {
			return 2 * (precision * recall) / (precision + recall);
		}
	}

}
