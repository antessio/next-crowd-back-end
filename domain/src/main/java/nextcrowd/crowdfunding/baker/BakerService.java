package nextcrowd.crowdfunding.baker;

import java.util.Optional;

import nextcrowd.crowdfunding.baker.command.SurveySubmissionCommand;
import nextcrowd.crowdfunding.baker.model.BakerId;
import nextcrowd.crowdfunding.baker.model.Baker;
import nextcrowd.crowdfunding.baker.model.SurveyResult;
import nextcrowd.crowdfunding.baker.port.BakerRepository;
import nextcrowd.crowdfunding.baker.port.EventPublisher;
import nextcrowd.crowdfunding.baker.service.BakerCreationService;
import nextcrowd.crowdfunding.common.TransactionalManager;

public class BakerService implements BakerServicePort {

    private final BakerRepository bakerRepository;
    private final BakerCreationService bakerCreationService;
    private final TransactionalManager transactionalManager;

    public BakerService(BakerRepository bakerRepository, TransactionalManager transactionalManager, EventPublisher eventPublisher) {
        this.bakerRepository = bakerRepository;
        this.bakerCreationService = new BakerCreationService(bakerRepository, eventPublisher);
        this.transactionalManager = transactionalManager;
    }

    @Override
    public SurveyResult submitSurvey(SurveySubmissionCommand command) {
        //TODO: real implementation goes to the CMS and loads the questions with their score.
        // Then it calculates the risk level based on the answers and returns the result.
        Baker baker = transactionalManager.executeInTransaction(() -> bakerCreationService.createBaker(command));
        return new SurveyResult(baker.getRiskLevel());
    }

    @Override
    public Optional<Baker> getBaker(BakerId bakerId) {
        return bakerRepository.getBaker(bakerId);
    }

}
