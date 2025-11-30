package com.example.replyservice.model;

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
        dateFormats.add("yyyy-MM-dd HH:mm:ss"); // Example: "2003-04-09 06:38:24"
        dateFormats.add("EEE MMM dd HH:mm:ss z yyyy"); // Example: "Wed Apr 09 06:38:24 GMT+01:00 2003"
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
        this.notificationType = "item";
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

        parseCategories();

        if (createdAtString != null && !createdAtString.isEmpty()) {
            parseCreatedAt(createdAtString); // Parse valid date
        } else {
            System.err.println("❌ Empty or null createdAtString provided");
        }
    }

    public void parseCategories() {
        if (categoriesRaw != null && !categoriesRaw.isEmpty()) {
            this.categories = List.of(categoriesRaw.split("#"));
        }
    }

    public void parseCreatedAt(String createdAtString) {
        for (String format : dateFormats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                this.createdAt = sdf.parse(createdAtString);
                this.timestamp = createdAt.getTime();
                return; // Successful parsing, exit loop
            } catch (ParseException e) {
                System.err.println("Error parsing date with format: " + format + ". Trying next format...");
            }
        }
        System.err.println("❌ Unable to parse the createdAt date: " + createdAtString);
        this.createdAt = null; // Handle the case where parsing fails
    }

    @Override
    public void postProcess() {
        this.setNotificationType("item");
    }
}
