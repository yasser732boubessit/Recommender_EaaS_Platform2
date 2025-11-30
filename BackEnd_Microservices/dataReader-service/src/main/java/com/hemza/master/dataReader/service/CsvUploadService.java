package com.hemza.master.dataReader.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CsvUploadService {

	// Dossier où les fichiers sont stockés
	@Value("${file.upload-dir}")
	private String uploadDir;

	// Méthode pour enregistrer un fichier sur le serveur et retourner son chemin
	public String saveFile(MultipartFile file) throws IOException {

		// Créer le dossier si nécessaire
		File dir = new File(uploadDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		// Générer un nom unique pour le fichier pour éviter les collisions
		String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

		// Construire le chemin de destination complet
		Path path = Path.of(uploadDir, fileName);

		// Sauvegarder le fichier dans le dossier spécifié
		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

		// Retourner le chemin du fichier
		return path.toString();
	}
}
