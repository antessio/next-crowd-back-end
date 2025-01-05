package nextcrowd.crowdfunding.baker.command;

import java.util.List;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.baker.model.BakerId;

@Value
@Builder
public class SurveySubmissionCommand {
    List<Answer> answers;
    BakerId bakerId;

    @Value
    @Builder
    public static class Answer{
        String questionId;
        String answer;
    }
}
