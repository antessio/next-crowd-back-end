package nextcrowd.crowdfunding.infrastructure.project.persistence;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;


/**
 * A CrowdfundingProject.
 */
@Entity
@Table(name = "crowdfunding_project")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CrowdfundingProjectEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "title")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CrowdfundingProject.Status status;

    @Column(name = "requested_amount", precision = 21, scale = 2)
    private BigDecimal requestedAmount;

    @Column(name = "collected_amount", precision = 21, scale = 2)
    private BigDecimal collectedAmount;

    @Column(name = "currency")
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

    @ManyToOne
    @JoinColumn(name = "project_owner_id")
    private ProjectOwnerEntity projectOwner;

    @OneToMany(mappedBy = "crowdfundingProject", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ProjectRewardEntity> rewards = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "crowdfunding_project_id")  // Define the foreign key in the investment table
    @JsonIgnoreProperties(value = { "crowdfundingProject" }, allowSetters = true)
    private Set<InvestmentEntity> investments = new HashSet<>();


}
