package com.hemza.master.evaluation.service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.hemza.master.evaluation.metrics.CTRAtK;
import com.hemza.master.evaluation.metrics.F1AtK;
import com.hemza.master.evaluation.metrics.Metric;
import com.hemza.master.evaluation.metrics.NDCGAtK;
import com.hemza.master.evaluation.metrics.PrecisionAtK;
import com.hemza.master.evaluation.metrics.RecallAtK;

@Service
public class EvaluationService {

	public Map<String, Double> evaluate(List<Long> recommended, List<Long> relevant, int k) {
		List<Metric> metrics = Arrays.asList(new PrecisionAtK(k), new RecallAtK(k), new F1AtK(k), new NDCGAtK(k),
				new CTRAtK(k));
				System.out.println("📥 recommended "+recommended.toString());
				System.out.println("📥 relevant "+relevant.toString());

		Map<String, Double> results = new LinkedHashMap<>();
		for (Metric metric : metrics) {
			results.put(metric.getClass().getSimpleName(), metric.compute(recommended, relevant));
			System.out.println("📥 metric.getClass() "+results.toString());

		}
		return results;
	}

}
