package nextcrowd.crowdfunding.infrastructure;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@EnabledIfSystemProperty(named = "enableTestcontainers", matches = "true")
public abstract class BaseTestWithTestcontainers {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.4")
            .withDatabaseName("nextcrowd_db")
            .withUsername("nextcrowd_root")
            .withPassword("nextcrowd_pwd")
            .withEnv("POSTGRES_HOST_AUTH_METHOD", "trust")
            .withExposedPorts(5432);

    @BeforeAll
    static void setUp() {
        // Additional setup if needed
    }
}