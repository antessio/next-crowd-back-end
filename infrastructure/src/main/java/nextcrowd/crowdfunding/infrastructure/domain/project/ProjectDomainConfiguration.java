package nextcrowd.crowdfunding.infrastructure.domain.project;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import nextcrowd.crowdfunding.infrastructure.domain.project.cms.StrapiCmsAdapter;
import nextcrowd.crowdfunding.infrastructure.domain.project.eventpublisher.ProjectDatabaseEventPublisher;
import nextcrowd.crowdfunding.infrastructure.domain.project.persistence.adapter.CrowdfundingProjectSpringDataRepositoryAdapter;
import nextcrowd.crowdfunding.infrastructure.transaction.SpringTransactionAdapter;
import nextcrowd.crowdfunding.project.ProjectService;
import nextcrowd.crowdfunding.project.ProjectServicePort;
import nextcrowd.crowdfunding.project.service.ProjectValidationService;

@Configuration
public class ProjectDomainConfiguration {
    @Bean
    public ProjectServicePort projectService(
            Clock clock,
            CrowdfundingProjectSpringDataRepositoryAdapter crowdfundingProjectSpringDataRepositoryAdapter,
            ProjectDatabaseEventPublisher projectDatabaseEventPublisher,
            StrapiCmsAdapter strapiCmsAdapter,
            SpringTransactionAdapter springTransactionAdapter) {
        return new ProjectService(
                new ProjectValidationService(clock),
                crowdfundingProjectSpringDataRepositoryAdapter,
                projectDatabaseEventPublisher,
                strapiCmsAdapter,
                springTransactionAdapter);
    }

}
