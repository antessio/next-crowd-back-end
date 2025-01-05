package nextcrowd.crowdfunding.infrastructure.domain.project.persistence;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nextcrowd.crowdfunding.project.model.InvestmentStatus;

/**
 * A Investment.
 */
@Entity
@Table(name = "investment")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InvestmentEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "baker_id", nullable = false)
    private UUID bakerId;

    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvestmentStatus status;

    @Column(name = "money_transfer_id")
    private UUID moneyTransferId;



}
