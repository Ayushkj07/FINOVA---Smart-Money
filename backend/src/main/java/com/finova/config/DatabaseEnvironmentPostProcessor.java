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
 * Translates URI formats like 'mysql://user:pass@host:port/db' or 'postgresql://...' into
 * valid JDBC URLs ('jdbc:mysql://...' / 'jdbc:postgresql://...') and extracts credentials.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DatabaseEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(DatabaseEnvironmentPostProcessor.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String rawUrl = environment.getProperty("SPRING_DATASOURCE_URL");
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = environment.getProperty("DATABASE_URL");
        }

        if (rawUrl == null || rawUrl.isBlank()) {
            return;
        }

        String trimmedUrl = rawUrl.trim();
        Map<String, Object> normalizedProps = new HashMap<>();

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

                normalizedProps.put("spring.datasource.url", jdbcUrl.toString());

                if (uri.getUserInfo() != null && !uri.getUserInfo().isBlank()) {
                    String[] userParts = uri.getUserInfo().split(":", 2);
                    if (userParts.length > 0 && !userParts[0].isBlank()) {
                        normalizedProps.put("spring.datasource.username", userParts[0]);
                    }
                    if (userParts.length > 1 && !userParts[1].isBlank()) {
                        normalizedProps.put("spring.datasource.password", userParts[1]);
                    }
                }
                log.info("Normalized MySQL database connection string from cloud environment URI.");
            } else if (trimmedUrl.startsWith("postgres://") || trimmedUrl.startsWith("postgresql://")) {
                URI uri = new URI(trimmedUrl.replace("postgres://", "postgresql://"));
                String host = uri.getHost();
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String path = uri.getPath() != null ? uri.getPath() : "/finova_db";
                String query = uri.getQuery();

                StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://")
                        .append(host)
                        .append(":")
                        .append(port)
                        .append(path);

                if (query != null && !query.isBlank()) {
                    jdbcUrl.append("?").append(query);
                }

                normalizedProps.put("spring.datasource.url", jdbcUrl.toString());

                if (uri.getUserInfo() != null && !uri.getUserInfo().isBlank()) {
                    String[] userParts = uri.getUserInfo().split(":", 2);
                    if (userParts.length > 0 && !userParts[0].isBlank()) {
                        normalizedProps.put("spring.datasource.username", userParts[0]);
                    }
                    if (userParts.length > 1 && !userParts[1].isBlank()) {
                        normalizedProps.put("spring.datasource.password", userParts[1]);
                    }
                }
                log.info("Normalized PostgreSQL database connection string from cloud environment URI.");
            } else if (trimmedUrl.startsWith("jdbc:")) {
                // Already standard JDBC format
                normalizedProps.put("spring.datasource.url", trimmedUrl);
            }

            if (!normalizedProps.isEmpty()) {
                environment.getPropertySources().addFirst(new MapPropertySource("finovaNormalizedDataSource", normalizedProps));
            }
        } catch (Exception ex) {
            log.warn("Could not normalize database URI '{}': {}", trimmedUrl, ex.getMessage());
        }
    }
}
