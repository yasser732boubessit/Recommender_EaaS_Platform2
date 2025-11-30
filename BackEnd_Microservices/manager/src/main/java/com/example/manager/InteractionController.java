package com.example.manager;

import com.example.manager.kafka.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Map;
import java.util.stream.Collectors;

import com.example.manager.model.*;
import com.example.manager.repository.AlgorithmRepository;

import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/interaction")
@CrossOrigin(origins = "*")
@Service

public class InteractionController {

    private final KafkaItemConsumer kafkaItemConsumer;
    private final RestTemplate loadBalancedRestTemplate;
    private final RestTemplate simpleRestTemplate;
    private final MongoTemplate mongoTemplate;
    private final KafkaRequestService kafkaRequestService;
    private final MetricsAggregatorService MetricsAggregatorService;

 @Autowired
    public InteractionController(KafkaItemConsumer kafkaItemConsumer,
                               @Qualifier("loadBalancedRestTemplate") RestTemplate loadBalancedRestTemplate,
                               @Qualifier("simpleRestTemplate") RestTemplate simpleRestTemplate,                
                               KafkaRequestService kafkaRequestService,
                               MongoTemplate mongoTemplate,                    
                                MetricsAggregatorService MetricsAggregatorService
                               ) {
        this.kafkaItemConsumer = kafkaItemConsumer;
        this.loadBalancedRestTemplate = loadBalancedRestTemplate;
        this.simpleRestTemplate = simpleRestTemplate;
        this.mongoTemplate = mongoTemplate;
        this.kafkaRequestService = kafkaRequestService;
        this.MetricsAggregatorService = MetricsAggregatorService;

    }

         @Autowired
    private AlgorithmRepository algorithmRepository;






    @PostMapping("/send-items")
    public String triggerKafkaProducer(@RequestBody Map<String, Object> payload) {
        // Corrected type casting
         List<String> algorithms = (List<String>) payload.get("algorithm");
        String dataset = (String) payload.get("dataset");
        String evaluationType = (String) payload.get("evaluationType");
        String windowSize = (String) payload.get("windowSize");
        String topK = (String) payload.get("topK");
        String tester = "DefaultTester";
      ManagerApplication.AlgorithmList =  algorithmRepository.findAll();

        // Corrected for loop syntax
        if (algorithms != null) {
            for (String algorithm : algorithms) {
                addToHistorique(algorithm, tester, evaluationType, windowSize, dataset, topK);
            }
        }

        String groupId = "groupId-" + windowSize+"-" + dataset ;
        String topic = "topic-" + windowSize+"-" + dataset ;
      //  System.out.printf("📩 Received from UI → Algo: %s, Dataset: %s, EvalType: %s, Window: %s, TopK: %s%n",
       //         algorithms, dataset, evaluationType, windowSize, topK);
                return    sendToReplyService(topic, groupId, dataset, windowSize, evaluationType, topK);

    }










    private void addToHistorique(String algorithm, String tester, String evaluationType, String windowSize, String dataset,String topK) {
                    // Formatage de la date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String currentDate = dateFormat.format(new Date());
                    tester = "expérimentation " + currentDate;
  
                    String metricsString1 = "{PrecisionAtK=0.444, RecallAtK=0.66, F1AtK=1.0, NDCGAtK=0.33, CTRAtK=0.44}";
                        metricsString1 = metricsString1.replace("{", "").replace("}", "");
                        String[] keyValuePairs = metricsString1.split(", ");

                        Map<String, Double> metrics1 = new HashMap<>();
                        for (String pair : keyValuePairs) {
                            String[] entry = pair.split("=");
                            String key = entry[0];
                            Double value = Double.parseDouble(entry[1]);
                            metrics1.put(key, value);
                        }      
            List<historique>    historiqueList = mongoTemplate.findAll(historique.class, "historiques");                     
   switch (dataset) {
        case "6H":
             
           String  startDate=    "2001-08-26 06:57:05";
           String endtDate=      "01/02/2016 05:11:24";
        case "1J":
            startDate=    "2001-08-26 06:57:05";
            endtDate=      "01/02/2016 11:04:43";

        case "1W":
            startDate=    "2001-08-26 06:57:05";
            endtDate=      "07/02/2016 11:00:00";
             
        case "1M":
            startDate=    "2001-08-26 06:57:05";
            endtDate=      "28/02/2016 00:00:00";
              
        default:
            startDate=    "2001-08-26 06:57:05";
            endtDate=      "28/02/2016 00:00:00";
   
    historique newHistorique = new historique(metrics1,
                    algorithm,
                    tester,
                    evaluationType,
                    windowSize,
                    startDate, // startDate
                    endtDate, // endDate
                    historiqueList.size() + 1 // historiqueId
                    ,  Integer.parseInt(topK)// Top_k
                    );

                    // Ajout à la liste en mémoire
                    ManagerApplication.historiqueList.add(newHistorique);

                    // Enregistrement dans MongoDB
                    mongoTemplate.save(newHistorique, "historiques");
                    }
                }
        private String sendToReplyService(String topic, String groupId, String dataset, 
                                    String windowSize, String evaluationType, String topK) {

                kafkaItemConsumer.startDynamicKafkaListener(topic,  groupId);
                 //   return "✅ Listener démarré + Envoi à reply-service via Eureka terminé: " ;


               String replyServiceUrl = "http://reply-service/reply/send-items?topic=" + topic;

                Map<String, String> body = Map.of(
                        "dataset", dataset,
                        "windowSize", windowSize,
                        "evaluationType", evaluationType,
                        "topK", topK
                );


                // Démarrer le listener Kafka
         //       kafkaItemConsumer.startDynamicKafkaListener("topic-5-5p0aoppzom1-1w", groupId);

                // Préparer la requête HTTP
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

          // Envoyer la requête
              try {
                    String response = loadBalancedRestTemplate.postForObject(replyServiceUrl, requestEntity, String.class);
                    return "✅ Listener démarré + Envoi à reply-service via Eureka terminé: " + response;
                } catch (Exception e) {
                    e.printStackTrace();
                    return "❌ Erreur lors de l'envoi via Eureka: " + e.getMessage();
                }     /*  /* */  
            }








    public class MetricsAggregatorController {

        @Autowired
        private MetricsAggregatorService aggregatorService;

        @PostMapping("/aggregate")
        public String aggregateMetrics(
                @RequestParam String algorithm,
                @RequestParam String tester,
                @RequestParam int historiqueId) {
            aggregatorService.aggregateAndUpdateMetrics(algorithm, tester, historiqueId);
            return "Agrégation terminée.";
        }
    }



    @PostMapping("/send-resResp")
    public void receiveRecommendationResponse(@RequestBody RequestResponse resResp) {      
        System.out.println("📥 Reçu depuis reply-service:");
        System.out.println(resResp.toString());
        String algorithm = resResp.getAlgorithm();
        String tester = resResp.getTester();
        int historique = resResp.getHistorique();


        
        System.out.println("📥 esResp.getrequestID());"+resResp.getrequestID());

        long requestId =(long) resResp.getrequestID();     
        System.out.println("📥 requestId;"+requestId);
 

        List<Long> recs = MetricsAggregatorService.getRecsByRequestId(requestId);  


        System.out.println("recs /**"+recs);
        System.out.println("resResp.getRecs() /**"+resResp.getRecs());

        EvaluationRequest evaluationRequest = new EvaluationRequest();
        evaluationRequest.setK(5);
        evaluationRequest.setRecommended(recs);
        evaluationRequest.setRelevant(resResp.getRecs());
        resResp.setTimestamp(MetricsAggregatorService.getByRequestId(requestId).getTimestamp());
        System.out.println("resResp.getTimestamp() /**"+resResp.getTimestamp());  

         if (resResp.getRecs().size() > 0 && recs.size() > 0) {
            kafkaRequestService.sendEvaluationRequest(evaluationRequest, resResp, historique, algorithm, tester);
          }
    }


 




    
    @GetMapping("/send-Historique")
    public List<historique> getAllHistoriques() {
        List<historique>    historiqueList = mongoTemplate.findAll(historique.class, "historiques");                     

        System.out.println("📥 getAllHistoriques *********** "+historiqueList);

      //  historiqueList1=     historiqueList.get(5)      ; 
        return  historiqueList    ;       //MetricsAggregatorService.getAllHistoriques();

    }

    @GetMapping("/send-Historique/{id}")
    public historique getHistoriqueById(@PathVariable int id) {
        return MetricsAggregatorService.getHistoriqueById(id);
    }

@GetMapping("/historique/{id}/metrics")
public List<MetricsTimePointDTO> getMetricsByHistoriqueId(@PathVariable("id") int historiqueId) {
    Query query = new Query(Criteria.where("historiqueId").is(historiqueId));
    List<Requestrelevant> list = mongoTemplate.find(query, Requestrelevant.class, "responses2");

    return list.stream()
            .map(req -> new MetricsTimePointDTO(req.getTimestamp(), req.getMetrics()))
            .collect(Collectors.toList());
}





}
