package com.finova;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class FinovaApplication {

    private static final Logger log = LoggerFactory.getLogger(FinovaApplication.class);

    public static void main(String[] args) {
        loadDotEnv();
        SpringApplication.run(FinovaApplication.class, args);
    }

    private static void loadDotEnv() {
        List<File> candidates = List.of(
                new File(".env"),
                new File("../.env"),
                new File(System.getProperty("user.dir"), ".env")
        );

        File envFile = null;
        for (File candidate : candidates) {
            if (candidate.exists() && candidate.isFile()) {
                envFile = candidate;
                break;
            }
        }

        if (envFile == null) {
            log.info("No .env file found in candidate locations. Proceeding with standard system environment.");
            return;
        }

        log.info("Loading environment configuration from: {}", envFile.getAbsolutePath());
        int loadedCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(envFile, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int eqIdx = line.indexOf('=');
                if (eqIdx > 0) {
                    String key = line.substring(0, eqIdx).trim();
                    String value = line.substring(eqIdx + 1).trim();

                    // Strip matching outer quotes
                    if ((value.startsWith("\"") && value.endsWith("\"")) ||
                        (value.startsWith("'") && value.endsWith("'"))) {
                        if (value.length() >= 2) {
                            value = value.substring(1, value.length() - 1).trim();
                        }
                    }

                    if (!value.isEmpty() && System.getProperty(key) == null) {
                        System.setProperty(key, value);
                        loadedCount++;
                    }
                }
            }
            log.info("Loaded {} environment variables from .env successfully into application properties.", loadedCount);
        } catch (Exception e) {
            log.warn("Failed to read .env file ({}): {}", envFile.getAbsolutePath(), e.getMessage());
        }
    }
}
