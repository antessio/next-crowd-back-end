package nextcrowd.crowdfunding.infrastructure.project.cms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadedFile {
    private String id;
    private String url;
    private String mime;

}
