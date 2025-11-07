package com.cookmate.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

@Service
public class FileStorageService {
    private final Path rootLocation;

    public FileStorageService() {
        // Use absolute path to ensure it works regardless of working directory
        this.rootLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootLocation);
            System.out.println("FileStorageService initialized. Upload directory: " + rootLocation.toString());
        } catch (IOException e) {
            System.err.println("Error creating uploads directory: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    public String storeFile(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file " + filename);
            }
            
            // Ensure uploads directory exists
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
                System.out.println("Created uploads directory: " + rootLocation.toString());
            }
            
            // Generate unique filename
            String uniqueFilename = System.currentTimeMillis() + "_" + filename;
            Path destinationFile = this.rootLocation.resolve(Paths.get(uniqueFilename))
                    .normalize().toAbsolutePath();
            
            // Ensure the destination is within the uploads directory
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new RuntimeException("Cannot store file outside current directory.");
            }
            
            System.out.println("Storing file: " + destinationFile.toString());
            
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            System.out.println("File stored successfully: " + uniqueFilename);
            // Return just the filename (without the /uploads/ prefix)
            return uniqueFilename;
        } catch (IOException e) {
            System.err.println("Error storing file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to store file " + filename + ": " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Unexpected error storing file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to store file " + filename + ": " + e.getMessage(), e);
        }
    }

    /**
     * Store binary data as a file and return the generated filename (no prefix).
     */
    public String storeFile(byte[] data, String originalFilename, String contentType) {
        String filename = StringUtils.cleanPath(originalFilename == null ? "file" : originalFilename);
        try {
            String uniqueFilename = System.currentTimeMillis() + "_" + filename;
            Path destinationFile = this.rootLocation.resolve(Paths.get(uniqueFilename))
                    .normalize().toAbsolutePath();

            // Ensure the destination is within the uploads directory
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new RuntimeException("Cannot store file outside current directory.");
            }

            Files.write(destinationFile, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            return uniqueFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + filename, e);
        }
    }

    /**
     * Delete a stored file by its filename (the generated name, not a path).
     */
    public void deleteFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) return;
        try {
            Path target = this.rootLocation.resolve(Paths.get(filename)).normalize().toAbsolutePath();
            if (Files.exists(target) && target.getParent().equals(this.rootLocation.toAbsolutePath())) {
                Files.delete(target);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file " + filename, e);
        }
    }
}