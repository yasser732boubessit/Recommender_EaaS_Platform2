package com.hemza.master.recsClient.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hemza.master.recsClient.model.RequestResponse;
import com.hemza.master.recsClient.service.RecsService;

import com.hemza.master.recsClient.data.*;


import java.util.Map;
import com.hemza.master.recsClient.config.*;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.hemza.master.recsClient.model.RequestResponse;
import com.hemza.master.recsClient.service.RecsService;

@RestController
public class RecsController {

	private final RecsService recsService;

	public RecsController(RecsService recsService) {
		this.recsService = recsService;
	}

	// Unified dispatcher for notifications
	@PostMapping("/notifyV2")
	public ResponseEntity<String> dispatchNotification(@RequestBody Map<String, Object> notification) {
		Object typeObj = notification.get("notificationType");

		if (typeObj == null) {
			return ResponseEntity.badRequest().body("Missing field: notificationType");
		}

		String type = typeObj.toString();

		switch (type.toLowerCase()) {
		case "item":
			recsService.processItemNotification(notification);
			return ResponseEntity.ok("Item notification processed");
		case "event":
			recsService.processEventNotification(notification);
			return ResponseEntity.ok("Event notification processed");
		default:
			return ResponseEntity.badRequest().body("Unsupported notificationType: " + type);
		}
	}

	// Endpoint to handle recommendation requests

@PostMapping("/recommend")
public RequestResponse processRecommendationRequest(@RequestBody Map<String, Object> payload) {
    try {
        ObjectMapper objectMapper = new ObjectMapper();
      // RecsEngineConfig.engineType = (String) payload.get("engineType");
        Request requestData = objectMapper.convertValue(payload.get("request"), Request.class);

        int historique = (int) payload.get("historique");
        String algorithm = (String) payload.get("algorithm");
        String tester = (String) payload.get("tester");
        long   idRequest=  requestData.getidRequest();
        System.out.println("✅ idRequest: " + idRequest);

        RequestResponse resResp = recsService.processRecommendationRequest(requestData, algorithm);
        resResp.setHistorique(historique);
        resResp.setAlgorithm(algorithm);
        resResp.setTester(tester);
        resResp.setrequestID(idRequest);
        System.out.println("✅ RequestResponse: " + algorithm);
    
        try {
            String url = "http://localhost:9004/interaction/send-resResp";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RequestResponse> requestEntity = new HttpEntity<>(resResp, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            System.out.println("✅ resResp sent successfully: " + response.getBody());
        } catch (Exception e) {
            System.err.println("❌ Failed to send resResp: " + e.getMessage());
        }
        System.out.println("✅ resResp.toString(: " + resResp.toString());

        return resResp;
    } catch (Exception e) {
        System.err.println("❌ Error processing request: " + e.getMessage());
        return new RequestResponse();
    }
}

}
