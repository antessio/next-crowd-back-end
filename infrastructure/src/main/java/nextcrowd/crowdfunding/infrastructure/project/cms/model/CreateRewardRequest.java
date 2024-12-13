package nextcrowd.crowdfunding.infrastructure.project.cms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRewardRequest {

    private Reward data;

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reward {

        private String name;
        private String description;
        private ContentRef image;

    }

}
