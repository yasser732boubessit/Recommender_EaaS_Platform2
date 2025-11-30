package com.hemza.master.dataReader.util;

import com.hemza.master.dataReader.model.Event;
import com.hemza.master.dataReader.model.Item;
import com.hemza.master.dataReader.model.Notification;

public class NotificationValidator {

    public static boolean isValid(Notification notification) {
        if (notification == null) {
            System.err.println("❌ Notification est null");
            return false;
        }

        if (notification.getNotificationType() == null || notification.getNotificationType().isBlank()) {
            System.err.println("❌ Type de notification manquant");
            return false;
        }

        if (notification.getTimestamp() <= 0) {
            System.err.println("❌ Timestamp invalide : " + notification.getTimestamp());
            return false;
        }

        if (notification instanceof Event  event) {
            return validateEvent(event);
        } else if (notification instanceof Item item) {
            return validateItem(item);
        }

        return true;
    }

    private static boolean validateEvent(Event event) {
        boolean isValid = true;

        if (event.getUserId() > Long.MAX_VALUE) {
            System.err.println("⚠️ userId trop grand : " + event.getUserId());
            isValid = false;
        }

        if (event.getItemId() > Long.MAX_VALUE) {
            System.err.println("⚠️ itemId trop grand : " + event.getItemId());
            isValid = false;
        }

        if (event.getActionType() == null || event.getActionType().isBlank()) {
            System.err.println("⚠️ actionType manquant");
            isValid = false;
        }

        return isValid;
    }

    private static boolean validateItem(Item item) {
        boolean isValid = true;

        if (item.getItemId() > Long.MAX_VALUE) {
        	System.err.println("⚠️ itemId trop grand : "+item.getItemId());
            isValid = false;
        }

        if (item.getCreatedAt() == null) {
            System.err.println("⚠️ createdAt manquant");
            isValid = false;
        }

        return isValid;
    }
}
