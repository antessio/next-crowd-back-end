package nextcrowd.crowdfunding.project.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UploadedResource {
    private UploadedResourceId id;
    private String url;
    private String contentType;
    private String path;
    private Location location;


    public enum Location{
        CMS,
        S3,
        LOCAL
    }
}
