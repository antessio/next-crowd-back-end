package nextcrowd.crowdfunding.infrastructure;

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BaseTestWithTestcontainers {

    @Container
    //@ServiceConnection
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.4")
            .withDatabaseName("nextcrowd_db")
            .withUsername("nextcrowd_root")
            .withPassword("nextcrowd_pwd")
            .withEnv("POSTGRES_HOST_AUTH_METHOD", "trust")
            .withExposedPorts(5432)
            .waitingFor(
                    Wait.forLogMessage(".*database system is ready to accept connections.*\\n", 1)
            )
            .withNetworkMode("bridge")
            .withInitScript("init.sql");

    /*@DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        *//*registry.add("spring.liquibase.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.liquibase.user", postgreSQLContainer::getUsername);
        registry.add("spring.liquibase.password", postgreSQLContainer::getPassword);*//*
    }*/

}