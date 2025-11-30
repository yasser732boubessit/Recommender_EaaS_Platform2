package com.hemza.master.dataReader.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hemza.master.dataReader.service.CsvUploadService;
import com.hemza.master.dataReader.service.InfluxService;

@RestController
public class DataController {

	private final InfluxService csvToInfluxService;
	private final CsvUploadService csvUploadService;

	// Constructor-based dependency injection (recommended way)
	public DataController(InfluxService csvToInfluxService, CsvUploadService csvUploadService) {
		this.csvToInfluxService = csvToInfluxService;
		this.csvUploadService = csvUploadService;
	}

	// Simple GET endpoint to return a hello message
	@GetMapping("/")
	public String home() {
		return "Hello from Data reader Service 👋";
	}

	/**
	 * Endpoint to trigger CSV import into InfluxDB. Example: POST
	 * /api/import?path=/path/to/file.csv
	 *
	 * @param csvFilePath Full path to the CSV file on disk
	 * @return Status message
	 */
	@GetMapping("/notifications")
	public String importCsv(@RequestParam("colName") String colName) {
		try {
			String csvFilePathEvent = "src/main/resources/Events_plista418_1m_dedup_6H.csv";
			String csvFilePathItem = "src/main/resources/Items_plista418_1m_fixed_6H.csv";
			String dbCollectionName = "notificationsV4_6H";

			csvToInfluxService.insertChronologicallyWithBatch(csvFilePathItem, csvFilePathEvent, dbCollectionName);
			
			return "✅ CSV import completed successfully.";
		} catch (Exception e) {
			e.printStackTrace();
			return "❌ Import failed: " + e.getMessage();
		}
	}

	@PostMapping("/dataSets")
	public ResponseEntity<String> uploadDataSetFiles(@RequestParam("eventFile") MultipartFile eventFile,
			@RequestParam("itemFile") MultipartFile itemFile, @RequestParam("collectionName") String collectionName) {

		try {

			// Sauvegarder les fichiers sur le serveur
			String eventFilePath = csvUploadService.saveFile(eventFile);
			String itemFilePath = csvUploadService.saveFile(itemFile);

			// Vous pouvez ensuite traiter les fichiers ou retourner leurs chemins
			return ResponseEntity.ok("Fichiers téléchargés avec succès. Chemin des événementn : " + eventFilePath
					+ ", Chemin des items : " + itemFilePath);
		} catch (IOException e) {
			return ResponseEntity.status(500).body("Erreur lors du traitement des fichiers : " + e.getMessage());
		}

	}

}
