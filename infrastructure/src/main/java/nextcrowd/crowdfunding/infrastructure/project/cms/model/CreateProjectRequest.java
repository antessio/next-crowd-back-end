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
public class CreateProjectRequest {

    private Project data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Project {

        private String title;
        private String description;
        private String longDescription;
        private ContentRef image;
        private ContentRef video;
        private String currency;
        private Double requestedAmount;
        private Double collectedAmount;
        private Integer numberOfBackers;
        private Integer risk;
        private Double expectedProfit;
        private Double minimumInvestment;
        private String startDate;
        private String endDate;
        private ContentRef projectOwner;
        private List<ContentRef> rewards;
        private String projectId;

    }

}
