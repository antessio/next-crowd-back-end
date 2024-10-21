package nextcrowd.crowdfunding.loan;


import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import nextcrowd.crowdfunding.loan.command.LoanCreationCommand;
import nextcrowd.crowdfunding.loan.event.ChargeCreatedEvent;
import nextcrowd.crowdfunding.loan.event.LoanCreatedEvent;
import nextcrowd.crowdfunding.loan.exception.LoanException;
import nextcrowd.crowdfunding.loan.model.Charge;
import nextcrowd.crowdfunding.loan.model.ChargeId;
import nextcrowd.crowdfunding.loan.model.DebtorId;
import nextcrowd.crowdfunding.loan.model.Investment;
import nextcrowd.crowdfunding.loan.model.LenderId;
import nextcrowd.crowdfunding.loan.model.Loan;
import nextcrowd.crowdfunding.loan.model.LoanId;
import nextcrowd.crowdfunding.loan.port.ChargeRepository;
import nextcrowd.crowdfunding.loan.port.EventPublisher;
import nextcrowd.crowdfunding.loan.port.LoanRepository;

class LoanServiceTest {

    private LoanRepository loanRepository;
    private ChargeRepository chargeRepository;
    private EventPublisher eventPublisher;
    private LoanService loanService;
    private Clock clock;

    @BeforeEach
    void setUp() {
        eventPublisher = mock(EventPublisher.class);
        loanRepository = mock(LoanRepository.class);
        chargeRepository = mock(ChargeRepository.class);
        clock = mock(Clock.class);
        loanService = new LoanService(loanRepository, chargeRepository, clock, eventPublisher);
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
                                                             .durationInMonths(30)
                                                             .build();

            // when
            Loan loan = loanService.createLoan(command);

            // then
            assertThat(loan)
                    .matches(l -> l.getDebtorId().equals(command.getDebtorId()))
                    .matches(l -> l.getId() != null)
                    .matches(l -> l.getInvestments().equals(command.getInvestments()))
                    .matches(l -> l.getDurationInMonths() == command.getDurationInMonths());
            ArgumentCaptor<Loan> argumentCaptor = ArgumentCaptor.forClass(Loan.class);
            verify(loanRepository).save(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue())
                    .matches(l -> l.getId().equals(loan.getId()))
                    .matches(l -> l.getDebtorId().equals(command.getDebtorId()))
                    .matches(l -> l.getInvestments().equals(command.getInvestments()))
                    .matches(l -> l.getDurationInMonths() == command.getDurationInMonths());
            verify(eventPublisher).publish(LoanCreatedEvent.builder()
                                                           .id(loan.getId())
                                                           .debtorId(loan.getDebtorId())
                                                           .build());

        }

    }

    @Nested
    @DisplayName("charge creation")
    class ChargeCreation {

        @Test
        @DisplayName("should fail if loan not found")
        void shouldFailLoanNotFound() {
            // given
            LoanId loanId = randomLoanId();
            Instant now = Instant.now();
            mockNow(now);
            when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

            // when
            assertThatExceptionOfType(LoanException.class)
                    .isThrownBy(()->            loanService.createCharges(loanId))
                    .matches(e -> e.getReason() == LoanException.Reason.LOAN_NOT_FOUND);
        }

        @Test
        @DisplayName("should create charges based on the debit with investors")
        void shouldCreateChargesFromDebitWithInvestors() {
            // given
            LoanId loanId = randomLoanId();
            Instant now = Instant.now();
            mockNow(now);
            DebtorId debtorId = randomDebtorId();
            when(loanRepository.findById(loanId)).thenReturn(Optional.of(Loan.builder()
                                                                             .badDebt(false)
                                                                             .durationInMonths(3)
                                                                             .debtorId(debtorId)
                                                                             .id(loanId)
                                                                             .investments(List.of(
                                                                                     Investment.builder()
                                                                                               .lenderId(randomLenderId())
                                                                                               .amount(new BigDecimal(300))
                                                                                               .interestRate(10)
                                                                                               .build(),
                                                                                     Investment.builder()
                                                                                               .lenderId(randomLenderId())
                                                                                               .amount(new BigDecimal(400))
                                                                                               .interestRate(5)
                                                                                               .build()
                                                                             ))
                                                                             .build()));

            // when
            loanService.createCharges(loanId);

            // then
            Charge[] expectedCharges = {
                    Charge
                            .builder()
                            .id(randomChargeId())
                            .status(Charge.ChargeStatus.PENDING)
                            .loanId(loanId)
                            .dueDate(LocalDate.ofInstant(now, ZoneId.of("UTC")).plusMonths(1))
                            // (330 + 440) / 3 = (300+(300*10/100) + (400+400*5/100) ) / 3 (months)
                            .amount(new BigDecimal("256.67"))
                            .build(),
                    Charge
                            .builder()
                            .id(randomChargeId())
                            .status(Charge.ChargeStatus.PENDING)
                            .loanId(loanId)
                            .dueDate(LocalDate.ofInstant(now, ZoneId.of("UTC")).plusMonths(2))
                            // (330 + 420) / 3 = (300+(300*10/100) + (400+400*5/100) ) / 3 (months)
                            .amount(new BigDecimal("256.67"))
                            .build(),
                    Charge
                            .builder()
                            .id(randomChargeId())
                            .status(Charge.ChargeStatus.PENDING)
                            .loanId(loanId)
                            .dueDate(LocalDate.ofInstant(now, ZoneId.of("UTC")).plusMonths(3))
                            // (330 + 420) / 3 = (300+(300*10/100) + (400+400*5/100) ) / 3 (months)
                            .amount(new BigDecimal("256.67"))
                            .build()
            };
            verifyChargesSaved(expectedCharges);
            verifyChargesCreatedEventPublished(expectedCharges, debtorId, loanId);

        }

        private void verifyChargesCreatedEventPublished(Charge[] expectedCharges, DebtorId debtorId, LoanId loanId) {
            ChargeCreatedEvent[] expectedEvents = Arrays.stream(expectedCharges).map(c -> ChargeCreatedEvent.builder()
                                                                                                            .dueDate(c.getDueDate())
                                                                                                            .id(c.getId())
                                                                                                            .amount(c.getAmount())
                                                                                                            .debtorId(debtorId)
                                                                                                            .loanId(loanId)
                                                                                                            .build()).toArray(ChargeCreatedEvent[]::new);
            ArgumentCaptor<ChargeCreatedEvent> chargeCreatedEventArgumentCaptor = ArgumentCaptor.forClass(ChargeCreatedEvent.class);
            verify(eventPublisher, times(expectedEvents.length)).publish(chargeCreatedEventArgumentCaptor.capture());
            assertThat(chargeCreatedEventArgumentCaptor.getAllValues())
                    .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                    .containsOnly(expectedEvents);
        }

        private void verifyChargesSaved(Charge[] expectedCharges) {
            ArgumentCaptor<Charge> chargeArgumentCaptor = ArgumentCaptor.forClass(Charge.class);
            verify(chargeRepository, times(expectedCharges.length)).save(chargeArgumentCaptor.capture());
            assertThat(chargeArgumentCaptor.getAllValues())
                    .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                    .containsOnly(
                            expectedCharges
                    );
        }

    }

    private DebtorId randomDebtorId() {
        return new DebtorId(UUID.randomUUID().toString());
    }

    private ChargeId randomChargeId() {
        return new ChargeId(UUID.randomUUID().toString());
    }

    private void mockNow(Instant now) {
        when(clock.instant()).thenReturn(now);
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
    }

    private LenderId randomLenderId() {
        return new LenderId(UUID.randomUUID().toString());
    }

    private LoanId randomLoanId() {
        return new LoanId(UUID.randomUUID().toString());
    }

}