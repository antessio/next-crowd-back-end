package nextcrowd.crowdfunding.infrastructure.domain.baker.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "baker")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BakerEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "risk_level")
    private Integer riskLevel;


}
