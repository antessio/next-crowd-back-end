package nextcrowd.crowdfunding.infrastructure.project.cms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardData {

    private String id;
    private String name;
    private String description;
    private UploadedFile image;


}
