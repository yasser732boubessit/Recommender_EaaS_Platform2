package com.example.replyservice.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBConfig {

    @Bean
    public InfluxDBClient influxDBClient() {
        String url = "http://localhost:8086";
        String token = "kzULhe2s9K1s8nJh1BUvElURzaN6UKGd3Li5UH01DmfOLqcfwJ59I2HuCUPhS-Gam0Vl-K-8BdCUllsHrIJvGQ==";
        String org = "my-org";
        String bucket = "my-bucket";

        return InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
    }
}
