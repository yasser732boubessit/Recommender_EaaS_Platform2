package com.example.manager.kafka;

import com.example.manager.ManagerApplication;
import com.example.manager.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaItemConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaRequestService kafkaRequestService;
    private final MongoTemplate mongoTemplate;
    @Autowired
    public KafkaItemConsumer(KafkaRequestService kafkaRequestService,MongoTemplate mongoTemplate) {
        this.kafkaRequestService = kafkaRequestService;
        this.mongoTemplate = mongoTemplate;
    }
    public void startDynamicKafkaListener(String topic, String groupId) {
        ContainerProperties containerProps = new ContainerProperties(topic);
        containerProps.setGroupId(groupId);
        containerProps.setMessageListener((MessageListener<String, String>) record -> {
            try {
                JsonNode notification = objectMapper.readTree(record.value());
                if (notification.has("notificationType")) {
                    String notificationType = notification.get("notificationType").asText();
                    switch (notificationType.toLowerCase()) {
                        case "item" ->{ kafkaRequestService.forwardToRecsClient(notification, "item");}

                        case "event" -> kafkaRequestService.handleInteractionNotification(notification);
                                          
                        case "response" -> {
                              //  kafkaRequestService.handleInteractionNotification(notification);   
                                RequestResponse response = objectMapper.treeToValue(notification,
                                                                      RequestResponse.class);
                                mongoTemplate.save(response, "responses");
                          
                          
                             //   ManagerApplication.RequestResponseList.add(response);
                                long idToFetch = response.getrequestID();
                                RequestResponse fetched = mongoTemplate.findOne(
                                    new Query(Criteria.where("requestID").is(idToFetch)),
                                    RequestResponse.class,
                                    "responses");                

                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ Error processing message: " + e.getMessage());
            }
        });

        ConcurrentMessageListenerContainer<String, String> container =
                new ConcurrentMessageListenerContainer<>(consumerFactory(groupId), containerProps);
        container.start();
    }

    private DefaultKafkaConsumerFactory<String, String> consumerFactory(String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
     
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

     
        return new DefaultKafkaConsumerFactory<>(props);
    }
}
