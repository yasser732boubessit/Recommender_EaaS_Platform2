package com.hemza.master.evaluation.metrics;

import java.util.List;

public class CTRAtK extends Metric {
	public CTRAtK(int k) {
		super(k);
	}

	@Override
	public double compute(List<Long> recommended, List<Long> relevant) {
	    int hit = 0;
	    int size = Math.min(k, recommended.size());
	    for (int i = 0; i < size; i++) {
	        if (relevant.contains(recommended.get(i))) {
	            hit++;
	        }
	    }
	    if (size == 0) {
	        return 0.0;
	    } else {
	        return (double) hit / size;
	    }
	}
}
