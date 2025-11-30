package com.example.manager;

import com.example.manager.model.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;

import com.example.manager.repository.AlgorithmRepository;

@SpringBootApplication
@EnableScheduling 

public class ManagerApplication {
    public static AlgorithmRepository algorithmRepository;

    // Ajout d'une liste statique de historique
    public static List<historique> historiqueList = new ArrayList<>();
    public static List<RequestResponse> RequestResponseList = new ArrayList<>();
    public static  List<Algorithm> AlgorithmList = new ArrayList<>();

    public static void main(String[] args) {

        SpringApplication.run(ManagerApplication.class, args);
    }

 
}




