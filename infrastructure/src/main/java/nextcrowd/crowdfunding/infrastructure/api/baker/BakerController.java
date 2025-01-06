package nextcrowd.crowdfunding.infrastructure.api.baker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import nextcrowd.crowdfunding.baker.BakerServicePort;
import nextcrowd.crowdfunding.baker.api.BakersApi;
import nextcrowd.crowdfunding.baker.api.model.InvestmentProfile;
import nextcrowd.crowdfunding.baker.api.model.InvestmentSurvey;
import nextcrowd.crowdfunding.baker.api.model.SurveyCompletionResponse;
import nextcrowd.crowdfunding.baker.model.BakerId;
import nextcrowd.crowdfunding.baker.model.SurveyResult;
import nextcrowd.crowdfunding.infrastructure.api.baker.adapter.ApiConverter;
import nextcrowd.crowdfunding.infrastructure.security.persistence.User;
import nextcrowd.crowdfunding.infrastructure.security.service.SecurityUtils;

@Controller
public class BakerController implements BakersApi {

    private final BakerServicePort bakerService;

    public BakerController(BakerServicePort bakerService) {
        this.bakerService = bakerService;
    }

    @Override
    public ResponseEntity<InvestmentProfile> bakersInvestmentProfileGet() {
        User user = SecurityUtils.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return bakerService.getBaker(new BakerId(user.getId().toString()))
                           .map(ApiConverter::convertInvestmentProfile)
                           .map(ResponseEntity::ok)
                           .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<SurveyCompletionResponse> bakersInvestmentSurveyPost(InvestmentSurvey investmentSurvey) {
        User user = SecurityUtils.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        SurveyResult surveyResult = bakerService.submitSurvey(ApiConverter.convertSurveyRequest(
                investmentSurvey,
                user.getId()));
        return ResponseEntity.ok(ApiConverter.convertSurveyResult(surveyResult));
    }

}
