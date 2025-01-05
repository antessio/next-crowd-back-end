package nextcrowd.crowdfunding.baker.service;

import nextcrowd.crowdfunding.baker.command.SurveySubmissionCommand;
import nextcrowd.crowdfunding.baker.event.BakerCreatedEvent;
import nextcrowd.crowdfunding.baker.exception.BakerException;
import nextcrowd.crowdfunding.baker.model.Baker;
import nextcrowd.crowdfunding.baker.model.RiskLevel;
import nextcrowd.crowdfunding.baker.port.BakerRepository;
import nextcrowd.crowdfunding.baker.port.EventPublisher;

public class BakerCreationService {

    private final BakerRepository bakerRepository;
    private final EventPublisher eventPublisher;

    public BakerCreationService(BakerRepository bakerRepository, EventPublisher eventPublisher) {
        this.bakerRepository = bakerRepository;
        this.eventPublisher = eventPublisher;
    }


    public Baker createBaker(SurveySubmissionCommand command) {
        if (bakerRepository.getBaker(command.getBakerId()).isPresent()) {
            throw new BakerException(BakerException.Reason.SURVEY_ALREADY_SUBMITTED);
        }

        Baker baker = Baker.builder()
                           .bakerId(command.getBakerId())
                           .riskLevel(RiskLevel.MODERATE)
                           .build();
        bakerRepository.saveBaker(baker);
        eventPublisher.publish(BakerCreatedEvent.builder()
                                                .bakerId(baker.getBakerId())
                                                .riskLevel(baker.getRiskLevel())
                                                .build());
        return baker;
    }

}
