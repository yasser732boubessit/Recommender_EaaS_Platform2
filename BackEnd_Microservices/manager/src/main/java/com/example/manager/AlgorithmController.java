package com.example.manager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.manager.model.Algorithm;
import com.example.manager.repository.AlgorithmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/algorithm")
@CrossOrigin(origins = "*")
@Service
public class AlgorithmController {

    @Autowired
    private AlgorithmRepository algorithmRepository;

    @PostMapping("/add-algorithm")
    public ResponseEntity<String> addAlgorithm(@RequestBody Map<String, String> algoData) {
        String name = algoData.get("name");
        String urlItems = algoData.get("urlItems");
        String urlEvents = algoData.get("urlEvents");
        String urlReco = algoData.get("urlReco");

        Algorithm algo = new Algorithm(name, urlItems, urlEvents, urlReco);
        algorithmRepository.save(algo);

        return ResponseEntity.ok("✅ Algorithme ajouté avec succès");
    }

@DeleteMapping("/delete-algorithm/{name}")
public ResponseEntity<String> deleteAlgorithm(@PathVariable String name) {
    Optional<Algorithm> algoOpt = algorithmRepository.findByName(name);
    if (algoOpt.isPresent()) {
        algorithmRepository.delete(algoOpt.get());
        return ResponseEntity.ok("🗑️ Algorithme supprimé");
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Algorithme non trouvé");
    }
}

    @GetMapping("/all-algorithms")
    public List<Algorithm> getAllAlgorithms() {
        return algorithmRepository.findAll();
    }

    @PutMapping("/update-algorithm/{originalName}")
    public ResponseEntity<String> updateAlgorithm(@PathVariable String originalName, @RequestBody Map<String, String> algoData) {
        Optional<Algorithm> optionalAlgo = algorithmRepository.findByName(originalName);
        if (optionalAlgo.isPresent()) {
            Algorithm algo = optionalAlgo.get();
            algo.setName(algoData.get("name"));
            algo.setUrlItems(algoData.get("urlItems"));
            algo.setUrlEvents(algoData.get("urlEvents"));
            algo.setUrlReco(algoData.get("urlReco"));
            algorithmRepository.save(algo);
            return ResponseEntity.ok("✏️ Algorithme mis à jour avec succès");
        } else {
            return ResponseEntity.status(404).body("❌ Algorithme non trouvé");
        }
    }
}
