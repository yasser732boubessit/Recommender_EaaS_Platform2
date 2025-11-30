package com.example.manager.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class EventNotification extends JsonNotification {
	
    private long EventId;

    public void setEventId(long eventId) {
        this.EventId = eventId;
    }
    public long getEventId() {
        return EventId;
    }


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



    	public EventNotification(long EventId, String actionType, long itemId, long userId, long timestamp, long geoUser, long deviceType) {
        this.notificationType = "event";
        this.actionType = actionType;
        this.itemId = itemId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.geoUser = geoUser;
        this.deviceType = deviceType;
		this.EventId = EventId;

    }

	@Override
	public void postProcess() {
		this.setNotificationType("event");
	}
}
