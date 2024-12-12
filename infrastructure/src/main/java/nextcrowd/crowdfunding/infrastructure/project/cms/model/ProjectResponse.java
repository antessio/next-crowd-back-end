package nextcrowd.crowdfunding.infrastructure.project.cms.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private String title;
    private String description;
    private String longDescription;
    private UploadedFile image;
    private UploadedFile video;
    private String currency;
    private Double requestedAmount;
    private Double collectedAmount;
    private Integer numberOfBackers;
    private Integer risk;
    private Double expectedProfit;
    private Double minimumInvestment;
    private String startDate;
    private String endDate;
    private ProjectOwnerData projectOwner;
    private List<RewardData> rewards;
    private String id;

}
