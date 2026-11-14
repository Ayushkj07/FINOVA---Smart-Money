package com.finova.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Normalizes cloud provider database URLs (e.g., Render, Railway, AWS, PlanetScale).
 * Ensures that whenever a MySQL JDBC URL is present, MySQL driver and MySQLDialect
 * are enforced and H2 profile settings are completely neutralized.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DatabaseEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(DatabaseEnvironmentPostProcessor.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String rawUrl = environment.getProperty("spring.datasource.url");
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = environment.getProperty("SPRING_DATASOURCE_URL");
        }
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = environment.getProperty("DATABASE_URL");
        }

        boolean onRender = Boolean.parseBoolean(environment.getProperty("RENDER")) ||
                environment.getProperty("RENDER_SERVICE_ID") != null;

        Map<String, Object> normalizedProps = new HashMap<>();

        if (rawUrl != null && !rawUrl.isBlank()) {
            String trimmedUrl = rawUrl.trim();

            try {
                if (trimmedUrl.startsWith("mysql://") || trimmedUrl.startsWith("mysqls://")) {
                    URI uri = new URI(trimmedUrl.replace("mysqls://", "mysql://"));
                    String host = uri.getHost();
                    int port = uri.getPort() == -1 ? 3306 : uri.getPort();
                    String path = uri.getPath() != null ? uri.getPath() : "/finova_db";
                    String query = uri.getQuery();

                    StringBuilder jdbcUrl = new StringBuilder("jdbc:mysql://")
                            .append(host)
                            .append(":")
                            .append(port)
                            .append(path);

                    if (query != null && !query.isBlank()) {
                        jdbcUrl.append("?").append(query);
                    } else {
                        jdbcUrl.append("?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true");
                    }

                    trimmedUrl = jdbcUrl.toString();
                    normalizedProps.put("spring.datasource.url", trimmedUrl);

                    if (uri.getUserInfo() != null && !uri.getUserInfo().isBlank()) {
                        String[] userParts = uri.getUserInfo().split(":", 2);
                        if (userParts.length > 0 && !userParts[0].isBlank() &&
                                environment.getProperty("spring.datasource.username") == null) {
                            normalizedProps.put("spring.datasource.username", userParts[0]);
                        }
                        if (userParts.length > 1 && !userParts[1].isBlank() &&
                                environment.getProperty("spring.datasource.password") == null) {
                            normalizedProps.put("spring.datasource.password", userParts[1]);
                        }
                    }
                    log.info("Normalized cloud MySQL URI to standard JDBC format.");
                }

                // If URL is MySQL (or normalized to MySQL), enforce MySQL driver, dialect, and activate prod profile
                if (trimmedUrl.startsWith("jdbc:mysql:")) {
                    normalizedProps.put("spring.datasource.url", trimmedUrl);
                    normalizedProps.put("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
                    normalizedProps.put("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
                    normalizedProps.put("spring.flyway.enabled", "true");
                    normalizedProps.put("spring.h2.console.enabled", "false");
                    normalizedProps.put("spring.profiles.active", "prod");

                    // Activate 'prod' profile directly on the environment
                    environment.addActiveProfile("prod");

                    if (onRender && (trimmedUrl.contains("localhost") || trimmedUrl.contains("127.0.0.1"))) {
                        log.error("=========================================================================================");
                        log.error("FATAL CONFIGURATION ERROR ON RENDER:");
                        log.error("The configured database URL is '{}'.", trimmedUrl);
                        log.error("Render Web Services cannot connect to 'localhost:3306'. Render does not run MySQL inside containers.");
                        log.error("Please configure an external managed MySQL database (e.g. Aiven, TiDB Cloud, Railway, AWS RDS)");
                        log.error("and update 'SPRING_DATASOURCE_URL' in the Render Dashboard Environment tab.");
                        log.error("=========================================================================================");
                    }
                }
            } catch (Exception ex) {
                log.warn("Could not parse database URI '{}': {}", trimmedUrl, ex.getMessage());
            }
        } else if (onRender) {
            log.warn("No SPRING_DATASOURCE_URL or DATABASE_URL found in Render environment. Please configure database credentials.");
            environment.addActiveProfile("prod");
        }

        if (!normalizedProps.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource("finovaNormalizedDataSource", normalizedProps));
        }
    }
}
