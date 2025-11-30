package com.hemza.master.dataReader.model.repository;

import java.util.List;

import org.springframework.stereotype.Component;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;

@Component
public class InfluxdbRepository {

	private final InfluxDBClient influxDBClient;

	public InfluxdbRepository(InfluxDBClient influxDBClient) {
		this.influxDBClient = influxDBClient;
	}

	public String insertBatch(List<Point> batchPoints) {

		// Get the Write API for inserting data into InfluxDB
		WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
		writeApi.writePoints(batchPoints);
		return "✅ Written: " + batchPoints.size() + " points";
	}

}
