package com.example.replyservice.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class RequestResponse extends JsonNotification {
	
	
	private long requestID;
	private long itemId;

	private long userId;

	private long timestamp;

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
	
	@Override
	public void postProcess() {
		this.setNotificationType("response");
	}
}
