package nextcrowd.infrastructure.jhipster.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import nextcrowd.infrastructure.jhipster.IntegrationTest;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@IntegrationTest
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
