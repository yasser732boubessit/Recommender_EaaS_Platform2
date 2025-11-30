package com.example.manager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "algorithms")
public class Algorithm {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "url_items")
    private String urlItems;

    @Column(name = "url_events")
    private String urlEvents;

    @Column(name = "url_reco")
    private String urlReco;

    public Algorithm() {
    }

    public Algorithm(String name, String urlItems, String urlEvents, String urlReco) {
        this.name = name;
        this.urlItems = urlItems;
        this.urlEvents = urlEvents;
        this.urlReco = urlReco;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlItems() {
        return urlItems;
    }

    public void setUrlItems(String urlItems) {
        this.urlItems = urlItems;
    }

    public String getUrlEvents() {
        return urlEvents;
    }

    public void setUrlEvents(String urlEvents) {
        this.urlEvents = urlEvents;
    }

    public String getUrlReco() {
        return urlReco;
    }

    public void setUrlReco(String urlReco) {
        this.urlReco = urlReco;
    }

    @Override
    public String toString() {
        return "Algorithm{" +
                "name='" + name + '\'' +
                ", urlItems='" + urlItems + '\'' +
                ", urlEvents='" + urlEvents + '\'' +
                ", urlReco='" + urlReco + '\'' +
                '}';
    }
}
