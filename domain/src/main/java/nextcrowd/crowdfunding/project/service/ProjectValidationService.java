package nextcrowd.crowdfunding.project.service;

import java.time.Clock;
import java.util.List;

import nextcrowd.crowdfunding.project.command.SubmitCrowdfundingProjectCommand;

public class ProjectValidationService {

    private final Clock clock;


    public ProjectValidationService(Clock clock) {
        this.clock = clock;
    }

    public List<ValidationFailure> validateProjectSubmission(SubmitCrowdfundingProjectCommand command) {
        return List.of();
    }

    public record ValidationFailure(String reason) {

    }

}
