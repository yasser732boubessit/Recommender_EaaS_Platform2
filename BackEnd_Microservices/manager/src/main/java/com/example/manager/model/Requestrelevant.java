package com.example.manager.model;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "responses2") 
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Requestrelevant extends JsonNotification {
    
    @Id
    private String id;
    private Long idRequet ;
    private String algorithm;
    private String expérimentation;
    private String data;
    private String windowSize;
    private String startDate;
    private String endDate;
    private int historiqueId;
    private Map<String, Double> metrics;
    private List<Long> recs;
    private List<Long>  recommended;
    private List<Long>  relevant;
    private int userId;

    public Requestrelevant( Long idRequet,int historiqueId,String algorithm,String expérimentation, int userId, List<Long>  recommended , List<Long> recs, Map<String, Double> metrics) {
        this.notificationType = "relevant";
        this.idRequet = idRequet;
        this.historiqueId = historiqueId;
        this.algorithm = algorithm;
        this.expérimentation = expérimentation;  

        this.recs = recs;
        this.userId = userId;
        this.recommended = recommended;
        this.metrics = metrics;




    }


    public Requestrelevant(historique historique) {
        this.notificationType = "relevant";
        // Add historique fields
        this.algorithm = historique.getAlgorithm();
        this.expérimentation = historique.getexpérimentation();
        this.data = historique.getdata();
        this.windowSize = historique.getWindowSize();
        this.startDate = historique.getStartDate();
        this.endDate = historique.getEndDate();
        this.historiqueId = historique.getHistoriqueId();
    }

    // Add getters and setters for historique fields
    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getdata() {
        return data;
    }

    public void setdata(String data) {
        this.data = data;
    }

    public String getexpérimentation() {
        return expérimentation;
    }

    public void setexpérimentation(String expérimentation) {
        this.expérimentation = expérimentation;
    }

    public String getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(String windowSize) {
        this.windowSize = windowSize;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getHistoriqueId() {
        return historiqueId;
    }

    public void setHistoriqueId(int historiqueId) {
        this.historiqueId = historiqueId;
    }

	


   

    public List<Long>  getRecommended() {
        return recommended;
    }

    public void setRecommended(List<Long>  recommended) {
        this.recommended = recommended;
    }

    public List<Long>  getRelevant() {
        return relevant;
    }

    public void setRelevant(List<Long>  relevant) {
        this.relevant = relevant;
    }


	@Override
	public void postProcess() {
		this.setNotificationType("response");
	}









	






/* 

	public Requestrelevant(long requestID, long itemId, long userId, long timestamp, long geoUser, long deviceType   
	  ,  List<Integer> relevant ,List<Integer> recommended) {
        this.notificationType = "relevant";
        this.requestID  = requestID;
        this.itemId = itemId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.geoUser = geoUser;
        this.deviceType = deviceType;
		this.recommended = recommended;
       this.relevant = relevant;
    }*/
	
  


}
