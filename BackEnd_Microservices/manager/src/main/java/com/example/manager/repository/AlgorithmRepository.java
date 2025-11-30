package com.example.manager.repository;

import com.example.manager.model.Algorithm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlgorithmRepository extends JpaRepository<Algorithm, Long> {
    Optional<Algorithm> findByName(String name);
    void deleteByName(String name);
}
