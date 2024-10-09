package nextcrowd.crowdfunding.loan;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import nextcrowd.crowdfunding.loan.command.LoanCreationCommand;
import nextcrowd.crowdfunding.loan.event.LoanCreatedEvent;
import nextcrowd.crowdfunding.loan.model.DebtorId;
import nextcrowd.crowdfunding.loan.model.Investment;
import nextcrowd.crowdfunding.loan.model.LenderId;
import nextcrowd.crowdfunding.loan.model.Loan;
import nextcrowd.crowdfunding.loan.port.EventPublisher;
import nextcrowd.crowdfunding.loan.port.LoanRepository;

class LoanServiceTest {

    private LoanRepository loanRepository;
    private EventPublisher eventPublisher;
    private LoanService loanService;

    @BeforeEach
    void setUp() {
        eventPublisher = mock(EventPublisher.class);
        loanRepository = mock(LoanRepository.class);
        loanService = new LoanService(loanRepository, eventPublisher);
    }

    @Nested
    @DisplayName("loan creation")
    class LoanCreationTest {

        @Test
        void shouldCreateLoanAndPublishEvent() {
            // given
            LoanCreationCommand command = LoanCreationCommand.builder()
                                                             .debtorId(new DebtorId(UUID.randomUUID().toString()))
                                                             .investments(List.of(
                                                                     Investment.builder()
                                                                               .amount(new BigDecimal(300))
                                                                               .lenderId(new LenderId(UUID.randomUUID().toString()))
                                                                               .build(),
                                                                     Investment.builder()
                                                                               .amount(new BigDecimal(1300))
                                                                               .lenderId(new LenderId(UUID.randomUUID().toString()))
                                                                               .build()
                                                             ))
                                                             .build();

            // when
            Loan loan = loanService.createLoan(command);

            // then
            assertThat(loan)
                    .matches(l -> l.getDebtorId().equals(command.getDebtorId()))
                    .matches(l -> l.getId() != null)
                    .matches(l -> l.getInvestments().equals(command.getInvestments()));
            ArgumentCaptor<Loan> argumentCaptor = ArgumentCaptor.forClass(Loan.class);
            verify(loanRepository).save(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue())
                    .matches(l -> l.getId().equals(loan.getId()))
                    .matches(l -> l.getDebtorId().equals(command.getDebtorId()))
                    .matches(l -> l.getInvestments().equals(command.getInvestments()));
            verify(eventPublisher).publish(LoanCreatedEvent.builder()
                                                           .id(loan.getId())
                                                           .debtorId(loan.getDebtorId())
                                                           .build());

        }

    }

}