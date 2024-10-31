package nextcrowd.crowdfunding.infrastructure.project.persistence;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ProjectRewardId implements Serializable {

    private String name;
    private UUID crowdfundingProjectId;

    // Default constructor
    public ProjectRewardId() {
    }

    // Parameterized constructor
    public ProjectRewardId(String name, UUID crowdfundingProjectId) {
        this.name = name;
        this.crowdfundingProjectId = crowdfundingProjectId;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getCrowdfundingProjectId() {
        return crowdfundingProjectId;
    }

    public void setCrowdfundingProjectId(UUID crowdfundingProjectId) {
        this.crowdfundingProjectId = crowdfundingProjectId;
    }

    // Override equals and hashCode for composite key
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProjectRewardId that = (ProjectRewardId) o;
        return Objects.equals(name, that.name) &&
               Objects.equals(crowdfundingProjectId, that.crowdfundingProjectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, crowdfundingProjectId);
    }

}
