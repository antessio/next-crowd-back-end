package nextcrowd.crowdfunding.baker.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Baker {
    BakerId bakerId;
    RiskLevel riskLevel;

}
