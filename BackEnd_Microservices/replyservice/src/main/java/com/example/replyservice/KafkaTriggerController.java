package com.example.replyservice;

import com.example.replyservice.kafka.KafkaItemProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

    // POST http://localhost:8082/reply/send-items?topic=your-custom-topic


@RestController


@RequestMapping("/reply")
public class KafkaTriggerController {
    @Autowired
    private KafkaItemProducer kafkaItemProducer;
    @PostMapping("/send-items")
    public String triggerKafkaProducer(
            @RequestParam(defaultValue = "items-topic") String topic,
            @RequestBody Map<String, String> requestBody) {
        // Lire les valeurs du corps de la requête
        String dataset = requestBody.get("dataset");
        String windowSize = requestBody.get("windowSize");
        String evaluationType = requestBody.get("evaluationType");
        String topK = requestBody.get("topK");

        ReplyServiceApplication.historiqueList.add(topic);
        kafkaItemProducer.sendSampleNotifications(topic,dataset, windowSize);

        return "✅ Items sent to Kafka topic: " + topic + ", dataset: " + dataset;
    }

}
