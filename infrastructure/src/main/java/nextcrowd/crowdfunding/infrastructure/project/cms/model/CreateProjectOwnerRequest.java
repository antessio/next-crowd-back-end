package nextcrowd.crowdfunding.infrastructure.project.cms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProjectOwnerRequest {
    private ProjectOwner data;

    @Builder(toBuilder = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProjectOwner{
        private String name;
        private ContentRef image;
    }

}
