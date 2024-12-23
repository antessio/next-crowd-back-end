package nextcrowd.crowdfunding.project.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProjectReward {
    private String name;
    private UploadedResource image;
    private String description;
}
