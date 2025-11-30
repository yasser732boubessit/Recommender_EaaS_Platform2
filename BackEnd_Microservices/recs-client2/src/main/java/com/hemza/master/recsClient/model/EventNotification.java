package com.hemza.master.recsClient.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class EventNotification extends JsonNotification {
	
	private String actionType;

	private long itemId;

	private long userId;

	//private long timestamp;

	private long geoUser;

	private long deviceType;
	
	public EventNotification(String actionType, long itemId, long userId, long timestamp, long geoUser, long deviceType) {
        this.notificationType = "event";
        this.actionType = actionType;
        this.itemId = itemId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.geoUser = geoUser;
        this.deviceType = deviceType;
    }
	
	@Override
	public void postProcess() {
		this.setNotificationType("event");
	}
}
