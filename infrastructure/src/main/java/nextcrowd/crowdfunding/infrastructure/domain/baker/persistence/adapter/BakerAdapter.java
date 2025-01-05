package nextcrowd.crowdfunding.infrastructure.domain.baker.persistence.adapter;

import java.util.UUID;

import nextcrowd.crowdfunding.baker.model.Baker;
import nextcrowd.crowdfunding.baker.model.BakerId;
import nextcrowd.crowdfunding.baker.model.RiskLevel;
import nextcrowd.crowdfunding.infrastructure.domain.baker.persistence.BakerEntity;

public class BakerAdapter {
    private BakerAdapter(){

    }

    public static BakerEntity toEntity(Baker baker) {
        return new BakerEntity(UUID.fromString(baker.getBakerId().getId()), baker.getRiskLevel().getLevel());
    }

    public static Baker toDomain(BakerEntity bakerEntity) {
        return Baker.builder()
                .bakerId(new BakerId(bakerEntity.getId().toString()))
                .riskLevel(RiskLevel.fromLevel(bakerEntity.getRiskLevel()))
                .build();
    }


}
