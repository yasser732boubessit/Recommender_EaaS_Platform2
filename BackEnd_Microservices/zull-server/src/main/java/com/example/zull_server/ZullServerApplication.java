package com.example.zull_server;




import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication

@EnableEurekaServer

public class ZullServerApplication 
{
 public static void main(String[] args) 
{
    SpringApplication.run(ZullServerApplication.class, args);
 }

}