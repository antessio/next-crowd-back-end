package nextcrowd.infrastructure.jhipster.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import nextcrowd.infrastructure.jhipster.domain.enumeration.Status;

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
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @NotNull
    @Column(name = "requested_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal requestedAmount;

    @Column(name = "collected_amount", precision = 21, scale = 2)
    private BigDecimal collectedAmount;

    @NotNull
    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "project_start_date")
    private Instant projectStartDate;

    @Column(name = "project_end_date")
    private Instant projectEndDate;

    @Column(name = "number_of_backers")
    private Integer numberOfBackers;

    @Column(name = "description")
    private String description;

    @Column(name = "long_description")
    private String longDescription;

    @Column(name = "project_video_url")
    private String projectVideoUrl;

    @Column(name = "risk")
    private Integer risk;

    @Column(name = "expected_profit", precision = 21, scale = 2)
    private BigDecimal expectedProfit;

    @Column(name = "minimum_investment", precision = 21, scale = 2)
    private BigDecimal minimumInvestment;

    @JsonIgnoreProperties(value = { "crowdfundingProject" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private ProjectOwner owner;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "crowdfundingProject")
    @JsonIgnoreProperties(value = { "crowdfundingProject" }, allowSetters = true)
    private Set<ProjectReward> rewards = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "crowdfundingProject")
    @JsonIgnoreProperties(value = { "crowdfundingProject" }, allowSetters = true)
    private Set<Investment> investments = new HashSet<>();

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

    public Status getStatus() {
        return this.status;
    }

    public CrowdfundingProject status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public String getLongDescription() {
        return this.longDescription;
    }

    public CrowdfundingProject longDescription(String longDescription) {
        this.setLongDescription(longDescription);
        return this;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
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

    public ProjectOwner getOwner() {
        return this.owner;
    }

    public void setOwner(ProjectOwner projectOwner) {
        this.owner = projectOwner;
    }

    public CrowdfundingProject owner(ProjectOwner projectOwner) {
        this.setOwner(projectOwner);
        return this;
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

    public CrowdfundingProject addRewards(ProjectReward projectReward) {
        this.rewards.add(projectReward);
        projectReward.setCrowdfundingProject(this);
        return this;
    }

    public CrowdfundingProject removeRewards(ProjectReward projectReward) {
        this.rewards.remove(projectReward);
        projectReward.setCrowdfundingProject(null);
        return this;
    }

    public Set<Investment> getInvestments() {
        return this.investments;
    }

    public void setInvestments(Set<Investment> investments) {
        if (this.investments != null) {
            this.investments.forEach(i -> i.setCrowdfundingProject(null));
        }
        if (investments != null) {
            investments.forEach(i -> i.setCrowdfundingProject(this));
        }
        this.investments = investments;
    }

    public CrowdfundingProject investments(Set<Investment> investments) {
        this.setInvestments(investments);
        return this;
    }

    public CrowdfundingProject addInvestments(Investment investment) {
        this.investments.add(investment);
        investment.setCrowdfundingProject(this);
        return this;
    }

    public CrowdfundingProject removeInvestments(Investment investment) {
        this.investments.remove(investment);
        investment.setCrowdfundingProject(null);
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
            ", status='" + getStatus() + "'" +
            ", requestedAmount=" + getRequestedAmount() +
            ", collectedAmount=" + getCollectedAmount() +
            ", currency='" + getCurrency() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            ", projectStartDate='" + getProjectStartDate() + "'" +
            ", projectEndDate='" + getProjectEndDate() + "'" +
            ", numberOfBackers=" + getNumberOfBackers() +
            ", description='" + getDescription() + "'" +
            ", longDescription='" + getLongDescription() + "'" +
            ", projectVideoUrl='" + getProjectVideoUrl() + "'" +
            ", risk=" + getRisk() +
            ", expectedProfit=" + getExpectedProfit() +
            ", minimumInvestment=" + getMinimumInvestment() +
            "}";
    }
}
