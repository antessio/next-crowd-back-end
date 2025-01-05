package nextcrowd.crowdfunding.baker.command;

import java.util.List;

import lombok.Value;
import nextcrowd.crowdfunding.baker.model.BakerId;

@Value
public class SurveySubmissionCommand {
    List<Answer> answers;
    BakerId bakerId;

    @Value
    public static class Answer{
        String questionId;
        String answer;
    }
}
