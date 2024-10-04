package nextcrowd.crowdfunding.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A CrowdfundingProjectOwner.
 */
@Entity
@Table(name = "crowdfunding_project_owner")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CrowdfundingProjectOwner implements Serializable {

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
    @JsonIgnoreProperties(value = { "rewards", "owner" }, allowSetters = true)
    private Set<CrowdfundingProject> projects = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CrowdfundingProjectOwner id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public CrowdfundingProjectOwner name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public CrowdfundingProjectOwner imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Set<CrowdfundingProject> getProjects() {
        return this.projects;
    }

    public void setProjects(Set<CrowdfundingProject> crowdfundingProjects) {
        if (this.projects != null) {
            this.projects.forEach(i -> i.setOwner(null));
        }
        if (crowdfundingProjects != null) {
            crowdfundingProjects.forEach(i -> i.setOwner(this));
        }
        this.projects = crowdfundingProjects;
    }

    public CrowdfundingProjectOwner projects(Set<CrowdfundingProject> crowdfundingProjects) {
        this.setProjects(crowdfundingProjects);
        return this;
    }

    public CrowdfundingProjectOwner addProject(CrowdfundingProject crowdfundingProject) {
        this.projects.add(crowdfundingProject);
        crowdfundingProject.setOwner(this);
        return this;
    }

    public CrowdfundingProjectOwner removeProject(CrowdfundingProject crowdfundingProject) {
        this.projects.remove(crowdfundingProject);
        crowdfundingProject.setOwner(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CrowdfundingProjectOwner)) {
            return false;
        }
        return getId() != null && getId().equals(((CrowdfundingProjectOwner) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CrowdfundingProjectOwner{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            "}";
    }
}
