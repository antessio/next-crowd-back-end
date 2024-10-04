package nextcrowd.crowdfunding.web.rest;

import static nextcrowd.crowdfunding.domain.ProjectRewardAsserts.*;
import static nextcrowd.crowdfunding.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import nextcrowd.crowdfunding.IntegrationTest;
import nextcrowd.crowdfunding.domain.ProjectReward;
import nextcrowd.crowdfunding.repository.ProjectRewardRepository;
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
 * Integration tests for the {@link ProjectRewardResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProjectRewardResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/project-rewards";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProjectRewardRepository projectRewardRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProjectRewardMockMvc;

    private ProjectReward projectReward;

    private ProjectReward insertedProjectReward;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectReward createEntity() {
        return new ProjectReward().name(DEFAULT_NAME).imageUrl(DEFAULT_IMAGE_URL).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectReward createUpdatedEntity() {
        return new ProjectReward().name(UPDATED_NAME).imageUrl(UPDATED_IMAGE_URL).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    public void initTest() {
        projectReward = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedProjectReward != null) {
            projectRewardRepository.delete(insertedProjectReward);
            insertedProjectReward = null;
        }
    }

    @Test
    @Transactional
    void createProjectReward() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProjectReward
        var returnedProjectReward = om.readValue(
            restProjectRewardMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectReward)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProjectReward.class
        );

        // Validate the ProjectReward in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertProjectRewardUpdatableFieldsEquals(returnedProjectReward, getPersistedProjectReward(returnedProjectReward));

        insertedProjectReward = returnedProjectReward;
    }

    @Test
    @Transactional
    void createProjectRewardWithExistingId() throws Exception {
        // Create the ProjectReward with an existing ID
        projectReward.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectRewardMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectReward)))
            .andExpect(status().isBadRequest());

        // Validate the ProjectReward in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        projectReward.setName(null);

        // Create the ProjectReward, which fails.

        restProjectRewardMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectReward)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProjectRewards() throws Exception {
        // Initialize the database
        insertedProjectReward = projectRewardRepository.saveAndFlush(projectReward);

        // Get all the projectRewardList
        restProjectRewardMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectReward.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getProjectReward() throws Exception {
        // Initialize the database
        insertedProjectReward = projectRewardRepository.saveAndFlush(projectReward);

        // Get the projectReward
        restProjectRewardMockMvc
            .perform(get(ENTITY_API_URL_ID, projectReward.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(projectReward.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingProjectReward() throws Exception {
        // Get the projectReward
        restProjectRewardMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProjectReward() throws Exception {
        // Initialize the database
        insertedProjectReward = projectRewardRepository.saveAndFlush(projectReward);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the projectReward
        ProjectReward updatedProjectReward = projectRewardRepository.findById(projectReward.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProjectReward are not directly saved in db
        em.detach(updatedProjectReward);
        updatedProjectReward.name(UPDATED_NAME).imageUrl(UPDATED_IMAGE_URL).description(UPDATED_DESCRIPTION);

        restProjectRewardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProjectReward.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedProjectReward))
            )
            .andExpect(status().isOk());

        // Validate the ProjectReward in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProjectRewardToMatchAllProperties(updatedProjectReward);
    }

    @Test
    @Transactional
    void putNonExistingProjectReward() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectReward.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectRewardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectReward.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(projectReward))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectReward in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProjectReward() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectReward.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectRewardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(projectReward))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectReward in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProjectReward() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectReward.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectRewardMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectReward)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectReward in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProjectRewardWithPatch() throws Exception {
        // Initialize the database
        insertedProjectReward = projectRewardRepository.saveAndFlush(projectReward);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the projectReward using partial update
        ProjectReward partialUpdatedProjectReward = new ProjectReward();
        partialUpdatedProjectReward.setId(projectReward.getId());

        partialUpdatedProjectReward.imageUrl(UPDATED_IMAGE_URL).description(UPDATED_DESCRIPTION);

        restProjectRewardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectReward.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProjectReward))
            )
            .andExpect(status().isOk());

        // Validate the ProjectReward in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectRewardUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProjectReward, projectReward),
            getPersistedProjectReward(projectReward)
        );
    }

    @Test
    @Transactional
    void fullUpdateProjectRewardWithPatch() throws Exception {
        // Initialize the database
        insertedProjectReward = projectRewardRepository.saveAndFlush(projectReward);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the projectReward using partial update
        ProjectReward partialUpdatedProjectReward = new ProjectReward();
        partialUpdatedProjectReward.setId(projectReward.getId());

        partialUpdatedProjectReward.name(UPDATED_NAME).imageUrl(UPDATED_IMAGE_URL).description(UPDATED_DESCRIPTION);

        restProjectRewardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectReward.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProjectReward))
            )
            .andExpect(status().isOk());

        // Validate the ProjectReward in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectRewardUpdatableFieldsEquals(partialUpdatedProjectReward, getPersistedProjectReward(partialUpdatedProjectReward));
    }

    @Test
    @Transactional
    void patchNonExistingProjectReward() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectReward.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectRewardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, projectReward.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(projectReward))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectReward in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProjectReward() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectReward.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectRewardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(projectReward))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectReward in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProjectReward() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectReward.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectRewardMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(projectReward)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectReward in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProjectReward() throws Exception {
        // Initialize the database
        insertedProjectReward = projectRewardRepository.saveAndFlush(projectReward);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the projectReward
        restProjectRewardMockMvc
            .perform(delete(ENTITY_API_URL_ID, projectReward.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return projectRewardRepository.count();
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

    protected ProjectReward getPersistedProjectReward(ProjectReward projectReward) {
        return projectRewardRepository.findById(projectReward.getId()).orElseThrow();
    }

    protected void assertPersistedProjectRewardToMatchAllProperties(ProjectReward expectedProjectReward) {
        assertProjectRewardAllPropertiesEquals(expectedProjectReward, getPersistedProjectReward(expectedProjectReward));
    }

    protected void assertPersistedProjectRewardToMatchUpdatableProperties(ProjectReward expectedProjectReward) {
        assertProjectRewardAllUpdatablePropertiesEquals(expectedProjectReward, getPersistedProjectReward(expectedProjectReward));
    }
}
