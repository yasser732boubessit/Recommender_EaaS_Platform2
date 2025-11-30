package com.hemza.master.evaluation.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hemza.master.evaluation.model.EvaluationRequest;
import com.hemza.master.evaluation.model.EvaluationResult;
import com.hemza.master.evaluation.service.EvaluationService;

@RestController
public class EvaluationController {
	private final EvaluationService evaluationService;
	public EvaluationController(EvaluationService evaluationService) {
		this.evaluationService = evaluationService;
	}
	// Simple GET endpoint to return a hello message
	@GetMapping("/")
	public String home() {
		return "Hello from Evaluation Service 👋";
	}
	@PostMapping("/evaluation")
	public EvaluationResult evaluate(@RequestBody EvaluationRequest request) {
		Map<String, Double> results = evaluationService.evaluate(request.getRecommended(),
		 request.getRelevant(), request.getK());
		return new EvaluationResult(results);
	}


}
