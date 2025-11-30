package com.example.replyservice.tools;

import com.example.replyservice.model.JsonNotification;
import com.example.replyservice.InfluxdbRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DataFetcher {

    private final InfluxdbRepository influxdbRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public DataFetcher(InfluxdbRepository influxdbRepository) {
        this.influxdbRepository = influxdbRepository;
    }

    public List<List<JsonNotification>> fetchInChunks(String startDate, String endDate, int chunkHours) {
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);

        List<List<JsonNotification>> allChunks = new ArrayList<>();

        LocalDateTime currentStart = start;
        while (currentStart.isBefore(end)) {
            LocalDateTime currentEnd = currentStart.plusHours(chunkHours);
            if (currentEnd.isAfter(end)) {
                currentEnd = end;
            }

            String partStartDate = currentStart.format(formatter);
            String partEndDate = currentEnd.format(formatter);

            System.out.println("✅ Reading notifications between " + partStartDate + " and " + partEndDate);

            List<JsonNotification> chunk = influxdbRepository.findNotificationsBetween(partStartDate, partEndDate);
            allChunks.add(chunk);

            currentStart = currentEnd;
        }

        return allChunks;
    }
}

