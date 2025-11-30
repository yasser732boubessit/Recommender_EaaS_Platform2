package com.hemza.master.dataReader.service;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hemza.master.dataReader.model.Event;
import com.hemza.master.dataReader.model.Item;
import com.hemza.master.dataReader.model.Notification;
import com.hemza.master.dataReader.model.RecommendationRequest;
import com.hemza.master.dataReader.model.repository.InfluxdbRepository;
import com.hemza.master.dataReader.util.NotificationMapper;
import com.influxdb.client.write.Point;
import com.opencsv.CSVParser;
import com.opencsv.bean.CsvToBeanBuilder;

@Service
public class InfluxService {

	private final InfluxdbRepository influxRepository;

	public InfluxService(InfluxdbRepository influxRepository) {
		this.influxRepository = influxRepository;
	}

	// Method to insert events and items chronologically with batch processing
	public void insertChronologicallyWithBatch(String itemCsvPath, String eventCsvPath, String dbCollectionName)
			throws Exception {
		try (

				// Open file readers for item and event CSV files
				Reader itemReader = new FileReader(Paths.get(itemCsvPath).toFile());
				Reader eventReader = new FileReader(Paths.get(eventCsvPath).toFile())) {

			// Parse items from CSV into an iterator
			Iterator<Item> itemIt = new CsvToBeanBuilder<Item>(itemReader).withType(Item.class)
					.withIgnoreLeadingWhiteSpace(true).withSeparator(',') // Specifies the comma as the delimiter
					.withQuoteChar(CSVParser.NULL_CHARACTER) // Disables quote character handling
					.build().iterator();

			// Parse events from CSV into an iterator
			Iterator<Event> eventIt = new CsvToBeanBuilder<Event>(eventReader).withType(Event.class)
					.withIgnoreLeadingWhiteSpace(true).build().iterator();

			// Initialize the current item and event objects from the iterators
			Item currentItem = null;
			
			if (itemIt.hasNext()) {
				currentItem = itemIt.next();
				currentItem.postProcess();
			}

			Event currentEvent = null;
			if (eventIt.hasNext()) {
				currentEvent = eventIt.next();
				currentEvent.postProcess();
			}

			List<Point> batchPoints = new ArrayList<>();
			Point point;
			int batchSize = 1000; // Number of points per batch

			// Iterate through both item and event records
			while (currentItem != null || currentEvent != null) {
				Notification nextToInsert = null;

				// Compare timestamps of current item and event to insert them in order
				if (currentItem != null && currentEvent != null) {
					if (currentItem.getTimestamp() <= currentEvent.getTimestamp()) {
						nextToInsert = currentItem;
					} else {
						nextToInsert = currentEvent;
					}
				} else if (currentItem != null) {
					nextToInsert = currentItem;
				} else if (currentEvent != null) {
					nextToInsert = currentEvent;
				}
				nextToInsert.postProcess();
				// Skip invalid timestamps and print a warning
				if (nextToInsert == null || nextToInsert.getTimestamp() <= 0) {
					System.err.println("⚠️ Invalid timestamp, line ignored: " + nextToInsert);

				} else {
					// Convert the notification (item or event) into a point for InfluxDB
					point = NotificationMapper.toPoint(nextToInsert, dbCollectionName);
					// System.out.println("Point..." + point.toLineProtocol());

					// Add the point to the batch
					batchPoints.add(point);

					// create and add a request after each event
					if (nextToInsert instanceof Event event) {
						Notification request = new RecommendationRequest((Event) nextToInsert, 5);
						request.postProcess();
						point = NotificationMapper.toPoint(request, dbCollectionName);
						batchPoints.add(point);
					}

					// If batch reaches the specified size, write to InfluxDB
					if (batchPoints.size() >= batchSize) {
						influxRepository.insertBatch(batchPoints);
						batchPoints.clear(); // Clear the batch buffer for the next batch
					}
				}

				// Move to the next item or event depending on which was inserted
				if (nextToInsert == currentItem) {
					if (itemIt.hasNext()) {
						currentItem = itemIt.next();
						currentItem.postProcess();

					} else {
						currentItem = null;
					}
				} else if (nextToInsert == currentEvent) {
					if (eventIt.hasNext()) {
						currentEvent = eventIt.next();
						currentEvent.postProcess();
					} else {
						currentEvent = null;
					}
				}
			}

			// After the loop ends, write any remaining points in the batch
			if (!batchPoints.isEmpty()) {
				influxRepository.insertBatch(batchPoints);
			}

			System.out.println("🎉 Insertion completed successfully!");
		}
	}

	/*
	 * public void importCsvToInflux(String csvFilePath) { try (FileReader reader =
	 * new FileReader(csvFilePath)) {
	 * 
	 * // Build a CSV reader that maps CSV rows to Event objects (one row at a time)
	 * CsvToBean<Event> csvToBean = new
	 * CsvToBeanBuilder<Event>(reader).withType(Event.class)
	 * .withIgnoreLeadingWhiteSpace(true).build();
	 * 
	 * // Use an iterator to avoid loading the entire file into memory
	 * Iterator<Event> iterator = csvToBean.iterator();
	 * 
	 * List<Point> batchPoints = new ArrayList<>(); int batchSize = 1000; // Number
	 * of records per batch
	 * 
	 * // Stream through each CSV record while (iterator.hasNext()) { Event event =
	 * iterator.next();
	 * 
	 * // Create a data point for InfluxDB Point point =
	 * Point.measurement("event_data") // Measurement name in InfluxDB
	 * .addTag("notification", "event") // Tag: notification=event
	 * .addField("itemId", event.getItemId()).addField("userId", event.getUserId())
	 * .addField("geoUser", event.getGeoUser()).addField("deviceType",
	 * event.getDeviceType()) .time(Instant.ofEpochMilli(event.getTimestamp()),
	 * WritePrecision.MS); // Use the timestamp
	 * 
	 * // Add point to current batch batchPoints.add(point);
	 * 
	 * // Write to InfluxDB every 1000 points if (batchPoints.size() >= batchSize) {
	 * influxRepository.insertBatch(batchPoints); batchPoints.clear(); // Clear
	 * batch buffer } }
	 * 
	 * // Write any remaining points after loop ends if (!batchPoints.isEmpty()) {
	 * influxRepository.insertBatch(batchPoints); }
	 * 
	 * } catch (Exception e) { // Handle any errors such as file not found or parse
	 * error e.printStackTrace(); } }
	 */

}
