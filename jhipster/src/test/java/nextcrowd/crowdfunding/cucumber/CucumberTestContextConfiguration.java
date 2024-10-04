package nextcrowd.crowdfunding.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import nextcrowd.crowdfunding.IntegrationTest;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@IntegrationTest
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
