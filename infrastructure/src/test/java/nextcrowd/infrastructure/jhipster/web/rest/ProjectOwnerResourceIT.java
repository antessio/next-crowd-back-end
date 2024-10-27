package nextcrowd.infrastructure.jhipster.web.rest;

import static nextcrowd.infrastructure.jhipster.domain.ProjectOwnerAsserts.*;
import static nextcrowd.infrastructure.jhipster.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import nextcrowd.infrastructure.jhipster.IntegrationTest;
import nextcrowd.infrastructure.jhipster.domain.ProjectOwner;
import nextcrowd.infrastructure.jhipster.repository.ProjectOwnerRepository;
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
 * Integration tests for the {@link ProjectOwnerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProjectOwnerResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/project-owners";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProjectOwnerRepository projectOwnerRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProjectOwnerMockMvc;

    private ProjectOwner projectOwner;

    private ProjectOwner insertedProjectOwner;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectOwner createEntity() {
        return new ProjectOwner().name(DEFAULT_NAME).imageUrl(DEFAULT_IMAGE_URL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectOwner createUpdatedEntity() {
        return new ProjectOwner().name(UPDATED_NAME).imageUrl(UPDATED_IMAGE_URL);
    }

    @BeforeEach
    public void initTest() {
        projectOwner = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedProjectOwner != null) {
            projectOwnerRepository.delete(insertedProjectOwner);
            insertedProjectOwner = null;
        }
    }

    @Test
    @Transactional
    void createProjectOwner() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProjectOwner
        var returnedProjectOwner = om.readValue(
            restProjectOwnerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectOwner)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProjectOwner.class
        );

        // Validate the ProjectOwner in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertProjectOwnerUpdatableFieldsEquals(returnedProjectOwner, getPersistedProjectOwner(returnedProjectOwner));

        insertedProjectOwner = returnedProjectOwner;
    }

    @Test
    @Transactional
    void createProjectOwnerWithExistingId() throws Exception {
        // Create the ProjectOwner with an existing ID
        projectOwner.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectOwnerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectOwner)))
            .andExpect(status().isBadRequest());

        // Validate the ProjectOwner in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        projectOwner.setName(null);

        // Create the ProjectOwner, which fails.

        restProjectOwnerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectOwner)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProjectOwners() throws Exception {
        // Initialize the database
        insertedProjectOwner = projectOwnerRepository.saveAndFlush(projectOwner);

        // Get all the projectOwnerList
        restProjectOwnerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectOwner.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)));
    }

    @Test
    @Transactional
    void getProjectOwner() throws Exception {
        // Initialize the database
        insertedProjectOwner = projectOwnerRepository.saveAndFlush(projectOwner);

        // Get the projectOwner
        restProjectOwnerMockMvc
            .perform(get(ENTITY_API_URL_ID, projectOwner.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(projectOwner.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL));
    }

    @Test
    @Transactional
    void getNonExistingProjectOwner() throws Exception {
        // Get the projectOwner
        restProjectOwnerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProjectOwner() throws Exception {
        // Initialize the database
        insertedProjectOwner = projectOwnerRepository.saveAndFlush(projectOwner);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the projectOwner
        ProjectOwner updatedProjectOwner = projectOwnerRepository.findById(projectOwner.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProjectOwner are not directly saved in db
        em.detach(updatedProjectOwner);
        updatedProjectOwner.name(UPDATED_NAME).imageUrl(UPDATED_IMAGE_URL);

        restProjectOwnerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProjectOwner.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedProjectOwner))
            )
            .andExpect(status().isOk());

        // Validate the ProjectOwner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProjectOwnerToMatchAllProperties(updatedProjectOwner);
    }

    @Test
    @Transactional
    void putNonExistingProjectOwner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectOwner.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectOwnerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectOwner.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(projectOwner))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectOwner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProjectOwner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectOwner.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectOwnerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(projectOwner))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectOwner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProjectOwner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectOwner.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectOwnerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectOwner)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectOwner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProjectOwnerWithPatch() throws Exception {
        // Initialize the database
        insertedProjectOwner = projectOwnerRepository.saveAndFlush(projectOwner);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the projectOwner using partial update
        ProjectOwner partialUpdatedProjectOwner = new ProjectOwner();
        partialUpdatedProjectOwner.setId(projectOwner.getId());

        partialUpdatedProjectOwner.name(UPDATED_NAME);

        restProjectOwnerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectOwner.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProjectOwner))
            )
            .andExpect(status().isOk());

        // Validate the ProjectOwner in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectOwnerUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProjectOwner, projectOwner),
            getPersistedProjectOwner(projectOwner)
        );
    }

    @Test
    @Transactional
    void fullUpdateProjectOwnerWithPatch() throws Exception {
        // Initialize the database
        insertedProjectOwner = projectOwnerRepository.saveAndFlush(projectOwner);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the projectOwner using partial update
        ProjectOwner partialUpdatedProjectOwner = new ProjectOwner();
        partialUpdatedProjectOwner.setId(projectOwner.getId());

        partialUpdatedProjectOwner.name(UPDATED_NAME).imageUrl(UPDATED_IMAGE_URL);

        restProjectOwnerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectOwner.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProjectOwner))
            )
            .andExpect(status().isOk());

        // Validate the ProjectOwner in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectOwnerUpdatableFieldsEquals(partialUpdatedProjectOwner, getPersistedProjectOwner(partialUpdatedProjectOwner));
    }

    @Test
    @Transactional
    void patchNonExistingProjectOwner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectOwner.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectOwnerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, projectOwner.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(projectOwner))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectOwner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProjectOwner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectOwner.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectOwnerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(projectOwner))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectOwner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProjectOwner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectOwner.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectOwnerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(projectOwner)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectOwner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProjectOwner() throws Exception {
        // Initialize the database
        insertedProjectOwner = projectOwnerRepository.saveAndFlush(projectOwner);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the projectOwner
        restProjectOwnerMockMvc
            .perform(delete(ENTITY_API_URL_ID, projectOwner.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return projectOwnerRepository.count();
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

    protected ProjectOwner getPersistedProjectOwner(ProjectOwner projectOwner) {
        return projectOwnerRepository.findById(projectOwner.getId()).orElseThrow();
    }

    protected void assertPersistedProjectOwnerToMatchAllProperties(ProjectOwner expectedProjectOwner) {
        assertProjectOwnerAllPropertiesEquals(expectedProjectOwner, getPersistedProjectOwner(expectedProjectOwner));
    }

    protected void assertPersistedProjectOwnerToMatchUpdatableProperties(ProjectOwner expectedProjectOwner) {
        assertProjectOwnerAllUpdatablePropertiesEquals(expectedProjectOwner, getPersistedProjectOwner(expectedProjectOwner));
    }
}
