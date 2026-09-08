package com.raidiam.trustframework.mockinsurance.db;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.raidiam.trustframework.mockinsurance.exceptions.TrustframeworkException;
import io.micronaut.configuration.jdbc.tomcat.DatasourceConfiguration;
import io.micronaut.configuration.jdbc.tomcat.DatasourceFactory;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DbInitHandler implements RequestHandler<Map<String, Object>, String> {

    private static final Logger log = LoggerFactory.getLogger(DbInitHandler.class);

    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        log.info("Handling request");
        return getVariablesAndProcess(event);
    }

    String getVariablesAndProcess(Map<String, Object> event) {
        var dbUrl = System.getenv("DB_URL");
        var dbDriver = System.getenv("DB_DRIVER");
        var dbUsername = System.getenv("DB_USERNAME");
        var dbPassword = System.getenv("DB_PASSWORD");
        var dbSecretsManagerArn = System.getenv("DB_SECRETSMANAGER_ARN");
        boolean existingEnvironment = Boolean.parseBoolean(System.getenv("LEGACY_DB"));
        var executeWriteData = Optional.ofNullable(event)
                .map(e -> e.get("executeWriteData"))
                .map(run -> Boolean.parseBoolean(String.valueOf(run)))
                .orElse(true);

        return process(
                dbUrl,
                dbDriver,
                dbUsername,
                dbPassword,
                dbSecretsManagerArn,
                existingEnvironment,
                executeWriteData);
    }

    public String process(
            String dbUrl,
            String dbDriver,
            String dbUsername,
            String dbPassword,
            String dbSecretsManagerArn,
            boolean existingEnvironment,
            boolean executeWriteData) {
        log.info("Starting Flyway migrations");

        DatasourceConfiguration config = new DatasourceConfiguration("flyway");
        config.setUsername(dbUsername);
        config.setUrl(dbUrl);
        config.setDriverClassName(dbDriver);
        if (dbSecretsManagerArn != null && !dbSecretsManagerArn.isEmpty()) {
            log.info("Using secrets manager login");
            HashMap<String, String> properties = new HashMap<>();
            properties.put("wrapperPlugins", "awsSecretsManager");
            properties.put("secretsManagerSecretId", dbSecretsManagerArn);
            config.setDataSourceProperties(properties);
        } else if (dbPassword != null && !dbPassword.isEmpty()) {
            log.info("Using password login");
            config.setPassword(dbPassword);
        } else {
            throw new TrustframeworkException("Cannot continue - no KMS details or password provided");
        }

        try (var factory = new DatasourceFactory(null, null)) {
            var placeholders = Map.of("mockbank-url", System.getenv("MOCKSERVICE_URL") != null ? System.getenv("MOCKSERVICE_URL") : "http://matls-api.local");
            Flyway schemaFlyway = Flyway.configure()
                    .dataSource(factory.dataSource(config))
                    .placeholders(placeholders)
                    .locations("classpath:db/migration")
                    .ignoreMigrationPatterns("repeatable:*")
                    .load();
            log.info("Flyway configuration complete");

            log.info("Starting Flyway schema migrations");
            if (existingEnvironment) {
                log.info("Running baseline operation");
                schemaFlyway.baseline();
            }
            MigrateResult migrateResult = schemaFlyway.migrate();
            log.info("Schema migration completed, migrations executed {}", migrateResult.migrationsExecuted);

            if (executeWriteData) {
                log.info("Starting Flyway repeatable migrations");
                MigrateResult dataResult = Flyway.configure()
                        .dataSource(factory.dataSource(config))
                        .placeholders(placeholders)
                        .locations("classpath:db/dataloading")
                        .ignoreMigrationPatterns("versioned:*")
                        .load()
                        .migrate();
                log.info("Data loading completed, migrations executed {}", dataResult.migrationsExecuted);
            } else {
                log.info("Data loading disabled");
            }
        }
        log.info("Migration complete");
        return "200 OK";
    }
}
