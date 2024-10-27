package nextcrowd.infrastructure.jhipster;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import nextcrowd.infrastructure.jhipster.config.AsyncSyncConfiguration;
import nextcrowd.infrastructure.jhipster.config.EmbeddedSQL;
import nextcrowd.infrastructure.jhipster.config.JacksonConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { NextCrowdBackEndApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class })
@EmbeddedSQL
public @interface IntegrationTest {
}
