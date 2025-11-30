package com.example.replyservice.model;

import java.util.Random;

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
    private long geoUser;
    private long deviceType;
    private long  timestamp;
  
    public EventNotification(long EventId,String actionType, long itemId, long userId, long timestamp, long geoUser, long deviceType) {
        this.notificationType = "event";
        this.actionType = actionType;
        this.itemId = itemId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.geoUser = geoUser;
        this.deviceType = deviceType;
        this.EventId = EventId;

    }

    public EventNotification(String actionType, long itemId, long userId, long timestamp, long geoUser, long deviceType) {
        this.notificationType = "event";
        this.actionType = actionType;
        this.itemId = itemId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.geoUser = geoUser;
        this.deviceType = deviceType;
        this.EventId = generateRandomIdRequest();

    }
    

    public static long generateRandomIdRequest() {
        long currentTimeMillis = System.currentTimeMillis();
        Random random = new Random();
        int randomNumber = random.nextInt(1001);
        return Long.parseLong(currentTimeMillis + "" + randomNumber);
    }


    @Override
    public void postProcess() {
        this.setNotificationType("event");
    }
   
   
    public String getActionType() {
        return actionType;
    }

    public long getItemId() {
        return itemId;
    }
    public long getTimestamp() {
        return  timestamp;
    }
    public long getUserId() {
        return userId;
    }
    
    public long getGeoUser() {
        return geoUser;
    }
    
    public long getDeviceType() {
        return deviceType;
    }
}
