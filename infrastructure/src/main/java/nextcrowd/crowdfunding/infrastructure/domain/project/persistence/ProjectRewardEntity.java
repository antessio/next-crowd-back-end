package nextcrowd.crowdfunding.infrastructure.domain.project.persistence;


import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A ProjectOwner.
 */
@Entity
@Table(name = "project_reward")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectRewardEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ProjectRewardId id;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @MapsId("crowdfundingProjectId")
    @JoinColumn(name = "crowdfunding_project_id")
    private CrowdfundingProjectEntity crowdfundingProject;

    public String getName(){
        return id.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProjectRewardEntity that = (ProjectRewardEntity) o;
        return Objects.equals(id, that.id)
               && Objects.equals(imageUrl, that.imageUrl)
               && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(imageUrl);
        result = 31 * result + Objects.hashCode(description);
        return result;
    }

}
