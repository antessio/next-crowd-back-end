package nextcrowd.infrastructure.jhipster.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A ProjectOwner.
 */
@Entity
@Table(name = "project_owner")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectOwner implements Serializable {

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

    @JsonIgnoreProperties(value = { "owner", "rewards", "investments" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "owner")
    private CrowdfundingProject crowdfundingProject;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProjectOwner id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public ProjectOwner name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public ProjectOwner imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public CrowdfundingProject getCrowdfundingProject() {
        return this.crowdfundingProject;
    }

    public void setCrowdfundingProject(CrowdfundingProject crowdfundingProject) {
        if (this.crowdfundingProject != null) {
            this.crowdfundingProject.setOwner(null);
        }
        if (crowdfundingProject != null) {
            crowdfundingProject.setOwner(this);
        }
        this.crowdfundingProject = crowdfundingProject;
    }

    public ProjectOwner crowdfundingProject(CrowdfundingProject crowdfundingProject) {
        this.setCrowdfundingProject(crowdfundingProject);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectOwner)) {
            return false;
        }
        return getId() != null && getId().equals(((ProjectOwner) o).getId());
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
