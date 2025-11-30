package com.example.manager.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "responses")

public class RequestResponse extends JsonNotification {
	
     private String id;
	private int historique;
    private String algorithm;
    private String tester;

    // Getters and Setters
    public int getHistorique() {
        return historique;
    }

    public void setHistorique(int historique) {
        this.historique = historique;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getTester() {
        return tester;
    }

    public void setTester(String tester) {
        this.tester = tester;
    }


    
	public long requestID;

    public long getrequestID() {
        return requestID;
    }

    public void setrequestID(long requestID) {
        this.requestID = requestID;
    }


	
	private long itemId;

	private long userId;

	//private long timestamp;

	private long geoUser;

	private long deviceType;
	
	private List<Long> recs;
	  
	public RequestResponse(long requestID, long itemId, long userId, long timestamp, long geoUser, long deviceType) {
        this.notificationType = "response";
        this.requestID  = requestID;
        this.itemId = itemId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.geoUser = geoUser;
        this.deviceType = deviceType;
    }

    public List<Long> getRecs() { // Renamed from getRelevant to getRecs
        return recs;
    }
    
    public void setRecs(List<Long> recs) { // Renamed from setRelevant to setRecs
        this.recs = recs;
    }



	
	@Override
	public void postProcess() {
		this.setNotificationType("response");
	}
}