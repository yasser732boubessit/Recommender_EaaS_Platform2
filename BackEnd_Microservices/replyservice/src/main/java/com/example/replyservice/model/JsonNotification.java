package com.example.replyservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JsonNotification {

    protected String notificationType; // "event" ou "item"
    protected long timestamp;

    public void postProcess() {
        // Method to post-process notifications
    }
}
