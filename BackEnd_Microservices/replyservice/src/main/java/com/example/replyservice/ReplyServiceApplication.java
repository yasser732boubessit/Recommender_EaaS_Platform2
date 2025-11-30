package com.example.replyservice;

import com.example.replyservice.model.JsonNotification;
import com.example.replyservice.tools.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ReplyServiceApplication /*implements CommandLineRunner */ {



        public static List<String> historiqueList = new ArrayList<>();


    public static void main(String[] args) {
        SpringApplication.run(ReplyServiceApplication.class, args);
    }


    @Autowired
    private InfluxdbRepository influxdbRepository;
      /*   @Override
    public void run(String... args) {
   
        String startDate = "03/02/2016 00:19:53";
        String endDate = "03/02/2016 12:19:53";
        
        DataFetcher dataFetcher = new DataFetcher(influxdbRepository);
        List<List<JsonNotification>> chunks = dataFetcher.fetchInChunks(startDate, endDate, 6);
        
        for (int i = 0; i < chunks.size(); i++) {
            System.out.println("✅ Chunk " + (i + 1) + ":");
            for (JsonNotification notification : chunks.get(i)) {
                System.out.println(notification);
            }
        }
   
   
   
   
      String startDate = "03/02/2016 04:19:53";
        String endDate = "03/02/2016 12:19:53";
        System.out.println("✅ Notifications between " + startDate + " and " + endDate + ":");

        LocalDateTime localStartTime = LocalDateTime.of(2015, 5, 2, 14, 0, 0, 0);
        Instant startTime = localStartTime.toInstant(ZoneOffset.UTC);



        List<JsonNotification> notifications =
               // influxdbRepository.findNotificationsByTimeWindow(1000, startTime);
                influxdbRepository.findNotificationsBetween(startDate, endDate);

                System.out.println("✅ Notifications between " + startDate + " and " + endDate + ":");

        for (JsonNotification notification : notifications) {
            System.out.println(notification);


        } 

    } */
    
    
   


    
  
}

