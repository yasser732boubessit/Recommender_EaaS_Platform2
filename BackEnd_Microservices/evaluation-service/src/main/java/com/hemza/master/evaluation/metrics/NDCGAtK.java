package com.hemza.master.evaluation.metrics;

import java.util.List;

public class NDCGAtK extends Metric {
	public NDCGAtK(int k) {
		super(k);
	}

	@Override
	public double compute(List<Long> recommended, List<Long> relevant) {
		double dcg = 0.0;
		for (int i = 0; i < Math.min(k, recommended.size()); i++) {
			if (relevant.contains(recommended.get(i))) {
				dcg += 1.0 / (Math.log(i + 2) / Math.log(2));
			}
		}

		int idealHits = Math.min(relevant.size(), k);
		double idcg = 0.0;
		for (int i = 0; i < idealHits; i++) {
			idcg += 1.0 / (Math.log(i + 2) / Math.log(2));
		}

		if (idcg == 0.0) {
			return 0.0;
		} else {
			return dcg / idcg;
		}
	}

}
