package com.hemza.master.recsClient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hemza.master.recsClient.algorithm.Algorithm;
import com.hemza.master.recsClient.algorithm.CoOccurrence;
import com.hemza.master.recsClient.algorithm.ItemItemCF;
import com.hemza.master.recsClient.algorithm.MostPopular;
import com.hemza.master.recsClient.algorithm.MostRecent;
import com.hemza.master.recsClient.algorithm.Random;
import com.hemza.master.recsClient.algorithm.RecentlyClicked;
import com.hemza.master.recsClient.algorithm.RecentlyPopular;

@Configuration
public class RecsEngineConfig {

	@Value("${recs.engine: MostRecent}")
	private String engineType;

	@Bean
    public Algorithm recsEngine() {
        switch (engineType) {
            case "CoOccurrence":
                return new CoOccurrence();
            case "ItemItemCF":
                return new ItemItemCF();
            case "MostPopular":
                return new MostPopular();
            case "MostRecent":
                return new MostRecent();
            case "Random":
                return new Random();
            case "RecentlyClicked":
                return new RecentlyClicked();
            case "RecentlyPopular":
            	return new RecentlyPopular();
            default:
                return new CoOccurrence();
        }
    }
}
