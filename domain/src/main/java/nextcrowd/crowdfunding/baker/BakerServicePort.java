package nextcrowd.crowdfunding.baker;

import java.util.Optional;

import nextcrowd.crowdfunding.baker.command.SurveySubmissionCommand;
import nextcrowd.crowdfunding.baker.model.BakerId;
import nextcrowd.crowdfunding.baker.model.Baker;
import nextcrowd.crowdfunding.baker.model.SurveyResult;

public interface BakerServicePort {

    SurveyResult submitSurvey(SurveySubmissionCommand command);

    Optional<Baker> getBaker(BakerId bakerId);
}
