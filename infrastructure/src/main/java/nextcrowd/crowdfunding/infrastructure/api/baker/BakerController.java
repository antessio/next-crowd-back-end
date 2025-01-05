package nextcrowd.crowdfunding.infrastructure.api.baker;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import nextcrowd.crowdfunding.baker.api.BakersApi;
import nextcrowd.crowdfunding.baker.api.model.InvestmentProfile;
import nextcrowd.crowdfunding.baker.api.model.InvestmentSurvey;
import nextcrowd.crowdfunding.baker.api.model.SurveyCompletionResponse;

@Controller
public class BakerController implements BakersApi {

    @Override
    public ResponseEntity<InvestmentProfile> bakersInvestmentProfileGet() {
        return BakersApi.super.bakersInvestmentProfileGet();
    }

    @Override
    public ResponseEntity<SurveyCompletionResponse> bakersInvestmentSurveyPost(InvestmentSurvey investmentSurvey) {
        return BakersApi.super.bakersInvestmentSurveyPost(investmentSurvey);
    }

}
