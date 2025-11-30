package com.example.replyservice.tools;

import java.util.List;
import java.util.Map;

import com.example.replyservice.model.EventNotification;
import com.example.replyservice.model.ItemNotification;
import com.example.replyservice.model.RequestResponse;

import org.springframework.stereotype.Component;

@Component
public class NotificationParser {

    public static ItemNotification parseItem(Map<String, Object> values) {
        String itemType = getString(values, "itemType");
        String domain = getString(values, "domain");
        String createdAtString = getString(values, "createdAtString");
        long itemId = getLong(values, "itemId");
        boolean recommendable = getBoolean(values, "recommendable");
        String url = getString(values, "url");
        String title = getString(values, "title");
        String category = getString(values, "category");
        String text = getString(values, "text");
        String keywords = getString(values, "keywords");
        String categoriesRaw = getString(values, "categoriesRaw");

        return new ItemNotification(itemType, domain, createdAtString, itemId, recommendable, url, title, category, text, keywords, categoriesRaw);
    }

    public static EventNotification parseEvent(Map<String, Object> values) {
        String actionType = getString(values, "actionType");
        long itemId = getLong(values, "itemId");
        long userId = getLong(values, "userId");
        long timestamp = getLong(values, "timestamp");
        long geoUser = getLong(values, "geoUser");
        long deviceType = getLong(values, "deviceType");

        return new EventNotification(actionType, itemId, userId, timestamp, geoUser, deviceType);
    }

    public static RequestResponse parseRequest(Map<String, Object> values) {
        long requestID = getLong(values, "requestID");
        long itemId = getLong(values, "itemId");
        long userId = getLong(values, "userId");
        long timestamp = getLong(values, "timestamp");
        long geoUser = getLong(values, "geoUser");
        long deviceType = getLong(values, "deviceType");

        return new RequestResponse(requestID, itemId, userId, timestamp, geoUser, deviceType);
    }

    private static String getString(Map<String, Object> values, String key) {
        Object value = values.get(key);
        return value != null ? value.toString() : "";
    }

    private static long getLong(Map<String, Object> values, String key) {
        Object value = values.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String && !((String) value).isEmpty()) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing long from String: " + value);
            }
        }
        return 0L;
    }

    private static boolean getBoolean(Map<String, Object> values, String key) {
        Object value = values.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return false;
    }
}
