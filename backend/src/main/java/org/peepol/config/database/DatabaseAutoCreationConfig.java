package org.peepol.config.database;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

@Configuration
public class DatabaseAutoCreationConfig {

    @Bean
    public DatabaseCreator databaseCreator(Environment env) {
        return new DatabaseCreator(env);
    }

    public static class DatabaseCreator {
        private final Environment env;

        public DatabaseCreator(Environment env) {
            this.env = env;
        }

        @PostConstruct
        public void createDatabase() {
            String datasourceUrl = env.getProperty("spring.datasource.url");
            String username = env.getProperty("spring.datasource.username");
            String password = env.getProperty("spring.datasource.password");

            if (Objects.isNull(datasourceUrl) || Objects.isNull(username) || Objects.isNull(password)) return;

            int lastSlashIndex = datasourceUrl.lastIndexOf('/');
            if (lastSlashIndex != -1) {
                String dbName = datasourceUrl.substring(lastSlashIndex + 1);
                String baseUrl = datasourceUrl.substring(0, lastSlashIndex + 1) + "postgres";

                if (dbName.contains("?")) dbName = dbName.substring(0, dbName.indexOf("?"));

                try (Connection connection = DriverManager.getConnection(baseUrl, username, password);
                     Statement statement = connection.createStatement()) {

                    var resultSet = statement.executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'");
                    if (!resultSet.next()) statement.executeUpdate("CREATE DATABASE " + dbName);

                } catch (SQLException e) {
                    System.err.println("Failed to auto-create database: " + e.getMessage());
                }
            }
        }
    }

    @Bean
    public static BeanFactoryPostProcessor dataSourceDependencyPostProcessor() {
        return beanFactory -> {

            if (beanFactory.containsBeanDefinition("dataSource")) {
                BeanDefinition dataSourceDefinition = beanFactory.getBeanDefinition("dataSource");

                String[] dependsOn = dataSourceDefinition.getDependsOn();

                if (Objects.isNull(dependsOn)) dataSourceDefinition.setDependsOn("databaseCreator");
                else {
                    String[] newDependsOn = new String[dependsOn.length + 1];
                    System.arraycopy(dependsOn, 0, newDependsOn, 0, dependsOn.length);
                    newDependsOn[dependsOn.length] = "databaseCreator";
                    dataSourceDefinition.setDependsOn(newDependsOn);
                }
            }
        };
    }

}
