package nextcrowd.crowdfunding.infrastructure.project.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import nextcrowd.crowdfunding.infrastructure.project.persistence.InvestmentEntity;
import nextcrowd.crowdfunding.project.model.BakerId;
import nextcrowd.crowdfunding.project.model.Investment;
import nextcrowd.crowdfunding.project.model.InvestmentId;
import nextcrowd.crowdfunding.project.model.MoneyTransferId;

public class InvestmentAdapter {

    public static Investment toDomain(InvestmentEntity entity) {
        return Investment.builder()
                         .id(new InvestmentId(entity.getId().toString()))
                         .status(entity.getStatus())
                         .amount(entity.getAmount())
                         .status(entity.getStatus())
                         .bakerId(Optional.ofNullable(entity.getBakerId())
                                          .map(UUID::toString)
                                          .map(BakerId::new)
                                          .orElse(null))
                         .moneyTransferId(Optional.ofNullable(entity.getMoneyTransferId())
                                                  .map(UUID::toString)
                                                  .map(MoneyTransferId::new)
                                                  .orElse(null))
                         .build();
    }

    // Convert from Domain to Entity
    public static InvestmentEntity toEntity(Investment investment) {
        return InvestmentEntity.builder()
                               .id(UUID.fromString(investment.getId().id()))
                               .amount(investment.getAmount())
                               .status(investment.getStatus())
                               .bakerId(UUID.fromString(investment.getBakerId().id()))
                               .moneyTransferId(Optional.ofNullable(investment.getMoneyTransferId())
                                                        .map(MoneyTransferId::id)
                                                        .map(UUID::fromString)
                                                        .orElse(null))
                               .build();
    }

}
