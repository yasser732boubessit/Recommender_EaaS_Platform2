package com.hemza.master.recsClient.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.hemza.master.recsClient.algorithm.Algorithm;
import com.hemza.master.recsClient.data.Event;
import com.hemza.master.recsClient.data.Item;
import com.hemza.master.recsClient.data.NotificationParser;
import com.hemza.master.recsClient.data.Request;
import com.hemza.master.recsClient.model.RequestResponse;

@Service
public class RecsService {

	private final Algorithm recsEngine;

	// Inject your recommendation engine (could be a mock or real implementation)
	public RecsService(Algorithm recsEngine) {
		this.recsEngine = recsEngine;
	}

	// Handle item notification (e.g., train/update model)
	public void processItemNotification(Map<String, Object> itemData) {
		Item item = NotificationParser.toItem(itemData);
		recsEngine.handleItemUpdate(item); // assuming method exists
	}

	// Handle event notification (e.g., user interactions)
	public void processEventNotification(Map<String, Object> eventData) {
		Event event = NotificationParser.toEvent(eventData);
		recsEngine.handleEventNotification(event);
	}

	// Handle event notification (e.g., user interactions)
	public RequestResponse processRecommendationRequest(Map<String, Object> requestData) {
		Request request = NotificationParser.toRequest(requestData);
		
		RequestResponse resResp = new RequestResponse();
		// to complete ...
		resResp.setUserId(request.getId_user());
		resResp.setItemId(request.getCurrentItemId());
		//resResp.setTimestamp(requestData.getTime());
			
		List<Long> recs = new ArrayList<Long>(recsEngine.getRecommendations(request));
		resResp.setRecs(recs);
		
		System.out.println(recs);
		return resResp;
	}
    public RequestResponse processRecommendationRequest(Request request, String algorithmType) {
    System.out.println("Request: " + request.getId_user());    
	System.out.println("Request: " + request.getCurrentItemId());    

		RequestResponse resResp = new RequestResponse();
		// to complete ...
		resResp.setUserId(request.getId_user());
		resResp.setItemId(request.getCurrentItemId());
		//resResp.setTimestamp(requestData.getTime());
		List<Long> recs = new ArrayList<Long>(recsEngine.getRecommendations(request));
	resResp.setRecs(recs);
		
		System.out.println(recs);
		return resResp;
    }

	

}
