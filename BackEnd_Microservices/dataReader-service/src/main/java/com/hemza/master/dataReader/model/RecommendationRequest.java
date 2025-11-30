package com.hemza.master.dataReader.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class RecommendationRequest extends Notification {

	private long itemId;
	private long userId;
	private long geoUser;
	private long deviceType;
	private int limit;

	public RecommendationRequest(long itemId, long userId, long timestamp, long geoUser, long deviceType, int limit) {
		this.notificationType = "request";
		this.itemId = itemId;
		this.userId = userId;
		this.timestamp = timestamp;
		this.geoUser = geoUser;
		this.deviceType = deviceType;
		this.limit = limit;
	}

	public RecommendationRequest(Event event, int limit) {
		this.notificationType = "request";
		this.itemId = event.getItemId();
		this.userId = event.getUserId();
		this.timestamp = event.getTimestamp();
		this.geoUser = event.getUserId();
		this.deviceType = event.getDeviceType();
		this.limit = limit;
	}

	@Override
	public void postProcess() {
		this.setNotificationType("request");
	}

}
