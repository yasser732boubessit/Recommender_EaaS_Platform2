package com.example.manager.data;

import java.util.Date;
import java.util.Map;

public class NotificationParser {

	public static Item toItem(Map<String, Object> map) {
		Item item = new Item();
		item.setId(asLong(map.get("itemId")));
		item.setTime(new Date(asLong(map.get("timestamp"))));
		item.setType(asString(map.get("itemType")));
		item.setDomain(asLong(map.get("domain")));
		item.setUrl(asString(map.get("url")));
		item.setCategory(asLong(map.get("category")));
		item.setText(asString(map.get("text")));
		item.setTitle(asString(map.get("title")));
		return item;
	}

	public static Event toEvent(Map<String, Object> map) {
		Event event = new Event();
		event.setId(asLong(map.get("itemId")));
		event.setTime(new Date(asLong(map.get("timestamp"))));
		event.setId_user(asLong(map.get("userId")));
		event.setGeoUser(asLong(map.get("geoUser")));
		event.setAction(asString(map.get("actionType")));
		event.setDevice_type(asLong(map.get("deviceType")));
		event.setEventId(asLong(map.get("eventId")));
		return event;
	}

	public static Request toRequest(Map<String, Object> map) {
		Request request = new Request();
		request.setId(asLong(map.get("itemId")));
		request.setTime(new Date(asLong(map.get("timestamp"))));
		request.setId_user(asLong(map.get("userId")));
		request.setGeoUser(asLong(map.get("geoUser")));
		request.setDevice_type(asLong(map.get("deviceType")));
		request.setCurrentItemId(asLong(map.get("itemId")));
		request.setLimit(asInt(map.get("limit"), 10)); // valeur par défaut = 5 si non fourni
		return request;
	}

	// Utilitaires pour cast sécurisés
	private static String asString(Object obj) {
		return obj != null ? obj.toString() : null;
	}

	private static long asLong(Object obj) {
		if (obj instanceof Number) {
			return ((Number) obj).longValue();
		}
		if (obj instanceof String) {
			try {
				return Long.parseLong((String) obj);
			} catch (NumberFormatException e) {
				return 0L;
			}
		}
		return 0L;
	}

	private static int asInt(Object obj, int defaultValue) {
		if (obj instanceof Number) {
			return ((Number) obj).intValue();
		}
		if (obj instanceof String) {
			try {
				return Integer.parseInt((String) obj);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
		return defaultValue;
	}
}
