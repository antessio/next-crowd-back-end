package nextcrowd.crowdfunding.infrastructure.project.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.github.javafaker.Faker;

import nextcrowd.crowdfunding.infrastructure.project.persistence.CrowdfundingProjectRepository;
import nextcrowd.crowdfunding.infrastructure.project.persistence.ProjectOwnerEntity;
import nextcrowd.crowdfunding.infrastructure.project.persistence.ProjectOwnerRepository;
import nextcrowd.crowdfunding.project.model.BakerId;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.Investment;
import nextcrowd.crowdfunding.project.model.InvestmentId;
import nextcrowd.crowdfunding.project.model.InvestmentStatus;
import nextcrowd.crowdfunding.project.model.MoneyTransferId;
import nextcrowd.crowdfunding.project.model.ProjectId;
import nextcrowd.crowdfunding.project.model.ProjectReward;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // Prevent replacing with an embedded DB
public class CrowdfundingProjectSpringDataRepositoryAdapterTest {

    @Autowired
    private CrowdfundingProjectRepository repository;
    @Autowired
    private ProjectOwnerRepository projectOwnerRepository;
    private List<ProjectOwnerEntity> projectOwnerEntities;
    private CrowdfundingProjectSpringDataRepositoryAdapter repositoryAdapter;
    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    @BeforeEach
    public void setUp() {
        // Initialize a test CrowdfundingProject entity
        repositoryAdapter = new CrowdfundingProjectSpringDataRepositoryAdapter(repository);
        projectOwnerEntities = List.of(
                buildRandomProjectOwner(),
                buildRandomProjectOwner(),
                buildRandomProjectOwner(),
                buildRandomProjectOwner()
        );
        projectOwnerRepository.saveAll(projectOwnerEntities);

    }

    private static ProjectOwnerEntity buildRandomProjectOwner() {
        return ProjectOwnerEntity.builder()
                                 .id(UUID.randomUUID())
                                 .imageUrl(faker.internet().url())
                                 .name(faker.lebowski().character())
                                 .build();
    }

    private CrowdfundingProject buildRandomProject() {
        List<Investment> investments = List.of(buildRandomInvestment());
        CrowdfundingProject project = CrowdfundingProject.builder()
                                                         .id(new ProjectId(UUID.randomUUID().toString()))
                                                         .status(faker.options().option(CrowdfundingProject.Status.values()))
                                                         .title(faker.company().name())  // Random project title
                                                         .description(faker.lorem().sentence(10))  // Random project description
                                                         .requestedAmount(BigDecimal.valueOf(random.nextInt(100000)
                                                                                             + 1000))  // Random amount between 1000 and 100000
                                                         .currency("USD")  // Set to "USD" or randomize if needed
                                                         .numberOfBackers(random.nextInt(1000))  // Random number of backers up to 1000
                                                         .owner(ProjectOwnerAdapter.toDomain(projectOwnerEntities.get(random.nextInt(projectOwnerEntities.size()))))
                                                         .rewards(List.of(
                                                                 buildRandomProjectReward(),
                                                                 buildRandomProjectReward(),
                                                                 buildRandomProjectReward()
                                                         ))
                                                         .build();
        return project.updateInvestments(investments);
    }

    private Investment buildRandomInvestment() {
        return Investment.builder()
                         .moneyTransferId(MoneyTransferId.generate())
                         .amount(BigDecimal.valueOf(random.nextInt(100000) + 1000))  // Random amount between 1000 and 100000)
                         .bakerId(BakerId.generate())
                         .status(faker.options().option(InvestmentStatus.values()))
                         .id(InvestmentId.generate())
                         .build();
    }


    private ProjectReward buildRandomProjectReward() {
        return ProjectReward.builder()
                            .description(faker.lorem().sentence(10))  // Random description
                            .name(faker.commerce().productName())
                            .imageUrl(faker.internet().url())
                            .build();
    }

    @Test
    public void shouldInsertAndFind() {
        // given
        CrowdfundingProject project = buildRandomProject();
        CrowdfundingProject savedProject = repositoryAdapter.save(project);

        // when
        Optional<CrowdfundingProject> foundProject = repositoryAdapter.findById(savedProject.getId());
        // then

        assertThat(foundProject).isPresent();
        assertThat(foundProject.get())
                .usingRecursiveComparison()
                .ignoringFields("rewards")  // Ignore the rewards field in the comparison
                .isEqualTo(project);

        // Separate assertion for rewards
        assertThat(foundProject.get().getRewards())
                .containsExactlyInAnyOrderElementsOf(project.getRewards());
    }

    @Test
    public void shouldUpdateProject() {
        // given
        CrowdfundingProject project = buildRandomProject();
        CrowdfundingProject savedProject = repositoryAdapter.save(project);
        // when
        CrowdfundingProject updatedProject = repositoryAdapter.save(savedProject.toBuilder()
                                                                                .title(faker.company().name())
                                                                                .description(faker.lorem().sentence(10))
                                                                                .build());
        Optional<CrowdfundingProject> foundProject = repositoryAdapter.findById(savedProject.getId());
        // then

        assertThat(foundProject).isPresent();
        assertThat(foundProject.get())
                .usingRecursiveComparison()
                .isEqualTo(updatedProject);
    }

}
