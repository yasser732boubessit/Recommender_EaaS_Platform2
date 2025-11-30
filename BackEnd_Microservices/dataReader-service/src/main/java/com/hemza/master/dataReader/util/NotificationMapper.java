package com.hemza.master.dataReader.util;

import com.hemza.master.dataReader.model.Event;
import com.hemza.master.dataReader.model.Item;
import com.hemza.master.dataReader.model.Notification;
import com.hemza.master.dataReader.model.RecommendationRequest;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

public class NotificationMapper {

	public static Point toPoint(Notification notification, String dbCollectionName) {
		Point point = Point.measurement(dbCollectionName).addTag("type", notification.getNotificationType())
				.time(notification.getTimestamp(), WritePrecision.MS);

		if (notification instanceof Event event) {
			mapEvent(point, event);
		} else if (notification instanceof Item item) {
			mapItem(point, item);
		} else if (notification instanceof RecommendationRequest request) {
			mapRequest(point, request);
		}

		return point;
	}

	private static void mapEvent(Point point, Event event) {
		point.addField("actionType", event.getActionType());
		point.addField("itemId", event.getItemId());
		point.addField("userId", event.getUserId());
		point.addField("geoUser", event.getGeoUser());
		point.addField("deviceType", event.getDeviceType());
	}

	private static void mapRequest(Point point, RecommendationRequest request) {
		point.addField("itemId", request.getItemId());
		point.addField("userId", request.getUserId());
		point.addField("geoUser", request.getGeoUser());
		point.addField("deviceType", request.getDeviceType());
		point.addField("limit", request.getLimit());
	}

	private static void mapItem(Point point, Item item) {

		point.addField("itemType", item.getItemType());
		point.addField("domain", item.getDomain());
		point.addField("itemId", item.getItemId());
		point.addField("recommendable", item.isRecommendable());
		point.addField("url", item.getUrl());
		point.addField("title", item.getTitle());
		// point.addField("category", item.getCategory());
		point.addField("text", item.getText());
		if (item.getCategories() != null) {
			point.addField("keywords", String.join(",", item.getKeywords()));
		}
		if (item.getCategories() != null) {
			point.addField("categories", String.join(",", item.getCategories()));
		}
	}
}
