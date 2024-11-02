package nextcrowd.crowdfunding.infrastructure;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.f4b6a3.uuid.UuidCreator;
import com.github.javafaker.Faker;

import nextcrowd.crowdfunding.infrastructure.project.persistence.ProjectOwnerEntity;
import nextcrowd.crowdfunding.project.model.BakerId;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.Investment;
import nextcrowd.crowdfunding.project.model.InvestmentId;
import nextcrowd.crowdfunding.project.model.InvestmentStatus;
import nextcrowd.crowdfunding.project.model.MoneyTransferId;
import nextcrowd.crowdfunding.project.model.ProjectId;
import nextcrowd.crowdfunding.project.model.ProjectOwner;
import nextcrowd.crowdfunding.project.model.ProjectReward;

public class TestUtils {

    private static final Faker faker = new Faker();
    private static final Random random = new Random();
    private static ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    private TestUtils() {

    }

    public static ProjectOwnerEntity buildRandomProjectOwnerEntity() {
        return ProjectOwnerEntity.builder()
                                 .id(UUID.randomUUID())
                                 .imageUrl(faker.internet().url())
                                 .name(faker.lebowski().character())
                                 .build();
    }

    public static ProjectOwner buildRandomProjectOwner() {
        return ProjectOwner.builder()
                           .id(UuidCreator.getTimeOrderedEpoch().toString())
                           .imageUrl(faker.internet().url())
                           .name(faker.lebowski().character())
                           .build();
    }

    public static CrowdfundingProject buildRandomProject(ProjectOwner projectOwner) {
        List<Investment> investments = List.of(buildRandomInvestment());
        CrowdfundingProject project = CrowdfundingProject.builder()
                                                         .id(ProjectId.generateId())
                                                         .status(getRandomStatus())
                                                         .title(faker.company().name())  // Random project title
                                                         .description(faker.lorem().sentence(10))  // Random project description
                                                         .requestedAmount(BigDecimal.valueOf(random.nextInt(100000)
                                                                                             + 1000))  // Random amount between 1000 and 100000
                                                         .currency("USD")  // Set to "USD" or randomize if needed
                                                         .numberOfBackers(random.nextInt(1000))  // Random number of backers up to 1000
                                                         .owner(projectOwner)
                                                         .rewards(List.of(
                                                                 buildRandomProjectReward(),
                                                                 buildRandomProjectReward(),
                                                                 buildRandomProjectReward()
                                                         ))
                                                         .build();
        return project.updateInvestments(investments);
    }

    public static CrowdfundingProject.Status getRandomStatus(CrowdfundingProject.Status... excluding) {
        Set<CrowdfundingProject.Status> excludedStatuses = Set.of(excluding);
        List<CrowdfundingProject.Status> availableStatuses = Stream.of(CrowdfundingProject.Status.values())
                                                                   .filter(status -> !excludedStatuses.contains(status))
                                                                   .toList();
        return faker.options().option(availableStatuses.toArray(new CrowdfundingProject.Status[0]));
    }

    public static Investment buildRandomInvestment() {
        return Investment.builder()
                         .moneyTransferId(MoneyTransferId.generate())
                         .amount(BigDecimal.valueOf(random.nextInt(100000) + 1000))  // Random amount between 1000 and 100000)
                         .bakerId(BakerId.generate())
                         .status(faker.options().option(InvestmentStatus.values()))
                         .id(InvestmentId.generate())
                         .build();
    }


    public static ProjectReward buildRandomProjectReward() {
        return ProjectReward.builder()
                            .description(faker.lorem().sentence(10))  // Random description
                            .name(faker.commerce().productName())
                            .imageUrl(faker.internet().url())
                            .build();
    }

    public static Instant buildRandomInstant() {
        return Instant.ofEpochMilli(faker.date().birthday().getTime());
    }
    public static OffsetDateTime buildRandomOffsetDateTime() {
        return OffsetDateTime.now().plusDays(new Random().nextInt(30));
    }

    public static ObjectMapper objectMapper(){
        return objectMapper;
    }

    public static Faker getFaker() {
        return faker;
    }

    public static BigDecimal getRandomAmount() {
        return BigDecimal.valueOf(random.nextInt(100000)
                                  + 1000);
    }

}
