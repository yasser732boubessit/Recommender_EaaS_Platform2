package com.example.replyservice.model;

public class TimeRange {
    public String eventStart;
    public String eventEnd;
    public int eventCount;

    public String itemStart;
    public String itemEnd;
    public int itemCount;

    public int chunkSize;

    public TimeRange(String eventStart, String eventEnd, int eventCount,
                     String itemStart, String itemEnd, int itemCount,
                     int chunkSize) {
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.eventCount = eventCount;

        this.itemStart = itemStart;
        this.itemEnd = itemEnd;
        this.itemCount = itemCount;

        this.chunkSize = chunkSize;
    }
}


