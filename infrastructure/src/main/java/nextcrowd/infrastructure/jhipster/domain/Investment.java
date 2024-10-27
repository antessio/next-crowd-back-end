package nextcrowd.infrastructure.jhipster.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A Investment.
 */
@Entity
@Table(name = "investment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Investment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "baker_id", nullable = false)
    private String bakerId;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "money_transfer_id")
    private String moneyTransferId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "owner", "rewards", "investments" }, allowSetters = true)
    private CrowdfundingProject crowdfundingProject;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Investment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBakerId() {
        return this.bakerId;
    }

    public Investment bakerId(String bakerId) {
        this.setBakerId(bakerId);
        return this;
    }

    public void setBakerId(String bakerId) {
        this.bakerId = bakerId;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Investment amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMoneyTransferId() {
        return this.moneyTransferId;
    }

    public Investment moneyTransferId(String moneyTransferId) {
        this.setMoneyTransferId(moneyTransferId);
        return this;
    }

    public void setMoneyTransferId(String moneyTransferId) {
        this.moneyTransferId = moneyTransferId;
    }

    public CrowdfundingProject getCrowdfundingProject() {
        return this.crowdfundingProject;
    }

    public void setCrowdfundingProject(CrowdfundingProject crowdfundingProject) {
        this.crowdfundingProject = crowdfundingProject;
    }

    public Investment crowdfundingProject(CrowdfundingProject crowdfundingProject) {
        this.setCrowdfundingProject(crowdfundingProject);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Investment)) {
            return false;
        }
        return getId() != null && getId().equals(((Investment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Investment{" +
            "id=" + getId() +
            ", bakerId='" + getBakerId() + "'" +
            ", amount=" + getAmount() +
            ", moneyTransferId='" + getMoneyTransferId() + "'" +
            "}";
    }
}
