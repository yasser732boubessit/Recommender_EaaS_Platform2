package com.example.manager.kafka;

import com.example.manager.ManagerApplication;
import com.example.manager.data.*;
import com.example.manager.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.example.manager.repository.AlgorithmRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KafkaRequestService {



    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    @Qualifier("simpleRestTemplate")
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void forwardToRecsClient(JsonNode notification, String type) {
        try {
            String recsClientUrl = "http://localhost:2000/notifyV2";
            for (historique hist : ManagerApplication.historiqueList) {
                int historique = hist.getHistoriqueId();
                String algorithm = hist.getAlgorithm();
                String tester = hist.getexpérimentation();
                for (Algorithm ALGO :  ManagerApplication.AlgorithmList) {
                    if (ALGO.getName().equals(algorithm)) {
                        recsClientUrl = ALGO.getUrlEvents();
                        break;
                    }
                }
                System.out.println("📨 Sending request to recsClientUrl: " + recsClientUrl);

                Map<String, Object> payload = objectMapper.convertValue(notification, Map.class);
                payload.put("notificationType", type.toLowerCase());
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

                restTemplate.postForObject(recsClientUrl, entity, String.class);
            }
        } catch (Exception e) {
            System.err.println("❌ Error sending to recs-client: " + e.getMessage());
        }
    }

    public void handleInteractionNotification(JsonNode notification) {
        System.out.println("📨  notification.toString(): " + notification.toString());
        forwardToRecsClient(notification, "event");
        if (notification.has("itemId")) {
            try {
                Map<String, Object> map = objectMapper.convertValue(notification, new TypeReference<>() {
                });
                Event event = NotificationParser.toEvent(map);
                System.out.println("📨  getEventId: " + event.getEventId());

                Request request = new Request(event);
                request.setLimit(3);

                request.setLimit(3);
                sendRecommendationAndEvaluate(request);
            } catch (IllegalArgumentException e) {
                System.err.println("❌ Error while creating request: " + e.getMessage());
            }
        }
    }

    public void sendRecommendationAndEvaluate(Request request) {
        try {
            String recsClientUrl = "http://localhost:2000/recommend";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            for (historique hist : ManagerApplication.historiqueList) {
                int historique = hist.getHistoriqueId();
                String algorithm = hist.getAlgorithm();
                String tester = hist.getexpérimentation();
                request.setLimit(hist.getTop_k());

                for (Algorithm ALGO :  ManagerApplication.AlgorithmList) {
                    if (ALGO.getName().equals(algorithm)) {
                        recsClientUrl = ALGO.getUrlReco();
                        System.out.println("📨 Sending request to recsClientUrl: " + recsClientUrl);
                        break;
                    }
                  System.out.println("📨 Sending request to recsClientUrl: " + recsClientUrl);

                    if (ALGO.getName().equals(algorithm)) {
                        recsClientUrl = ALGO.getUrlReco();
                        System.out.println("📨 Sending request ALGO to recsClientUrl: " + recsClientUrl);
                        break;
                    }
                }

                // Ajout des variables supplémentaires dans une Map
                Map<String, Object> requestWithMetadata = new HashMap<>();
                requestWithMetadata.put("request", request);
                requestWithMetadata.put("historique", historique);
                requestWithMetadata.put("algorithm", algorithm);
                requestWithMetadata.put("tester", tester);

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestWithMetadata, headers);
                RequestResponse response = restTemplate.postForObject(recsClientUrl, entity, RequestResponse.class);
            }
        } catch (Exception e) {
            System.err.println("❌ Error sending recommendation request: " + e.getMessage());
        }
    }

    /*
     * // Prepare evaluation request
     * EvaluationRequest evaluationRequest = new EvaluationRequest();
     * evaluationRequest.setK(5);
     * evaluationRequest.setRecommended(Arrays.asList(1, 2, 3, 4, 5));
     * evaluationRequest.setRelevant(Arrays.asList(3, 4, 6));
     * 
     * // Send evaluation request
     * // sendEvaluationRequest(evaluationRequest, response, historique, algorithm,
     * tester);
     */

    public void sendEvaluationRequest(EvaluationRequest evaluationRequest, RequestResponse request, int historique,
            String algorithm, String tester) {
        try {
            String evaluationServiceUrl = "http://localhost:8088/evaluation";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<EvaluationRequest> entity = new HttpEntity<>(evaluationRequest, headers);

            EvaluationResult result = restTemplate.postForObject(evaluationServiceUrl, entity, EvaluationResult.class);
            System.out.println("📨 Evaluation Result: " + result.getMetrics());

            Requestrelevant requestrelevant = new Requestrelevant(request.getrequestID(),
                    historique,
                    algorithm,
                    tester,
                    evaluationRequest.getK(),
                    evaluationRequest.getRecommended(),
                    evaluationRequest.getRelevant(),
                    result.getMetrics()

            );
            requestrelevant.setTimestamp(request.getTimestamp());
            mongoTemplate.save(requestrelevant, "responses2");

        } catch (Exception e) {
            System.err.println("❌ Error sending evaluation request: " + e.getMessage());
        }
    }

}

/*
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * public void sendRecommendationAndEvaluate1(Request request) {
 * try {
 * 
 * 
 * String recsClientUrl = "http://localhost:2000/recommend";
 * HttpHeaders headers = new HttpHeaders();
 * headers.setContentType(MediaType.APPLICATION_JSON);
 * 
 * // Ajout des variables supplémentaires dans une Map
 * Map<String, Object> requestWithMetadata = new HashMap<>();
 * requestWithMetadata.put("request", request);
 * requestWithMetadata.put("historique", 0);
 * requestWithMetadata.put("algorithm", "algorithm");
 * requestWithMetadata.put("tester", "tester");
 * 
 * HttpEntity<Map<String, Object>> entity = new
 * HttpEntity<>(requestWithMetadata, headers);
 * 
 * // Envoi de la requête
 * RequestResponse response = restTemplate.postForObject(recsClientUrl, entity,
 * RequestResponse.class); EvaluationRequest evaluationRequest = new
 * EvaluationRequest();
 * evaluationRequest.setK(5);
 * evaluationRequest.setRecommended(Arrays.asList(1, 2, 3, 4, 5));
 * evaluationRequest.setRelevant(Arrays.asList(3, 4, 6));
 * 
 * int historique = 0;
 * String algorithm = "algorithm";
 * String tester = "tester";
 * 
 * // sendEvaluationRequest(evaluationRequest, response,historique, algorithm,
 * tester);
 * 
 * 
 * } catch (Exception e) {
 * System.err.println("❌ Error sending recommendation request: " +
 * e.getMessage());
 * }
 * }
 * 
 */
