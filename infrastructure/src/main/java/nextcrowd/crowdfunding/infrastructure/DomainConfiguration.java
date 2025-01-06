package nextcrowd.crowdfunding.infrastructure;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import nextcrowd.crowdfunding.infrastructure.domain.baker.BakerDomainConfiguration;
import nextcrowd.crowdfunding.infrastructure.domain.project.ProjectDomainConfiguration;

@Configuration
@Import({ProjectDomainConfiguration.class, BakerDomainConfiguration.class})
public class DomainConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }



}
