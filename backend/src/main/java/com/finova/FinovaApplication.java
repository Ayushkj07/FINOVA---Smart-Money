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
import java.security.KeyStore;
import java.util.List;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class FinovaApplication {

    private static final Logger log = LoggerFactory.getLogger(FinovaApplication.class);

    public static void main(String[] args) {
        loadDotEnv();
        configureSslTrustStore();
        SpringApplication.run(FinovaApplication.class, args);
    }

    private static void configureSslTrustStore() {
        // If a custom truststore is already specified explicitly via CLI or environment, respect it
        if (System.getProperty("javax.net.ssl.trustStore") != null ||
            System.getProperty("javax.net.ssl.trustStoreType") != null) {
            log.info("Custom SSL truststore already defined via system properties: {}",
                    System.getProperty("javax.net.ssl.trustStore", System.getProperty("javax.net.ssl.trustStoreType")));
            return;
        }

        // Check if dedicated application truststore exists on filesystem
        List<File> candidates = List.of(
                new File("finova-truststore.jks"),
                new File("backend/finova-truststore.jks"),
                new File("../finova-truststore.jks"),
                new File("backend/src/main/resources/certs/finova-truststore.jks"),
                new File("src/main/resources/certs/finova-truststore.jks")
        );

        for (File f : candidates) {
            if (f.exists() && f.isFile()) {
                System.setProperty("javax.net.ssl.trustStore", f.getAbsolutePath());
                System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
                log.info("Configured dedicated Java SSL truststore: {}", f.getAbsolutePath());
                return;
            }
        }

        // On Windows environments with antivirus SSL scanning (such as Norton Web/Mail Shield),
        // fallback to Windows native Root CA store so all OS-trusted security software CAs are validated.
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) {
            try {
                KeyStore winStore = KeyStore.getInstance("WINDOWS-ROOT");
                winStore.load(null, null);
                if (winStore.size() > 0) {
                    System.setProperty("javax.net.ssl.trustStoreType", "WINDOWS-ROOT");
                    log.info("Configured Java SSL truststore to WINDOWS-ROOT ({} root certificates active).", winStore.size());
                    return;
                }
            } catch (Exception e) {
                log.warn("Could not initialize WINDOWS-ROOT truststore: {}", e.getMessage());
            }
        }
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
