package com.example.replyservice.tools;

import com.example.replyservice.InfluxdbRepository;
import com.example.replyservice.model.JsonNotification;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class NotificationService {

    private final InfluxdbRepository repository;

    public NotificationService(InfluxdbRepository repository) {
        this.repository = repository;
    }

    public List<JsonNotification> readLastMinutes(int minutes) {
        Instant start = Instant.now().minus(Duration.ofMinutes(minutes));
        return repository.findNotificationsByTimeWindow(minutes, start);
    }

    public List<JsonNotification> readLastHours(int hours) {
        Instant start = Instant.now().minus(Duration.ofHours(hours));
        int minutes = hours * 60;
        return repository.findNotificationsByTimeWindow(minutes, start);
    }

    public List<JsonNotification> readLastYears(int years) {
        Instant start = Instant.now().minus(Duration.ofDays(365L * years));
        int minutes = (int) (Duration.between(start, Instant.now()).toMinutes());
        return repository.findNotificationsByTimeWindow(minutes, start);
    }

    public List<JsonNotification> readPageByHours(int hours, Instant startTime) {
        return repository.findNotificationsByTimeWindow(hours * 60, startTime);
    }

    public List<JsonNotification> readAll() {
        Instant start = Instant.EPOCH;
        long totalMinutes = Duration.between(start, Instant.now()).toMinutes();
        int window = totalMinutes > Integer.MAX_VALUE
            ? Integer.MAX_VALUE
            : (int) totalMinutes;
        return repository.findNotificationsByTimeWindow(window, start);
    }
}
