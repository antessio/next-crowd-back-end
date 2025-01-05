package nextcrowd.crowdfunding.infrastructure.domain.project.cms.model;

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
public class ProjectOwnerData {

    private String id;
    private String name;
    private UploadedFile image;


}
