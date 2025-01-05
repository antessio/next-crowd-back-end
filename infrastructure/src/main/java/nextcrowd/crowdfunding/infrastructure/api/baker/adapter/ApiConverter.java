package nextcrowd.crowdfunding.infrastructure.api.baker.adapter;

import java.util.Optional;
import java.util.UUID;

import nextcrowd.crowdfunding.baker.api.model.InvestmentProfile;
import nextcrowd.crowdfunding.baker.api.model.InvestmentSurvey;
import nextcrowd.crowdfunding.baker.api.model.SurveyCompletionResponse;
import nextcrowd.crowdfunding.baker.command.SurveySubmissionCommand;
import nextcrowd.crowdfunding.baker.model.Baker;
import nextcrowd.crowdfunding.baker.model.BakerId;
import nextcrowd.crowdfunding.baker.model.SurveyResult;

public class ApiConverter {

    private ApiConverter() {

    }

    public static SurveySubmissionCommand convertSurveyRequest(InvestmentSurvey investmentSurvey, UUID userId) {
        return SurveySubmissionCommand.builder()
                                      .bakerId(new BakerId(userId.toString()))
                                      .answers(investmentSurvey.getAnswers().stream()
                                                               .map(answer -> SurveySubmissionCommand.Answer.builder()
                                                                                                            .questionId(answer.getQuestionId())
                                                                                                            .answer(answer.getAnswer())
                                                                                                            .build())
                                                               .toList())
                                      .build();
    }

    public static SurveyCompletionResponse convertSurveyResult(SurveyResult surveyResult) {
        return new SurveyCompletionResponse().riskLevel(surveyResult.getRiskLevel().getLevel());
    }

    public static InvestmentProfile convertInvestmentProfile(Baker baker) {
        return null;
    }

}
