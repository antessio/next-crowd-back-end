package nextcrowd.crowdfunding.infrastructure;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import nextcrowd.crowdfunding.infrastructure.project.cms.StrapiCmsAdapter;
import nextcrowd.crowdfunding.infrastructure.project.eventpublisher.DatabaseEventPublisher;
import nextcrowd.crowdfunding.infrastructure.project.persistence.adapter.CrowdfundingProjectSpringDataRepositoryAdapter;
import nextcrowd.crowdfunding.infrastructure.transaction.SpringTransactionAdapter;
import nextcrowd.crowdfunding.project.ProjectService;
import nextcrowd.crowdfunding.project.ProjectServicePort;
import nextcrowd.crowdfunding.project.service.ProjectValidationService;

@Configuration
public class DomainConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public ProjectServicePort projectService(
            Clock clock,
            CrowdfundingProjectSpringDataRepositoryAdapter crowdfundingProjectSpringDataRepositoryAdapter,
            DatabaseEventPublisher databaseEventPublisher,
            StrapiCmsAdapter strapiCmsAdapter,
            SpringTransactionAdapter springTransactionAdapter) {
        return new ProjectService(
                new ProjectValidationService(clock),
                crowdfundingProjectSpringDataRepositoryAdapter,
                databaseEventPublisher,
                strapiCmsAdapter,
                springTransactionAdapter);
    }

}
