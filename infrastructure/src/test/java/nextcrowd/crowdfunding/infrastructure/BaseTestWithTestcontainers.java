package nextcrowd.crowdfunding.infrastructure;

import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BaseTestWithTestcontainers {

    private static PostgreSQLContainer<?> postgreSQLContainer;

    static {
        if ("true".equals(System.getenv("ci"))) {
            postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.4")
                    .withDatabaseName("nextcrowd_db")
                    .withUsername("nextcrowd_root")
                    .withPassword("nextcrowd_pwd")
                    .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*\\n", 1))
                    .withInitScript("init.sql");
            postgreSQLContainer.start();
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        if (postgreSQLContainer != null) {
            registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
            registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
            registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        }
    }


}