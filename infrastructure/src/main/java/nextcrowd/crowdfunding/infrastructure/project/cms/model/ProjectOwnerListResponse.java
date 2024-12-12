package nextcrowd.crowdfunding.infrastructure.project.cms.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
@Builder
public class ProjectOwnerListResponse {

    private List<ProjectOwnerData> data;

}
