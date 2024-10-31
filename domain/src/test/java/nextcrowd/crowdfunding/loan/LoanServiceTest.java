package nextcrowd.crowdfunding.loan;


import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.github.f4b6a3.uuid.UuidCreator;

import nextcrowd.crowdfunding.loan.command.LoanCreationCommand;
import nextcrowd.crowdfunding.loan.event.ChargeCreatedEvent;
import nextcrowd.crowdfunding.loan.event.ChargePaidEvent;
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
import nextcrowd.crowdfunding.loan.port.PaymentService;
import nextcrowd.crowdfunding.loan.port.PaymentServiceChargeId;

class LoanServiceTest {

    private LoanRepository loanRepository;
    private ChargeRepository chargeRepository;
    private EventPublisher eventPublisher;
    private LoanService loanService;
    private PaymentService paymentService;
    private Clock clock;

    @BeforeEach
    void setUp() {
        eventPublisher = mock(EventPublisher.class);
        loanRepository = mock(LoanRepository.class);
        chargeRepository = mock(ChargeRepository.class);
        paymentService = mock(PaymentService.class);
        clock = mock(Clock.class);
        loanService = new LoanService(loanRepository, chargeRepository, paymentService, clock, eventPublisher);
    }

    @Nested
    @DisplayName("loan creation")
    class LoanCreationTest {

        @Test
        void shouldCreateLoanAndPublishEvent() {
            // given
            LoanCreationCommand command = LoanCreationCommand.builder()
                                                             .debtorId(new DebtorId(UuidCreator.getTimeOrderedEpoch().toString()))
                                                             .investments(List.of(
                                                                     Investment.builder()
                                                                               .amount(new BigDecimal(300))
                                                                               .lenderId(new LenderId(UuidCreator.getTimeOrderedEpoch().toString()))
                                                                               .build(),
                                                                     Investment.builder()
                                                                               .amount(new BigDecimal(1300))
                                                                               .lenderId(new LenderId(UuidCreator.getTimeOrderedEpoch().toString()))
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
                    .isThrownBy(() -> loanService.createCharges(loanId))
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

    }

    @Nested
    @DisplayName("charges executions")
    class ChargesExecution {

        @Test
        @DisplayName("should create payment for charges in pending")
        void shouldChargePendingCharges() {
            // given
            Instant now = Instant.now();
            mockNow(now);

            LocalDate targetDate = LocalDate.ofInstant(now, ZoneId.of("UTC"));
            List<Charge> expectedCharges = List.of(Charge.builder()
                                                         .loanId(randomLoanId())
                                                         .id(randomChargeId())
                                                         .dueDate(targetDate.minusDays(1))
                                                         .status(Charge.ChargeStatus.PENDING)
                                                         .amount(new BigDecimal("30.00"))
                                                         .build());
            when(chargeRepository.findByDueDateBefore(targetDate))
                    .thenReturn(expectedCharges.stream());

            when(paymentService.createCharge(any())).thenAnswer((_invocation) -> getRandomPaymentServiceChargeId());

            // when
            loanService.performCharges(targetDate);

            // then
            ArgumentCaptor<BigDecimal> chargeAmountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
            verify(paymentService, times(expectedCharges.size())).createCharge(chargeAmountCaptor.capture());
            assertThat(chargeAmountCaptor.getAllValues())
                    .containsOnly(expectedCharges.stream().map(Charge::getAmount).toArray(BigDecimal[]::new));
            ArgumentCaptor<Charge> chargeCaptor = ArgumentCaptor.forClass(Charge.class);
            verify(chargeRepository, times(expectedCharges.size())).save(chargeCaptor.capture());
            assertThat(chargeCaptor.getAllValues())
                    .allMatch(c -> c.getPaymentServiceChargeId() != null);

        }

        @Test
        @DisplayName("should skip payment for charges already created")
        void shouldSkipPaymentForChargesAlreadyCreated() {
            // given
            Instant now = Instant.now();
            mockNow(now);

            LocalDate targetDate = LocalDate.ofInstant(now, ZoneId.of("UTC"));
            List<Charge> expectedCharges = List.of(Charge.builder()
                                                         .loanId(randomLoanId())
                                                         .id(randomChargeId())
                                                         .dueDate(targetDate.minusDays(1))
                                                         .status(Charge.ChargeStatus.PENDING)
                                                         .amount(new BigDecimal("30.00"))
                                                         .paymentServiceChargeId(getRandomPaymentServiceChargeId())
                                                         .build());
            when(chargeRepository.findByDueDateBefore(targetDate))
                    .thenReturn(expectedCharges.stream());


            // when
            loanService.performCharges(targetDate);

            // verify
            verify(paymentService, times(0)).createCharge(any());
            verify(chargeRepository, times(0)).save(any());
        }


    }


    @Nested
    @DisplayName("charge payment received")
    class ChargePaymentReceived {

        @DisplayName("charge successful")
        @Test
        public void shouldMarkChargeAsSuccessful() {
            // given
            PaymentServiceChargeId paymentServiceChargeId = getRandomPaymentServiceChargeId();
            ChargeId chargeId = randomChargeId();
            when(chargeRepository.findByPaymentServiceChargeId(paymentServiceChargeId))
                    .thenReturn(Optional.of(Charge.builder()
                                                  .loanId(randomLoanId())
                                                  .id(chargeId)
                                                  .dueDate(LocalDate.now().minusDays(1))
                                                  .status(Charge.ChargeStatus.PENDING)
                                                  .amount(new BigDecimal("30.00"))
                                                  .paymentServiceChargeId(paymentServiceChargeId)
                                                  .build()));

            // when
            loanService.chargeExecuted(paymentServiceChargeId);

            // then
            ArgumentCaptor<Charge> chargeArgumentCaptor = ArgumentCaptor.forClass(Charge.class);
            verify(chargeRepository).save(chargeArgumentCaptor.capture());
            assertThat(chargeArgumentCaptor.getValue())
                    .matches(c -> c.getStatus() == Charge.ChargeStatus.PAID);
            verify(eventPublisher).publish(ChargePaidEvent.builder()
                                                          .id(chargeId)
                                                          .build());
        }

        @DisplayName("charge not found")
        @Test
        public void shouldFailIfChargeNotFound() {
            // given
            PaymentServiceChargeId paymentServiceChargeId = getRandomPaymentServiceChargeId();
            when(chargeRepository.findByPaymentServiceChargeId(paymentServiceChargeId))
                    .thenReturn(Optional.empty());

            // when
            // then
            assertThatExceptionOfType(LoanException.class)
                    .isThrownBy(() -> loanService.chargeExecuted(paymentServiceChargeId))
                    .matches(e -> e.getReason() == LoanException.Reason.CHARGE_NOT_FOUND);

            verify(chargeRepository, times(0)).save(any());
        }

        @DisplayName("charge not pending")
        @Test
        public void shouldDoNothingfChargeNotPending() {
            // given
            PaymentServiceChargeId paymentServiceChargeId = getRandomPaymentServiceChargeId();
            when(chargeRepository.findByPaymentServiceChargeId(paymentServiceChargeId))
                    .thenReturn(Optional.of(Charge.builder()
                                                  .loanId(randomLoanId())
                                                  .id(randomChargeId())
                                                  .dueDate(LocalDate.now().minusDays(1))
                                                  .status(Charge.ChargeStatus.PAID)
                                                  .amount(new BigDecimal("30.00"))
                                                  .paymentServiceChargeId(paymentServiceChargeId)
                                                  .build()));

            // when
            loanService.chargeExecuted(paymentServiceChargeId);

            // then
            verify(chargeRepository, times(0)).save(any());

        }

        @DisplayName("charge missing payment service charge id")
        @Test
        public void shouldFailIfChargeMissingPaymentServiceChargeId() {
            // given
            PaymentServiceChargeId paymentServiceChargeId = getRandomPaymentServiceChargeId();
            when(chargeRepository.findByPaymentServiceChargeId(paymentServiceChargeId))
                    .thenReturn(Optional.of(Charge.builder()
                                                  .loanId(randomLoanId())
                                                  .id(randomChargeId())
                                                  .dueDate(LocalDate.now().minusDays(1))
                                                  .status(Charge.ChargeStatus.PENDING)
                                                  .amount(new BigDecimal("30.00"))
                                                  .build()));

            // when
            // then
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() ->
                                        loanService.chargeExecuted(paymentServiceChargeId));
        }

    }

    private static PaymentServiceChargeId getRandomPaymentServiceChargeId() {
        return new PaymentServiceChargeId(UuidCreator.getTimeOrderedEpoch().toString());
    }

    private DebtorId randomDebtorId() {
        return new DebtorId(UuidCreator.getTimeOrderedEpoch().toString());
    }

    private ChargeId randomChargeId() {
        return new ChargeId(UuidCreator.getTimeOrderedEpoch().toString());
    }

    private void mockNow(Instant now) {
        when(clock.instant()).thenReturn(now);
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
    }

    private LenderId randomLenderId() {
        return new LenderId(UuidCreator.getTimeOrderedEpoch().toString());
    }

    private LoanId randomLoanId() {
        return new LoanId(UuidCreator.getTimeOrderedEpoch().toString());
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

}