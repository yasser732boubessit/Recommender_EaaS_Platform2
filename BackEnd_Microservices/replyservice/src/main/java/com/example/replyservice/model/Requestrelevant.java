package com.example.replyservice.model;

import java.util.List;
import java.util.Random;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Requestrelevant extends JsonNotification {
	
	
	private long requestID;
	
	private long itemId;

	private long userId;

	private long timestamp;

	private long geoUser;

	private long deviceType;
	
	private List<Long> recs;
	private int k;
    private List<Integer> recommended;
    private List<Integer> relevant;



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
       this.requestID = requestID;
    }
	

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public List<Integer> getRecommended() {
        return recommended;
    }

    public void setRecommended(List<Integer> recommended) {
        this.recommended = recommended;
    }

    public List<Integer> getRelevant() {
        return relevant;
    }

    public void setRelevant(List<Integer> relevant) {
        this.relevant = relevant;
    }


	@Override
	public void postProcess() {
		this.setNotificationType("response");
	}
}
