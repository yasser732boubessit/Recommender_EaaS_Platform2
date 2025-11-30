package com.hemza.master.dataReader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DataReaderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataReaderServiceApplication.class, args);
	}

}
