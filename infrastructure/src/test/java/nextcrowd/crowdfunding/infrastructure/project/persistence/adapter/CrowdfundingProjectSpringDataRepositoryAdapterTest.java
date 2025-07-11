package nextcrowd.crowdfunding.infrastructure.project.persistence.adapter;

import static nextcrowd.crowdfunding.infrastructure.TestUtils.buildRandomInvestment;
import static nextcrowd.crowdfunding.infrastructure.TestUtils.buildRandomProject;
import static nextcrowd.crowdfunding.infrastructure.TestUtils.buildRandomProjectOwnerEntity;
import static nextcrowd.crowdfunding.infrastructure.TestUtils.getRandomStatus;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.github.javafaker.Faker;

import nextcrowd.crowdfunding.infrastructure.BaseTestWithTestcontainers;
import nextcrowd.crowdfunding.infrastructure.domain.project.persistence.CrowdfundingProjectRepository;
import nextcrowd.crowdfunding.infrastructure.domain.project.persistence.InvestmentRepository;
import nextcrowd.crowdfunding.infrastructure.domain.project.persistence.ProjectOwnerEntity;
import nextcrowd.crowdfunding.infrastructure.domain.project.persistence.ProjectOwnerRepository;
import nextcrowd.crowdfunding.infrastructure.domain.project.persistence.adapter.CrowdfundingProjectSpringDataRepositoryAdapter;
import nextcrowd.crowdfunding.infrastructure.domain.project.persistence.adapter.ProjectOwnerAdapter;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.Investment;
import nextcrowd.crowdfunding.project.model.InvestmentStatus;
import nextcrowd.crowdfunding.project.model.ProjectOwner;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // Prevent replacing with an embedded DB
public class CrowdfundingProjectSpringDataRepositoryAdapterTest extends BaseTestWithTestcontainers {

    @Autowired
    private CrowdfundingProjectRepository repository;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private ProjectOwnerRepository projectOwnerRepository;
    @Autowired
    private InvestmentRepository investmentRepository;
    private static final Random random = new Random();
    private static final Faker faker = new Faker();
    private List<ProjectOwnerEntity> projectOwnerEntities;
    private CrowdfundingProjectSpringDataRepositoryAdapter repositoryAdapter;


    @BeforeEach
    public void setUp() {
        // Initialize a test CrowdfundingProject entity
        repositoryAdapter = new CrowdfundingProjectSpringDataRepositoryAdapter(repository, projectOwnerRepository, investmentRepository);
        projectOwnerEntities = List.of(
                buildRandomProjectOwnerEntity(),
                buildRandomProjectOwnerEntity(),
                buildRandomProjectOwnerEntity(),
                buildRandomProjectOwnerEntity()
        );
        try {
            System.out.println("dataSource = " + dataSource.getConnection().getMetaData().getURL());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        projectOwnerRepository.saveAll(projectOwnerEntities);

    }


    @Test
    public void shouldInsertAndFind() {
        // given
        CrowdfundingProject project = buildRandomProject(getRandomProjectOwnerFromExisting());
        CrowdfundingProject savedProject = repositoryAdapter.save(project);

        // when
        Optional<CrowdfundingProject> foundProject = repositoryAdapter.findById(savedProject.getId());
        // then

        assertThat(foundProject).isPresent();
        assertThat(foundProject.get())
                .usingRecursiveComparison()
                .isEqualTo(project);
    }

    private ProjectOwner getRandomProjectOwnerFromExisting() {
        return ProjectOwnerAdapter.toDomain(projectOwnerEntities.get(random.nextInt(projectOwnerEntities.size())));
    }

    private ProjectOwner getRandomProjectOwnerFromExisting(ProjectOwner ...exclude) {
        List<ProjectOwnerEntity> existing = projectOwnerEntities.stream()
                                                                .filter(Predicate.not(po -> List.of(exclude).contains(ProjectOwnerAdapter.toDomain(po))))
                                                                .toList();
        return ProjectOwnerAdapter.toDomain(existing.get(random.nextInt(existing.size())));
    }

    @Test
    public void shouldUpdateProject() {
        // given
        CrowdfundingProject project = buildRandomProject(getRandomProjectOwnerFromExisting());
        CrowdfundingProject savedProject = repositoryAdapter.save(project);
        // when
        CrowdfundingProject updatedProject = repositoryAdapter.save(savedProject.toBuilder().build());
        Optional<CrowdfundingProject> foundProject = repositoryAdapter.findById(savedProject.getId());
        // then

        assertThat(foundProject).isPresent();
        assertThat(foundProject.get())
                .usingRecursiveComparison()
                .isEqualTo(updatedProject);
    }

    @Test
    public void shouldFindProjectsByStatus() {
        // given
        CrowdfundingProject.Status targetStatus = getRandomStatus();
        List<CrowdfundingProject> expected = IntStream.range(0, 30)
                                                      .mapToObj(_ -> buildRandomProject(getRandomProjectOwnerFromExisting()).toBuilder()
                                                                                                                            .status(targetStatus)
                                                                                                                            .build())
                                                      .map(repositoryAdapter::save)
                                                      .toList();
        IntStream.range(0, 20)
                 .mapToObj(_ -> buildRandomProject(getRandomProjectOwnerFromExisting()).toBuilder()
                                                                                       .status(getRandomStatus(targetStatus))
                                                                                       .build())
                 .map(repositoryAdapter::save)
                 .forEach(repositoryAdapter::save);

        // when
        List<CrowdfundingProject> pendingReviewProjects = repositoryAdapter.findByStatusesOrderByAsc(Set.of(targetStatus), null).toList();

        // then
        assertThat(pendingReviewProjects).containsExactly(expected.toArray(new CrowdfundingProject[0]));
    }

    @Test
    public void shouldFindPendingInvestments() {
        // given

        int expectedPendingInvestmentsSize = 50;
        List<Investment> project1Investments = Stream.concat(
                                                             Stream.concat(
                                                                     IntStream.range(0, 20)
                                                                              .mapToObj(_ -> buildRandomInvestment().toBuilder().status(InvestmentStatus.ACCEPTED).build()),
                                                                     IntStream.range(0, expectedPendingInvestmentsSize)
                                                                              .mapToObj(_ -> buildRandomInvestment().toBuilder().status(InvestmentStatus.PENDING).build())
                                                             ), IntStream.range(0, 10)
                                                                         .mapToObj(_ -> buildRandomInvestment().toBuilder().status(InvestmentStatus.REFUSED).build()))
                                                     .toList();

        CrowdfundingProject project1 = buildRandomProject(getRandomProjectOwnerFromExisting()).toBuilder()
                                                                                              .investments(project1Investments)
                                                                                              .build();

        List<Investment> project2Investments = Stream.concat(
                                                             Stream.concat(
                                                                     IntStream.range(0, 10)
                                                                              .mapToObj(_ -> buildRandomInvestment().toBuilder().status(InvestmentStatus.ACCEPTED).build()),
                                                                     IntStream.range(0, 10)
                                                                              .mapToObj(_ -> buildRandomInvestment().toBuilder().status(InvestmentStatus.PENDING).build())
                                                             ), IntStream.range(0, 10)
                                                                         .mapToObj(_ -> buildRandomInvestment().toBuilder().status(InvestmentStatus.REFUSED).build()))
                                                     .toList();

        CrowdfundingProject project2 = buildRandomProject(getRandomProjectOwnerFromExisting()).toBuilder()
                                                                                              .investments(project2Investments)
                                                                                              .build();

        repositoryAdapter.save(project1);
        repositoryAdapter.save(project2);


        // when
        List<Investment> acceptedInvestments = repositoryAdapter.findInvestmentsByStatusesOrderByDesc(project1.getId(), null, Set.of(InvestmentStatus.PENDING))
                                                                .toList();

        // then
        assertThat(acceptedInvestments)
                .hasSize(expectedPendingInvestmentsSize)
                .containsExactlyInAnyOrderElementsOf(project1.getPendingInvestments());
    }

    @Test
    public void shouldFindAcceptedInvestments() {
        // given

        int expectedAcceptedInvestmentsSize = 40;
        List<Investment> project1Investments = Stream.concat(
                                                             Stream.concat(
                                                                     IntStream.range(0, expectedAcceptedInvestmentsSize)
                                                                              .mapToObj(_ -> buildRandomInvestment().toBuilder().status(InvestmentStatus.ACCEPTED).build()),
                                                                     IntStream.range(0, 10)
                                                                              .mapToObj(_ -> buildRandomInvestment().toBuilder().status(InvestmentStatus.PENDING).build())
                                                             ), IntStream.range(0, 10)
                                                                         .mapToObj(_ -> buildRandomInvestment().toBuilder().status(InvestmentStatus.REFUSED).build()))
                                                     .toList();

        CrowdfundingProject project1 = buildRandomProject(getRandomProjectOwnerFromExisting()).toBuilder()
                                                                                              .investments(project1Investments)
                                                                                              .build();

        List<Investment> project2Investments = Stream.concat(
                                                             Stream.concat(
                                                                     IntStream.range(0, 10)
                                                                              .mapToObj(_ -> buildRandomInvestment().toBuilder().status(InvestmentStatus.ACCEPTED).build()),
                                                                     IntStream.range(0, 10)
                                                                              .mapToObj(_ -> buildRandomInvestment().toBuilder().status(InvestmentStatus.PENDING).build())
                                                             ), IntStream.range(0, 10)
                                                                         .mapToObj(_ -> buildRandomInvestment().toBuilder().status(InvestmentStatus.REFUSED).build()))
                                                     .toList();

        CrowdfundingProject project2 = buildRandomProject(getRandomProjectOwnerFromExisting()).toBuilder()
                                                                                              .investments(project2Investments)
                                                                                              .build();

        repositoryAdapter.save(project1);
        repositoryAdapter.save(project2);


        // when
        List<Investment> acceptedInvestments = repositoryAdapter.findInvestmentsByStatusesOrderByDesc(project1.getId(), null, Set.of(InvestmentStatus.ACCEPTED))
                                                                .toList();

        // then
        assertThat(acceptedInvestments)
                .hasSize(expectedAcceptedInvestmentsSize)
                .containsExactlyInAnyOrderElementsOf(project1.getAcceptedInvestments());
    }

    @Test
    public void shouldFindProjectsByStatusAndProjectOwnerId() {
        // given
        CrowdfundingProject.Status targetStatus = getRandomStatus();
        ProjectOwner projectOwner = getRandomProjectOwnerFromExisting();
        List<CrowdfundingProject> expected = IntStream.range(0, 30)
                                                      .mapToObj(_ -> buildRandomProject(projectOwner).toBuilder()
                                                                                                      .status(targetStatus)
                                                                                                      .build())
                                                      .map(repositoryAdapter::save)
                                                      .toList();
        IntStream.range(0, 20)
                 .mapToObj(_ -> buildRandomProject(getRandomProjectOwnerFromExisting(projectOwner)).toBuilder()
                                                                                       .status(getRandomStatus(targetStatus))
                                                                                       .build())
                 .map(repositoryAdapter::save)
                 .forEach(repositoryAdapter::save);

        // when
        List<CrowdfundingProject> pendingReviewProjects = repositoryAdapter.findByOwnerIdOrderByAsc(projectOwner.getId(), null).toList();

        // then
        assertThat(pendingReviewProjects.size()).isEqualTo(expected.size());
        assertThat(pendingReviewProjects).containsExactly(expected.toArray(new CrowdfundingProject[0]));
    }

}
