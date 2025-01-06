package nextcrowd.crowdfunding.infrastructure.domain.project.persistence;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;



/**
 * A ProjectOwner.
 */
@Entity
@Table(name = "project_owner")
@SuppressWarnings("common-java:DuplicatedBlocks")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProjectOwnerEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "projectOwner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CrowdfundingProjectEntity> projects;

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setProjects(List<CrowdfundingProjectEntity> projects) {
        this.projects = projects;
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectOwner{" +
               "id=" + getId() +
               ", name='" + getName() + "'" +
               ", imageUrl='" + getImageUrl() + "'" +
               "}";
    }
}
