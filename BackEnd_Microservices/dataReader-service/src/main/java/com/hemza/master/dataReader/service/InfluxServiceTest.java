package com.hemza.master.dataReader.service;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hemza.master.dataReader.model.Event;
import com.hemza.master.dataReader.model.Item;
import com.hemza.master.dataReader.model.Notification;
import com.hemza.master.dataReader.util.NotificationMapper;
import com.hemza.master.dataReader.util.NotificationValidator;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;
import com.opencsv.CSVParser;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

public class InfluxServiceTest {

	private static String myToken = "kzULhe2s9K1s8nJh1BUvElURzaN6UKGd3Li5UH01DmfOLqcfwJ59I2HuCUPhS-Gam0Vl-K-8BdCUllsHrIJvGQ==";
	private static char[] token = myToken.toCharArray();
	private static String org = "my-org";
	private static String url = "http://localhost:8086";
	private static String bucket = "my-bucket";
	private static InfluxDBClient influxDBClient;
	private static String csvFilePathEvent = "src/main/resources/Events_plista418_1m_dedup.csv";
	private static String csvFilePathItem = "src/main/resources/Items_plista418_1m_fixed.csv";
	private static String dbCollectionName ="notificationsV2";
	
	
	public static void main(String[] args) {
		influxDBClient = InfluxDBClientFactory.create(url, token, org,bucket);

		// insertEventNotifications(csvFilePathEvent);
		try {
			insertChronologicallyWithBatch(csvFilePathItem, csvFilePathEvent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// Method to insert events and items chronologically with batch processing
	public static void insertChronologicallyWithBatch(String itemCsvPath, String eventCsvPath) throws Exception {
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
				System.out.println(currentItem.getTimestamp());
			}

			Event currentEvent = null;
			if (eventIt.hasNext()) {
				currentEvent = eventIt.next();
				currentEvent.postProcess();
				System.out.println(currentEvent.getTimestamp());
			}

			// Get the Write API for inserting data into InfluxDB
			WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

			List<Point> batchPoints = new ArrayList<>();
			int batchSize = 1000; // Number of points per batch
			int count = 0; // Counter for logging how many points are inserted

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
					Point point = NotificationMapper.toPoint(nextToInsert,dbCollectionName);
					//System.out.println("Point..." + point.toLineProtocol());

					// Add the point to the batch
					batchPoints.add(point);
					count++;

					// If batch reaches the specified size, write to InfluxDB
					if (batchPoints.size() >= batchSize) {
						writeApi.writePoints(batchPoints);
						System.out.println("✅ Written: " + count + " points");
						batchPoints.clear(); // Clear the batch buffer for the next batch
					}
				}

				// Move to the next item or event depending on which was inserted
				if (nextToInsert == currentItem) {
					if (itemIt.hasNext()) {
						currentItem = itemIt.next();
						currentItem.postProcess();
						//System.out.println(count);
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
				writeApi.writePoints(batchPoints);
				System.out.println("✅ Written (final): " + count + " points");
			}

			System.out.println("🎉 Insertion completed successfully!");
		}
	}

	public static void insertEventNotifications(String csvFilePath) {

		try (FileReader reader = new FileReader(csvFilePath)) {

			// Build a CSV reader that maps CSV rows to Event objects (one row at a time)
			CsvToBean<Event> csvToBean = new CsvToBeanBuilder<Event>(reader).withType(Event.class)
					.withIgnoreLeadingWhiteSpace(true).build();

			// Use an iterator to avoid loading the entire file into memory
			Iterator<Event> iterator = csvToBean.iterator();

			// InfluxDB blocking API for synchronous writes
			WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

			List<Point> batchPoints = new ArrayList<>();
			int batchSize = 1000; // Number of records per batch
			int count = 0; // Counter for logging progress

			// Stream through each CSV record
			while (iterator.hasNext()) {
				Event event = iterator.next();
				event.postProcess();
				if (NotificationValidator.isValid(event)) {
					// Create a data point for InfluxDB
					Point point = NotificationMapper.toPoint(event,dbCollectionName);
					// System.out.println("Point..." + point.toLineProtocol());

					// Add point to current batch
					batchPoints.add(point);
					count++;
				} else {
					System.err.println("❌ Notification invalide, non insérée : " + event);
				}

				// Write to InfluxDB every 1000 points
				if (batchPoints.size() >= batchSize) {
					writeApi.writePoints(batchPoints); // Synchronous write
					System.out.println("Written: " + count + " points");
					batchPoints.clear(); // Clear batch buffer
				}
			}

			// Write any remaining points after loop ends
			if (!batchPoints.isEmpty()) {
				// writeApi.writePoints(batchPoints);
				System.out.println("Written: " + count + " points (final)");
			}

		} catch (Exception e) {
			// Handle any errors such as file not found or parse error
			e.printStackTrace();
		}

	}

	public static void insertItemNotifications(String csvFilePath) {

		try (FileReader reader = new FileReader(csvFilePath)) {

			// Build a CSV reader that maps CSV rows to Event objects (one row at a time)
			CsvToBean<Item> csvToBean = new CsvToBeanBuilder<Item>(reader).withType(Item.class)
					.withIgnoreLeadingWhiteSpace(false).withSeparator(',').withQuoteChar(CSVParser.NULL_CHARACTER)
					// désactive
					// le
					// support
					// des
					// guillemets
					// doubles
					// comme
					// délimiteur
					// de
					// text
					.build();

			// Use an iterator to avoid loading the entire file into memory
			Iterator<Item> iterator = csvToBean.iterator();

			// InfluxDB blocking API for synchronous writes
			WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

			List<Point> batchPoints = new ArrayList<>();
			int batchSize = 1000; // Number of records per batch
			int count = 0; // Counter for logging progress

			// Stream through each CSV record
			while (iterator.hasNext()) {
				Item item = iterator.next();

				// Initialisation du timestamp et des autres champs
				item.postProcess();

				if (NotificationValidator.isValid(item)) {
					// Create a data point for InfluxDB
					Point point = NotificationMapper.toPoint(item,dbCollectionName);
					System.out.println("Point..." + point.toLineProtocol());

					// Add point to current batch
					batchPoints.add(point);
					count++;
					System.out.println(count);
				} else {
					System.err.println("❌ Notification invalide, non insérée : " + item);
				}

				// Write to InfluxDB every 1000 points
				if (batchPoints.size() >= batchSize) {
					writeApi.writePoints(batchPoints); // Synchronous write
					System.out.println("Written: " + count + " points");
					batchPoints.clear(); // Clear batch buffer
				}
			}

			// Write any remaining points after loop ends
			if (!batchPoints.isEmpty()) {
				// writeApi.writePoints(batchPoints);
				System.out.println("Written: " + count + " points (final)");
			}

		} catch (Exception e) {
			// Handle any errors such as file not found or parse error
			e.printStackTrace();
		}

	}


	

}
