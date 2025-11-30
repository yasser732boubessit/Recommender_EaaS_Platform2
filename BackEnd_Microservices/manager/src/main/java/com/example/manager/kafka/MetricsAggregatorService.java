package com.example.manager.kafka;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.example.manager.model.historique;
import com.example.manager.ManagerApplication;
import com.example.manager.model.RequestResponse;
import com.example.manager.model.Requestrelevant;
import java.util.Collections; 


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MetricsAggregatorService {

    @Autowired
    private  MongoTemplate mongoTemplate;





public  List<Long> getRecsByRequestId(long requestId) {
    Query query = new Query();
    query.addCriteria(Criteria.where("requestID").is(requestId)); 
    RequestResponse response = mongoTemplate.findOne(query, RequestResponse.class, "responses");

    if (response != null) {
        System.out.println("📥 Reçu depuis reply-service: algorithm 2 " + response.getrequestID());
        return response.getRecs();
    } else {
        System.out.println("📥 Reçu depuis reply-service: algorithm 2 " + requestId);

        return null;
    }
}
    

public  RequestResponse getByRequestId(long requestId) {
    Query query = new Query();
    query.addCriteria(Criteria.where("requestID").is(requestId)); 
    RequestResponse response = mongoTemplate.findOne(query, RequestResponse.class, "responses");

    if (response != null) {
        System.out.println("📥 Reçu depuis reply-service: algorithm 2 " + response.getrequestID());
        return response;
    } else {
        System.out.println("📥 Reçu depuis reply-service: algorithm 2 " + requestId);

        return null;
    }
}

 
    @Scheduled(fixedRate = 5000) 
    public void aggregateAndUpdateMetrics() {
            for (historique hist : ManagerApplication.historiqueList) {
                int historique = hist.getHistoriqueId();
                String algorithm = hist.getAlgorithm();
                String tester = hist.getexpérimentation();
        aggregateAndUpdateMetrics(algorithm, tester, historique);
       }
    }
    public void aggregateAndUpdateMetrics(String algorithm, String tester, int historiqueId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("algorithm").is(algorithm)
                .and("historiqueId").is(historiqueId));
        List<Requestrelevant> responses = mongoTemplate.find(query, Requestrelevant.class, "responses2");

        if (responses.isEmpty()) {
  //          System.out.println("Aucune donnée trouvée dans responses");
            return;
        }

        Map<String, Double> sumMetrics = new HashMap<>();
        int count = responses.size();

        for (Requestrelevant response : responses) {
            if (response.getMetrics() != null) {
                for (Map.Entry<String, Double> entry : response.getMetrics().entrySet()) {
                    sumMetrics.put(entry.getKey(), sumMetrics.getOrDefault(entry.getKey(), 0.0) + entry.getValue());
                }
            }
        }

        Map<String, Double> averageMetrics = new HashMap<>();
        for (Map.Entry<String, Double> entry : sumMetrics.entrySet()) {
            averageMetrics.put(entry.getKey(), entry.getValue() / count);
            //{PrecisionAtK=0.4, RecallAtK=0.6666666666666666, F1AtK=0.5, NDCGAtK=0.4367467095119259, CTRAtK=0.4}
        }

        Query historiqueQuery = new Query();
        historiqueQuery.addCriteria(Criteria.where("algorithm").is(algorithm)
                .and("historiqueId").is(historiqueId));
                historique hist = mongoTemplate.findOne(historiqueQuery, historique.class, "historiques");
                if (hist == null) {
              //      System.out.println("Aucun historique trouvé pour mettre à jour");
                    return;
                }
                hist.setMetrics(averageMetrics);
            //    System.out.println("  hist.setMetrics(averageMetrics);"+hist.getMetrics() );

                mongoTemplate.save(hist, "historiques");
            //    System.out.println("Mise à jour réussie dans la collection historiques.");

    //    System.out.println("Mise à jour réussie dans historiques.");
    }


    public List<historique> getAllHistoriques() {
        return mongoTemplate.findAll(historique.class, "historiques");
    }

    public historique getHistoriqueById(int historiqueId) {
        return mongoTemplate.findById(historiqueId, historique.class, "historiques");
    }


}
