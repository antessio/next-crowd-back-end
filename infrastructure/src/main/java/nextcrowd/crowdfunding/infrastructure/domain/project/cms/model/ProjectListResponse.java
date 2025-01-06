package nextcrowd.crowdfunding.infrastructure.domain.project.cms.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectListResponse {
    private List<ProjectResponse> data;

}
