package com.hemza.master.dataReader.model;

import com.opencsv.bean.CsvBindByName;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Notification {

	protected String notificationType; // "event" ou "item"

	@CsvBindByName(column = "TimeStamp")
	protected long timestamp;
	
	
	public void postProcess () {
	}

}
