package com.hemza.master.dataReader.model;

import com.opencsv.bean.CsvBindByName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Event extends Notification {
	
	@CsvBindByName(column = "actionType")
	private String actionType;

	@CsvBindByName(column = "ItemID")
	private long itemId;

	@CsvBindByName(column = "UserId")
	private long userId;

	//@CsvBindByName(column = "TimeStamp")
	//private long timestamp;

	@CsvBindByName(column = "geoUser")
	private long geoUser;

	@CsvBindByName(column = "deviceType")
	private long deviceType;
	
	public Event(String actionType, long itemId, long userId, long timestamp, long geoUser, long deviceType) {
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
