package nextcrowd.infrastructure.jhipster.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A ProjectReward.
 */
@Entity
@Table(name = "project_reward")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectReward implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "owner", "rewards", "investments" }, allowSetters = true)
    private CrowdfundingProject crowdfundingProject;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProjectReward id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public ProjectReward name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public ProjectReward imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return this.description;
    }

    public ProjectReward description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CrowdfundingProject getCrowdfundingProject() {
        return this.crowdfundingProject;
    }

    public void setCrowdfundingProject(CrowdfundingProject crowdfundingProject) {
        this.crowdfundingProject = crowdfundingProject;
    }

    public ProjectReward crowdfundingProject(CrowdfundingProject crowdfundingProject) {
        this.setCrowdfundingProject(crowdfundingProject);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectReward)) {
            return false;
        }
        return getId() != null && getId().equals(((ProjectReward) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectReward{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
