package nextcrowd.crowdfunding.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A CrowdfundingProject.
 */
@Entity
@Table(name = "crowdfunding_project")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CrowdfundingProject implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "requested_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal requestedAmount;

    @NotNull
    @Column(name = "collected_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal collectedAmount;

    @NotNull
    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "image_url")
    private String imageUrl;

    @NotNull
    @Column(name = "risk", nullable = false)
    private Integer risk;

    @NotNull
    @Column(name = "project_start_date", nullable = false)
    private Instant projectStartDate;

    @NotNull
    @Column(name = "project_end_date", nullable = false)
    private Instant projectEndDate;

    @NotNull
    @Column(name = "number_of_backers", nullable = false)
    private Integer numberOfBackers;

    @Column(name = "summary")
    private String summary;

    //@Lob
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "expected_profit", precision = 21, scale = 2, nullable = false)
    private BigDecimal expectedProfit;

    @NotNull
    @Column(name = "minimum_investment", precision = 21, scale = 2, nullable = false)
    private BigDecimal minimumInvestment;

    @Column(name = "project_video_url")
    private String projectVideoUrl;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "crowdfundingProject")
    @JsonIgnoreProperties(value = { "crowdfundingProject" }, allowSetters = true)
    private Set<ProjectReward> rewards = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "projects" }, allowSetters = true)
    private CrowdfundingProjectOwner owner;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CrowdfundingProject id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public CrowdfundingProject title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getRequestedAmount() {
        return this.requestedAmount;
    }

    public CrowdfundingProject requestedAmount(BigDecimal requestedAmount) {
        this.setRequestedAmount(requestedAmount);
        return this;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public BigDecimal getCollectedAmount() {
        return this.collectedAmount;
    }

    public CrowdfundingProject collectedAmount(BigDecimal collectedAmount) {
        this.setCollectedAmount(collectedAmount);
        return this;
    }

    public void setCollectedAmount(BigDecimal collectedAmount) {
        this.collectedAmount = collectedAmount;
    }

    public String getCurrency() {
        return this.currency;
    }

    public CrowdfundingProject currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public CrowdfundingProject imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getRisk() {
        return this.risk;
    }

    public CrowdfundingProject risk(Integer risk) {
        this.setRisk(risk);
        return this;
    }

    public void setRisk(Integer risk) {
        this.risk = risk;
    }

    public Instant getProjectStartDate() {
        return this.projectStartDate;
    }

    public CrowdfundingProject projectStartDate(Instant projectStartDate) {
        this.setProjectStartDate(projectStartDate);
        return this;
    }

    public void setProjectStartDate(Instant projectStartDate) {
        this.projectStartDate = projectStartDate;
    }

    public Instant getProjectEndDate() {
        return this.projectEndDate;
    }

    public CrowdfundingProject projectEndDate(Instant projectEndDate) {
        this.setProjectEndDate(projectEndDate);
        return this;
    }

    public void setProjectEndDate(Instant projectEndDate) {
        this.projectEndDate = projectEndDate;
    }

    public Integer getNumberOfBackers() {
        return this.numberOfBackers;
    }

    public CrowdfundingProject numberOfBackers(Integer numberOfBackers) {
        this.setNumberOfBackers(numberOfBackers);
        return this;
    }

    public void setNumberOfBackers(Integer numberOfBackers) {
        this.numberOfBackers = numberOfBackers;
    }

    public String getSummary() {
        return this.summary;
    }

    public CrowdfundingProject summary(String summary) {
        this.setSummary(summary);
        return this;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return this.description;
    }

    public CrowdfundingProject description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getExpectedProfit() {
        return this.expectedProfit;
    }

    public CrowdfundingProject expectedProfit(BigDecimal expectedProfit) {
        this.setExpectedProfit(expectedProfit);
        return this;
    }

    public void setExpectedProfit(BigDecimal expectedProfit) {
        this.expectedProfit = expectedProfit;
    }

    public BigDecimal getMinimumInvestment() {
        return this.minimumInvestment;
    }

    public CrowdfundingProject minimumInvestment(BigDecimal minimumInvestment) {
        this.setMinimumInvestment(minimumInvestment);
        return this;
    }

    public void setMinimumInvestment(BigDecimal minimumInvestment) {
        this.minimumInvestment = minimumInvestment;
    }

    public String getProjectVideoUrl() {
        return this.projectVideoUrl;
    }

    public CrowdfundingProject projectVideoUrl(String projectVideoUrl) {
        this.setProjectVideoUrl(projectVideoUrl);
        return this;
    }

    public void setProjectVideoUrl(String projectVideoUrl) {
        this.projectVideoUrl = projectVideoUrl;
    }

    public Set<ProjectReward> getRewards() {
        return this.rewards;
    }

    public void setRewards(Set<ProjectReward> projectRewards) {
        if (this.rewards != null) {
            this.rewards.forEach(i -> i.setCrowdfundingProject(null));
        }
        if (projectRewards != null) {
            projectRewards.forEach(i -> i.setCrowdfundingProject(this));
        }
        this.rewards = projectRewards;
    }

    public CrowdfundingProject rewards(Set<ProjectReward> projectRewards) {
        this.setRewards(projectRewards);
        return this;
    }

    public CrowdfundingProject addReward(ProjectReward projectReward) {
        this.rewards.add(projectReward);
        projectReward.setCrowdfundingProject(this);
        return this;
    }

    public CrowdfundingProject removeReward(ProjectReward projectReward) {
        this.rewards.remove(projectReward);
        projectReward.setCrowdfundingProject(null);
        return this;
    }

    public CrowdfundingProjectOwner getOwner() {
        return this.owner;
    }

    public void setOwner(CrowdfundingProjectOwner crowdfundingProjectOwner) {
        this.owner = crowdfundingProjectOwner;
    }

    public CrowdfundingProject owner(CrowdfundingProjectOwner crowdfundingProjectOwner) {
        this.setOwner(crowdfundingProjectOwner);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CrowdfundingProject)) {
            return false;
        }
        return getId() != null && getId().equals(((CrowdfundingProject) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CrowdfundingProject{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", requestedAmount=" + getRequestedAmount() +
            ", collectedAmount=" + getCollectedAmount() +
            ", currency='" + getCurrency() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            ", risk=" + getRisk() +
            ", projectStartDate='" + getProjectStartDate() + "'" +
            ", projectEndDate='" + getProjectEndDate() + "'" +
            ", numberOfBackers=" + getNumberOfBackers() +
            ", summary='" + getSummary() + "'" +
            ", description='" + getDescription() + "'" +
            ", expectedProfit=" + getExpectedProfit() +
            ", minimumInvestment=" + getMinimumInvestment() +
            ", projectVideoUrl='" + getProjectVideoUrl() + "'" +
            "}";
    }
}
