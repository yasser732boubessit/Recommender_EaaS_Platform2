package com.example.manager.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ItemNotification extends JsonNotification {

	private static final List<String> dateFormats = new ArrayList<>();

	static {
		// Add your possible date formats here
		dateFormats.add("yyyy-MM-dd HH:mm:ss"); // e.g., "2003-04-09 06:38:24"
		dateFormats.add("EEE MMM dd HH:mm:ss z yyyy"); // e.g., "Wed Apr 09 06:38:24 GMT+01:00 2003"
		// Add more formats as needed
	}

	private String itemType;

	private String domain;

	private Date createdAt;

	private String createdAtString;

	private long itemId;

	private boolean recommendable;

	private String url;

	private String title;

	private String category;

	private String text;

	private String keywords;

	private String categoriesRaw;

	private List<String> categories;

	public ItemNotification(String itemType, String domain, String createdAtString, long itemId, boolean recommendable, String url,
			String title, String category, String text, String keywords, String categoriesRaw) {
		this.notificationType = "item"; // Set the notification type
		this.itemType = itemType;
		this.domain = domain;
		this.itemId = itemId;
		this.recommendable = recommendable;
		this.url = url;
		this.title = title;
		this.category = category;
		this.text = text;
		this.keywords = keywords;
		this.categoriesRaw = categoriesRaw;

		parseCategories(); // Parse the categories from the raw string

		// Handle custom date parsing logic
		parseCreatedAt(); // Call the parsing method for date string
	}

	public List<String> getCategories() {
		return categories;
	}

	public void parseCategories() {
		if (categoriesRaw != null && !categoriesRaw.isEmpty()) {
			this.categories = List.of(categoriesRaw.split("#"));
		}
	}

	// Getter for timestamp
	public long getTimestamp() {
		return timestamp;
	}

	// Getter for createdAt
	public Date getCreatedAt() {
		return createdAt;
	}

	public void parseCreatedAt() {
		for (String format : dateFormats) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				this.createdAt = sdf.parse(createdAtString);
				this.timestamp = createdAt.getTime(); // Convert to timestamp
				return; // Exit if parsing is successful
			} catch (ParseException e) {
				// Continue to next format if parsing fails
				System.err.println("Error parsing date with format: " + format + ". Trying next format...");
			}
		}
		// If all formats fail, set to null or handle error
		System.err.println("❌ Unable to parse the createdAt date: " + createdAtString);
		this.createdAt = null; // or use 0 timestamp or handle as needed
	}

	@Override
	public void postProcess() {
		this.setNotificationType("item");
		//parseCreatedAt();
		//parseCategories();
	}

}

// You can include setters/getters or use Lombok to reduce boilerplate
