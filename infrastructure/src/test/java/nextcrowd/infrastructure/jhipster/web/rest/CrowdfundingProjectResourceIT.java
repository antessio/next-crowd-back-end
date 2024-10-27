package nextcrowd.infrastructure.jhipster.web.rest;

import static nextcrowd.infrastructure.jhipster.domain.CrowdfundingProjectAsserts.*;
import static nextcrowd.infrastructure.jhipster.web.rest.TestUtil.createUpdateProxyForBean;
import static nextcrowd.infrastructure.jhipster.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import nextcrowd.infrastructure.jhipster.IntegrationTest;
import nextcrowd.infrastructure.jhipster.domain.CrowdfundingProject;
import nextcrowd.infrastructure.jhipster.domain.enumeration.Status;
import nextcrowd.infrastructure.jhipster.repository.CrowdfundingProjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CrowdfundingProjectResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CrowdfundingProjectResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.SUBMITTED;
    private static final Status UPDATED_STATUS = Status.APPROVED;

    private static final BigDecimal DEFAULT_REQUESTED_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_REQUESTED_AMOUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_COLLECTED_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_COLLECTED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final Instant DEFAULT_PROJECT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PROJECT_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_PROJECT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PROJECT_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_NUMBER_OF_BACKERS = 1;
    private static final Integer UPDATED_NUMBER_OF_BACKERS = 2;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_LONG_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_LONG_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_PROJECT_VIDEO_URL = "AAAAAAAAAA";
    private static final String UPDATED_PROJECT_VIDEO_URL = "BBBBBBBBBB";

    private static final Integer DEFAULT_RISK = 1;
    private static final Integer UPDATED_RISK = 2;

    private static final BigDecimal DEFAULT_EXPECTED_PROFIT = new BigDecimal(1);
    private static final BigDecimal UPDATED_EXPECTED_PROFIT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_MINIMUM_INVESTMENT = new BigDecimal(1);
    private static final BigDecimal UPDATED_MINIMUM_INVESTMENT = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/crowdfunding-projects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CrowdfundingProjectRepository crowdfundingProjectRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCrowdfundingProjectMockMvc;

    private CrowdfundingProject crowdfundingProject;

    private CrowdfundingProject insertedCrowdfundingProject;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CrowdfundingProject createEntity() {
        return new CrowdfundingProject()
            .title(DEFAULT_TITLE)
            .status(DEFAULT_STATUS)
            .requestedAmount(DEFAULT_REQUESTED_AMOUNT)
            .collectedAmount(DEFAULT_COLLECTED_AMOUNT)
            .currency(DEFAULT_CURRENCY)
            .imageUrl(DEFAULT_IMAGE_URL)
            .projectStartDate(DEFAULT_PROJECT_START_DATE)
            .projectEndDate(DEFAULT_PROJECT_END_DATE)
            .numberOfBackers(DEFAULT_NUMBER_OF_BACKERS)
            .description(DEFAULT_DESCRIPTION)
            .longDescription(DEFAULT_LONG_DESCRIPTION)
            .projectVideoUrl(DEFAULT_PROJECT_VIDEO_URL)
            .risk(DEFAULT_RISK)
            .expectedProfit(DEFAULT_EXPECTED_PROFIT)
            .minimumInvestment(DEFAULT_MINIMUM_INVESTMENT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CrowdfundingProject createUpdatedEntity() {
        return new CrowdfundingProject()
            .title(UPDATED_TITLE)
            .status(UPDATED_STATUS)
            .requestedAmount(UPDATED_REQUESTED_AMOUNT)
            .collectedAmount(UPDATED_COLLECTED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .imageUrl(UPDATED_IMAGE_URL)
            .projectStartDate(UPDATED_PROJECT_START_DATE)
            .projectEndDate(UPDATED_PROJECT_END_DATE)
            .numberOfBackers(UPDATED_NUMBER_OF_BACKERS)
            .description(UPDATED_DESCRIPTION)
            .longDescription(UPDATED_LONG_DESCRIPTION)
            .projectVideoUrl(UPDATED_PROJECT_VIDEO_URL)
            .risk(UPDATED_RISK)
            .expectedProfit(UPDATED_EXPECTED_PROFIT)
            .minimumInvestment(UPDATED_MINIMUM_INVESTMENT);
    }

    @BeforeEach
    public void initTest() {
        crowdfundingProject = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCrowdfundingProject != null) {
            crowdfundingProjectRepository.delete(insertedCrowdfundingProject);
            insertedCrowdfundingProject = null;
        }
    }

    @Test
    @Transactional
    void createCrowdfundingProject() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CrowdfundingProject
        var returnedCrowdfundingProject = om.readValue(
            restCrowdfundingProjectMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(crowdfundingProject)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CrowdfundingProject.class
        );

        // Validate the CrowdfundingProject in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCrowdfundingProjectUpdatableFieldsEquals(
            returnedCrowdfundingProject,
            getPersistedCrowdfundingProject(returnedCrowdfundingProject)
        );

        insertedCrowdfundingProject = returnedCrowdfundingProject;
    }

    @Test
    @Transactional
    void createCrowdfundingProjectWithExistingId() throws Exception {
        // Create the CrowdfundingProject with an existing ID
        crowdfundingProject.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCrowdfundingProjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(crowdfundingProject)))
            .andExpect(status().isBadRequest());

        // Validate the CrowdfundingProject in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        crowdfundingProject.setTitle(null);

        // Create the CrowdfundingProject, which fails.

        restCrowdfundingProjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(crowdfundingProject)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        crowdfundingProject.setStatus(null);

        // Create the CrowdfundingProject, which fails.

        restCrowdfundingProjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(crowdfundingProject)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRequestedAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        crowdfundingProject.setRequestedAmount(null);

        // Create the CrowdfundingProject, which fails.

        restCrowdfundingProjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(crowdfundingProject)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCurrencyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        crowdfundingProject.setCurrency(null);

        // Create the CrowdfundingProject, which fails.

        restCrowdfundingProjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(crowdfundingProject)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCrowdfundingProjects() throws Exception {
        // Initialize the database
        insertedCrowdfundingProject = crowdfundingProjectRepository.saveAndFlush(crowdfundingProject);

        // Get all the crowdfundingProjectList
        restCrowdfundingProjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(crowdfundingProject.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].requestedAmount").value(hasItem(sameNumber(DEFAULT_REQUESTED_AMOUNT))))
            .andExpect(jsonPath("$.[*].collectedAmount").value(hasItem(sameNumber(DEFAULT_COLLECTED_AMOUNT))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].projectStartDate").value(hasItem(DEFAULT_PROJECT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].projectEndDate").value(hasItem(DEFAULT_PROJECT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].numberOfBackers").value(hasItem(DEFAULT_NUMBER_OF_BACKERS)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].longDescription").value(hasItem(DEFAULT_LONG_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].projectVideoUrl").value(hasItem(DEFAULT_PROJECT_VIDEO_URL)))
            .andExpect(jsonPath("$.[*].risk").value(hasItem(DEFAULT_RISK)))
            .andExpect(jsonPath("$.[*].expectedProfit").value(hasItem(sameNumber(DEFAULT_EXPECTED_PROFIT))))
            .andExpect(jsonPath("$.[*].minimumInvestment").value(hasItem(sameNumber(DEFAULT_MINIMUM_INVESTMENT))));
    }

    @Test
    @Transactional
    void getCrowdfundingProject() throws Exception {
        // Initialize the database
        insertedCrowdfundingProject = crowdfundingProjectRepository.saveAndFlush(crowdfundingProject);

        // Get the crowdfundingProject
        restCrowdfundingProjectMockMvc
            .perform(get(ENTITY_API_URL_ID, crowdfundingProject.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(crowdfundingProject.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.requestedAmount").value(sameNumber(DEFAULT_REQUESTED_AMOUNT)))
            .andExpect(jsonPath("$.collectedAmount").value(sameNumber(DEFAULT_COLLECTED_AMOUNT)))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL))
            .andExpect(jsonPath("$.projectStartDate").value(DEFAULT_PROJECT_START_DATE.toString()))
            .andExpect(jsonPath("$.projectEndDate").value(DEFAULT_PROJECT_END_DATE.toString()))
            .andExpect(jsonPath("$.numberOfBackers").value(DEFAULT_NUMBER_OF_BACKERS))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.longDescription").value(DEFAULT_LONG_DESCRIPTION))
            .andExpect(jsonPath("$.projectVideoUrl").value(DEFAULT_PROJECT_VIDEO_URL))
            .andExpect(jsonPath("$.risk").value(DEFAULT_RISK))
            .andExpect(jsonPath("$.expectedProfit").value(sameNumber(DEFAULT_EXPECTED_PROFIT)))
            .andExpect(jsonPath("$.minimumInvestment").value(sameNumber(DEFAULT_MINIMUM_INVESTMENT)));
    }

    @Test
    @Transactional
    void getNonExistingCrowdfundingProject() throws Exception {
        // Get the crowdfundingProject
        restCrowdfundingProjectMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCrowdfundingProject() throws Exception {
        // Initialize the database
        insertedCrowdfundingProject = crowdfundingProjectRepository.saveAndFlush(crowdfundingProject);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the crowdfundingProject
        CrowdfundingProject updatedCrowdfundingProject = crowdfundingProjectRepository.findById(crowdfundingProject.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCrowdfundingProject are not directly saved in db
        em.detach(updatedCrowdfundingProject);
        updatedCrowdfundingProject
            .title(UPDATED_TITLE)
            .status(UPDATED_STATUS)
            .requestedAmount(UPDATED_REQUESTED_AMOUNT)
            .collectedAmount(UPDATED_COLLECTED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .imageUrl(UPDATED_IMAGE_URL)
            .projectStartDate(UPDATED_PROJECT_START_DATE)
            .projectEndDate(UPDATED_PROJECT_END_DATE)
            .numberOfBackers(UPDATED_NUMBER_OF_BACKERS)
            .description(UPDATED_DESCRIPTION)
            .longDescription(UPDATED_LONG_DESCRIPTION)
            .projectVideoUrl(UPDATED_PROJECT_VIDEO_URL)
            .risk(UPDATED_RISK)
            .expectedProfit(UPDATED_EXPECTED_PROFIT)
            .minimumInvestment(UPDATED_MINIMUM_INVESTMENT);

        restCrowdfundingProjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCrowdfundingProject.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCrowdfundingProject))
            )
            .andExpect(status().isOk());

        // Validate the CrowdfundingProject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCrowdfundingProjectToMatchAllProperties(updatedCrowdfundingProject);
    }

    @Test
    @Transactional
    void putNonExistingCrowdfundingProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        crowdfundingProject.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCrowdfundingProjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, crowdfundingProject.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(crowdfundingProject))
            )
            .andExpect(status().isBadRequest());

        // Validate the CrowdfundingProject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCrowdfundingProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        crowdfundingProject.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCrowdfundingProjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(crowdfundingProject))
            )
            .andExpect(status().isBadRequest());

        // Validate the CrowdfundingProject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCrowdfundingProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        crowdfundingProject.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCrowdfundingProjectMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(crowdfundingProject)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CrowdfundingProject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCrowdfundingProjectWithPatch() throws Exception {
        // Initialize the database
        insertedCrowdfundingProject = crowdfundingProjectRepository.saveAndFlush(crowdfundingProject);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the crowdfundingProject using partial update
        CrowdfundingProject partialUpdatedCrowdfundingProject = new CrowdfundingProject();
        partialUpdatedCrowdfundingProject.setId(crowdfundingProject.getId());

        partialUpdatedCrowdfundingProject
            .title(UPDATED_TITLE)
            .requestedAmount(UPDATED_REQUESTED_AMOUNT)
            .numberOfBackers(UPDATED_NUMBER_OF_BACKERS)
            .description(UPDATED_DESCRIPTION)
            .longDescription(UPDATED_LONG_DESCRIPTION)
            .projectVideoUrl(UPDATED_PROJECT_VIDEO_URL);

        restCrowdfundingProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCrowdfundingProject.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCrowdfundingProject))
            )
            .andExpect(status().isOk());

        // Validate the CrowdfundingProject in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCrowdfundingProjectUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCrowdfundingProject, crowdfundingProject),
            getPersistedCrowdfundingProject(crowdfundingProject)
        );
    }

    @Test
    @Transactional
    void fullUpdateCrowdfundingProjectWithPatch() throws Exception {
        // Initialize the database
        insertedCrowdfundingProject = crowdfundingProjectRepository.saveAndFlush(crowdfundingProject);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the crowdfundingProject using partial update
        CrowdfundingProject partialUpdatedCrowdfundingProject = new CrowdfundingProject();
        partialUpdatedCrowdfundingProject.setId(crowdfundingProject.getId());

        partialUpdatedCrowdfundingProject
            .title(UPDATED_TITLE)
            .status(UPDATED_STATUS)
            .requestedAmount(UPDATED_REQUESTED_AMOUNT)
            .collectedAmount(UPDATED_COLLECTED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .imageUrl(UPDATED_IMAGE_URL)
            .projectStartDate(UPDATED_PROJECT_START_DATE)
            .projectEndDate(UPDATED_PROJECT_END_DATE)
            .numberOfBackers(UPDATED_NUMBER_OF_BACKERS)
            .description(UPDATED_DESCRIPTION)
            .longDescription(UPDATED_LONG_DESCRIPTION)
            .projectVideoUrl(UPDATED_PROJECT_VIDEO_URL)
            .risk(UPDATED_RISK)
            .expectedProfit(UPDATED_EXPECTED_PROFIT)
            .minimumInvestment(UPDATED_MINIMUM_INVESTMENT);

        restCrowdfundingProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCrowdfundingProject.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCrowdfundingProject))
            )
            .andExpect(status().isOk());

        // Validate the CrowdfundingProject in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCrowdfundingProjectUpdatableFieldsEquals(
            partialUpdatedCrowdfundingProject,
            getPersistedCrowdfundingProject(partialUpdatedCrowdfundingProject)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCrowdfundingProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        crowdfundingProject.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCrowdfundingProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, crowdfundingProject.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(crowdfundingProject))
            )
            .andExpect(status().isBadRequest());

        // Validate the CrowdfundingProject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCrowdfundingProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        crowdfundingProject.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCrowdfundingProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(crowdfundingProject))
            )
            .andExpect(status().isBadRequest());

        // Validate the CrowdfundingProject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCrowdfundingProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        crowdfundingProject.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCrowdfundingProjectMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(crowdfundingProject)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CrowdfundingProject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCrowdfundingProject() throws Exception {
        // Initialize the database
        insertedCrowdfundingProject = crowdfundingProjectRepository.saveAndFlush(crowdfundingProject);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the crowdfundingProject
        restCrowdfundingProjectMockMvc
            .perform(delete(ENTITY_API_URL_ID, crowdfundingProject.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return crowdfundingProjectRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected CrowdfundingProject getPersistedCrowdfundingProject(CrowdfundingProject crowdfundingProject) {
        return crowdfundingProjectRepository.findById(crowdfundingProject.getId()).orElseThrow();
    }

    protected void assertPersistedCrowdfundingProjectToMatchAllProperties(CrowdfundingProject expectedCrowdfundingProject) {
        assertCrowdfundingProjectAllPropertiesEquals(
            expectedCrowdfundingProject,
            getPersistedCrowdfundingProject(expectedCrowdfundingProject)
        );
    }

    protected void assertPersistedCrowdfundingProjectToMatchUpdatableProperties(CrowdfundingProject expectedCrowdfundingProject) {
        assertCrowdfundingProjectAllUpdatablePropertiesEquals(
            expectedCrowdfundingProject,
            getPersistedCrowdfundingProject(expectedCrowdfundingProject)
        );
    }
}
