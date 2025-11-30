package com.example.replyservice;



import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.replyservice.model.*;

import com.example.replyservice.tools.NotificationParser;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

@Component
public class InfluxdbRepository {

	private final InfluxDBClient influxDBClient;

	public InfluxdbRepository(InfluxDBClient influxDBClient) {
		this.influxDBClient = influxDBClient;
	}

	public List<JsonNotification> findNotificationsByTimeWindow(int windowSizeInMinutes, Instant startTime) {

		Duration pageSize = Duration.ofMinutes(windowSizeInMinutes);

		Instant stop = startTime.plus(pageSize);

		List<JsonNotification> notifications = new ArrayList<>();

		List<FluxTable> tables = findByTimeIntervale(startTime, stop);
		for (FluxTable fluxTable : tables) {
			List<FluxRecord> records = fluxTable.getRecords();
			for (FluxRecord fluxRecord : records) {
				String type = (String) fluxRecord.getValueByKey("type");
				if ("item".equals(type)) {
					ItemNotification item = NotificationParser.parseItem(fluxRecord.getValues());
					notifications.add(item);
				} else if ("event".equals(type)) {
					EventNotification event = NotificationParser.parseEvent(fluxRecord.getValues());
					//System.out.println(event.toString());
					notifications.add(event);
				} else if ("request".equals(type)) {
					RequestResponse request = NotificationParser.parseRequest(fluxRecord.getValues());
					System.out.println(request.toString());
					notifications.add(request);
				}
			}
		}

		return notifications;

	}

	public List<FluxTable> findByTimePage(int pageSizeInHours, Instant startTime) {

		Duration pageSize = Duration.ofHours(pageSizeInHours);

		Instant stop = startTime.plus(pageSize);

		return findByTimeIntervale(startTime, stop);

	}

	public List<FluxTable> findByTimeIntervale(Instant startTime, Instant stopTime) {

		QueryApi queryApi = influxDBClient.getQueryApi();

		String fluxQuery = String.format("""
				    from(bucket: "my-bucket")
				      |> range(start: %s, stop: %s)
				       |> filter(fn: (r) => r._measurement == "notificationsV4")
				       |> sort(columns: ["_time"])
				       |> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value")
					   |> filter(fn: (r) => r["type"] == "event")

				""", startTime.toString(), stopTime.toString());

		// Query data
		//
		List<FluxTable> tables = queryApi.query(fluxQuery);

		return tables;

	}
    public List<JsonNotification> findNotificationsBetween(String fromDateStr, String toDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        LocalDateTime fromDate = LocalDateTime.parse(fromDateStr, formatter);
        LocalDateTime toDate = LocalDateTime.parse(toDateStr, formatter);

        Instant fromInstant = fromDate.toInstant(ZoneOffset.UTC);
        Instant toInstant = toDate.toInstant(ZoneOffset.UTC);

        List<JsonNotification> notifications = new ArrayList<>();

        List<FluxTable> tables = findByTimeIntervale1(fromInstant, toInstant);

        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                String type = (String) fluxRecord.getValueByKey("type");
	 	        if ("event".equals(type)) {
				   EventNotification event = NotificationParser.parseEvent(fluxRecord.getValues());
			       notifications.add(event);
		    	} else  if ("item".equals(type)) {
                    ItemNotification item = NotificationParser.parseItem(fluxRecord.getValues());
                    notifications.add(item);

                } else if ("request".equals(type)) {
                    RequestResponse request = NotificationParser.parseRequest(fluxRecord.getValues());
                    notifications.add(request);
                }
            }
        }

        return notifications;
    }

    private List<FluxTable> findByTimeIntervale1(Instant from, Instant to) {
        String fluxQuery = String.format("""
				from(bucket: "my-bucket")
				  |> range(start: %s, stop: %s)
					|> filter(fn: (r) => r["_measurement"] == "notificationsV4")
					|> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value")
  					|> sort(columns: ["_time"])

				""", from.toString(), to.toString()
        );
	//					|> filter(fn: (r) => r["_field"] == "actionType" or r["_field"] == "categories" or r["_field"] == "deviceType" or r["_field"] == "domain" or r["_field"] == "userId" or r["_field"] == "url" or r["_field"] == "title" or r["_field"] == "recommendable" or r["_field"] == "text" or r["_field"] == "geoUser" or r["_field"] == "itemId" or r["_field"] == "itemType" or r["_field"] == "keywords" or r["_field"] == "limit")

//					|> filter(fn: (r) => r["type"] == "item" or r["type"] == "request" or r["type"] == "event")

        return influxDBClient.getQueryApi().query(fluxQuery);
    }


}




