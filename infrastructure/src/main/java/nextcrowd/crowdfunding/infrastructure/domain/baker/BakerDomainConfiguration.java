package nextcrowd.crowdfunding.infrastructure.domain.baker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import nextcrowd.crowdfunding.baker.BakerService;
import nextcrowd.crowdfunding.baker.BakerServicePort;
import nextcrowd.crowdfunding.infrastructure.domain.baker.eventpublisher.BakerDatabaseEventPublisher;
import nextcrowd.crowdfunding.infrastructure.domain.baker.persistence.adapter.BakerRepositoryAdapter;
import nextcrowd.crowdfunding.infrastructure.transaction.SpringTransactionAdapter;

@Configuration
public class BakerDomainConfiguration {

    @Bean
    public BakerServicePort bakerService(BakerRepositoryAdapter bakerRepository,
                                         SpringTransactionAdapter transactionManager,
                                         BakerDatabaseEventPublisher eventPublisher){
        return new BakerService(bakerRepository, transactionManager, eventPublisher);
    }

}
