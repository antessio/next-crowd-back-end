package nextcrowd.crowdfunding.infrastructure;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.f4b6a3.uuid.UuidCreator;
import com.github.javafaker.Faker;

import nextcrowd.crowdfunding.infrastructure.project.persistence.ProjectOwnerEntity;
import nextcrowd.crowdfunding.infrastructure.security.persistence.Role;
import nextcrowd.crowdfunding.infrastructure.security.persistence.User;
import nextcrowd.crowdfunding.project.model.BakerId;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.Investment;
import nextcrowd.crowdfunding.project.model.InvestmentId;
import nextcrowd.crowdfunding.project.model.InvestmentStatus;
import nextcrowd.crowdfunding.project.model.MoneyTransferId;
import nextcrowd.crowdfunding.project.model.ProjectContent;
import nextcrowd.crowdfunding.project.model.ProjectId;
import nextcrowd.crowdfunding.project.model.ProjectOwner;
import nextcrowd.crowdfunding.project.model.ProjectOwnerId;
import nextcrowd.crowdfunding.project.model.ProjectReward;
import nextcrowd.crowdfunding.project.model.UploadedResource;
import nextcrowd.crowdfunding.project.model.UploadedResourceId;

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
                           .id(new ProjectOwnerId(UuidCreator.getTimeOrderedEpoch().toString()))
                           .name(faker.lebowski().character())
                           .build();
    }

    public static CrowdfundingProject buildRandomProject(ProjectOwner projectOwner) {
        List<Investment> investments = List.of(buildRandomInvestment());
        CrowdfundingProject project = CrowdfundingProject.builder()
                                                         .id(ProjectId.generateId())
                                                         .status(getRandomStatus())
                                                         .requestedAmount(BigDecimal.valueOf(random.nextInt(100000)
                                                                                             + 1000))  // Random amount between 1000 and 100000
                                                         .currency("USD")  // Set to "USD" or randomize if needed
                                                         .numberOfBackers(random.nextInt(1000))  // Random number of backers up to 1000
                                                         .owner(projectOwner)
                                                         .risk(random.nextInt(5))
                                                         .minimumInvestment(BigDecimal.valueOf(random.nextInt(1000)
                                                                                               + 100))  // Random amount between 100 and 1000
                                                         .expectedProfit(BigDecimal.valueOf(random.nextInt(100)))
                                                         .numberOfBackers(random.nextInt(1000))  // Random number of backers up to 1000
                                                         .build();
        return project.updateInvestments(investments);
    }

    public static ProjectContent buildRandomProjectContent() {
        return ProjectContent.builder()
                             .title(faker.company().name())
                             .description(faker.lorem().sentence(10))
                             .risk(random.nextInt(5))
                             .minimumInvestment(BigDecimal.valueOf(random.nextInt(1000) + 100))
                             .expectedProfit(BigDecimal.valueOf(random.nextInt(100)))
                             .rewards(List.of(buildRandomProjectReward()))
                             .image(randomImage())
                             .video(randomImage())
                             .owner(buildRandomProjectOwnerContent())
                             .build();
    }

    private static ProjectContent.ProjectOwner buildRandomProjectOwnerContent() {
        return ProjectContent.ProjectOwner.builder()
                                          .id(UUID.randomUUID().toString())
                                          .name(faker.lebowski().character())
                                          .image(randomImage())
                                          .build();
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
                            .image(randomImage())
                            .build();
    }

    private static UploadedResource randomImage() {
        return UploadedResource.builder()
                               .url(faker.internet().url())
                               .path("/path/to/image.jpg")
                               .contentType("image/jpeg")
                               .location(UploadedResource.Location.LOCAL)
                               .id(new UploadedResourceId(UUID.randomUUID().toString()))
                               .build();
    }

    public static Instant buildRandomInstant() {
        return Instant.ofEpochMilli(faker.date().birthday().getTime());
    }

    public static OffsetDateTime buildRandomOffsetDateTime() {
        return OffsetDateTime.now().plusDays(new Random().nextInt(30));
    }

    public static ObjectMapper objectMapper() {
        return objectMapper;
    }

    public static Faker getFaker() {
        return faker;
    }

    public static BigDecimal getRandomAmount() {
        return BigDecimal.valueOf(random.nextInt(100000)
                                  + 1000);
    }

    public static User buildRandomUser(Set<String> roleAdmin) {
        return User.builder()
                   .id(UUID.randomUUID())
                   .email(faker.internet().emailAddress())
                   .password(faker.internet().password())
                   .fullName(faker.name().fullName())
                   .createdAt(Date.from(buildRandomInstant()))
                   .roles(roleAdmin.stream().map(r -> Role.builder().role(r).build()).collect(Collectors.toSet()))
                   .build();
    }

}
