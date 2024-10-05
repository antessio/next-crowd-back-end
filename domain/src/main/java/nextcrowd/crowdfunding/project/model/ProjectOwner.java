package nextcrowd.crowdfunding.project.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProjectOwner {
    private String id;
    private String name;
    private String imageUrl;
}

