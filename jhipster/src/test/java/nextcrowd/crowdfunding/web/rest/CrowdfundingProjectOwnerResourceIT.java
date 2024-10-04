package nextcrowd.crowdfunding.web.rest;

import static nextcrowd.crowdfunding.domain.CrowdfundingProjectOwnerAsserts.*;
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
import nextcrowd.crowdfunding.domain.CrowdfundingProjectOwner;
import nextcrowd.crowdfunding.repository.CrowdfundingProjectOwnerRepository;
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
 * Integration tests for the {@link CrowdfundingProjectOwnerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CrowdfundingProjectOwnerResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/crowdfunding-project-owners";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CrowdfundingProjectOwnerRepository crowdfundingProjectOwnerRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCrowdfundingProjectOwnerMockMvc;

    private CrowdfundingProjectOwner crowdfundingProjectOwner;

    private CrowdfundingProjectOwner insertedCrowdfundingProjectOwner;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CrowdfundingProjectOwner createEntity() {
        return new CrowdfundingProjectOwner().name(DEFAULT_NAME).imageUrl(DEFAULT_IMAGE_URL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CrowdfundingProjectOwner createUpdatedEntity() {
        return new CrowdfundingProjectOwner().name(UPDATED_NAME).imageUrl(UPDATED_IMAGE_URL);
    }

    @BeforeEach
    public void initTest() {
        crowdfundingProjectOwner = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCrowdfundingProjectOwner != null) {
            crowdfundingProjectOwnerRepository.delete(insertedCrowdfundingProjectOwner);
            insertedCrowdfundingProjectOwner = null;
        }
    }

    @Test
    @Transactional
    void createCrowdfundingProjectOwner() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CrowdfundingProjectOwner
        var returnedCrowdfundingProjectOwner = om.readValue(
            restCrowdfundingProjectOwnerMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(crowdfundingProjectOwner))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CrowdfundingProjectOwner.class
        );

        // Validate the CrowdfundingProjectOwner in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCrowdfundingProjectOwnerUpdatableFieldsEquals(
            returnedCrowdfundingProjectOwner,
            getPersistedCrowdfundingProjectOwner(returnedCrowdfundingProjectOwner)
        );

        insertedCrowdfundingProjectOwner = returnedCrowdfundingProjectOwner;
    }

    @Test
    @Transactional
    void createCrowdfundingProjectOwnerWithExistingId() throws Exception {
        // Create the CrowdfundingProjectOwner with an existing ID
        crowdfundingProjectOwner.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCrowdfundingProjectOwnerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(crowdfundingProjectOwner)))
            .andExpect(status().isBadRequest());

        // Validate the CrowdfundingProjectOwner in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        crowdfundingProjectOwner.setName(null);

        // Create the CrowdfundingProjectOwner, which fails.

        restCrowdfundingProjectOwnerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(crowdfundingProjectOwner)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCrowdfundingProjectOwners() throws Exception {
        // Initialize the database
        insertedCrowdfundingProjectOwner = crowdfundingProjectOwnerRepository.saveAndFlush(crowdfundingProjectOwner);

        // Get all the crowdfundingProjectOwnerList
        restCrowdfundingProjectOwnerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(crowdfundingProjectOwner.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)));
    }

    @Test
    @Transactional
    void getCrowdfundingProjectOwner() throws Exception {
        // Initialize the database
        insertedCrowdfundingProjectOwner = crowdfundingProjectOwnerRepository.saveAndFlush(crowdfundingProjectOwner);

        // Get the crowdfundingProjectOwner
        restCrowdfundingProjectOwnerMockMvc
            .perform(get(ENTITY_API_URL_ID, crowdfundingProjectOwner.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(crowdfundingProjectOwner.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL));
    }

    @Test
    @Transactional
    void getNonExistingCrowdfundingProjectOwner() throws Exception {
        // Get the crowdfundingProjectOwner
        restCrowdfundingProjectOwnerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCrowdfundingProjectOwner() throws Exception {
        // Initialize the database
        insertedCrowdfundingProjectOwner = crowdfundingProjectOwnerRepository.saveAndFlush(crowdfundingProjectOwner);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the crowdfundingProjectOwner
        CrowdfundingProjectOwner updatedCrowdfundingProjectOwner = crowdfundingProjectOwnerRepository
            .findById(crowdfundingProjectOwner.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedCrowdfundingProjectOwner are not directly saved in db
        em.detach(updatedCrowdfundingProjectOwner);
        updatedCrowdfundingProjectOwner.name(UPDATED_NAME).imageUrl(UPDATED_IMAGE_URL);

        restCrowdfundingProjectOwnerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCrowdfundingProjectOwner.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCrowdfundingProjectOwner))
            )
            .andExpect(status().isOk());

        // Validate the CrowdfundingProjectOwner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCrowdfundingProjectOwnerToMatchAllProperties(updatedCrowdfundingProjectOwner);
    }

    @Test
    @Transactional
    void putNonExistingCrowdfundingProjectOwner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        crowdfundingProjectOwner.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCrowdfundingProjectOwnerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, crowdfundingProjectOwner.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(crowdfundingProjectOwner))
            )
            .andExpect(status().isBadRequest());

        // Validate the CrowdfundingProjectOwner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCrowdfundingProjectOwner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        crowdfundingProjectOwner.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCrowdfundingProjectOwnerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(crowdfundingProjectOwner))
            )
            .andExpect(status().isBadRequest());

        // Validate the CrowdfundingProjectOwner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCrowdfundingProjectOwner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        crowdfundingProjectOwner.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCrowdfundingProjectOwnerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(crowdfundingProjectOwner)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CrowdfundingProjectOwner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCrowdfundingProjectOwnerWithPatch() throws Exception {
        // Initialize the database
        insertedCrowdfundingProjectOwner = crowdfundingProjectOwnerRepository.saveAndFlush(crowdfundingProjectOwner);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the crowdfundingProjectOwner using partial update
        CrowdfundingProjectOwner partialUpdatedCrowdfundingProjectOwner = new CrowdfundingProjectOwner();
        partialUpdatedCrowdfundingProjectOwner.setId(crowdfundingProjectOwner.getId());

        partialUpdatedCrowdfundingProjectOwner.name(UPDATED_NAME).imageUrl(UPDATED_IMAGE_URL);

        restCrowdfundingProjectOwnerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCrowdfundingProjectOwner.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCrowdfundingProjectOwner))
            )
            .andExpect(status().isOk());

        // Validate the CrowdfundingProjectOwner in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCrowdfundingProjectOwnerUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCrowdfundingProjectOwner, crowdfundingProjectOwner),
            getPersistedCrowdfundingProjectOwner(crowdfundingProjectOwner)
        );
    }

    @Test
    @Transactional
    void fullUpdateCrowdfundingProjectOwnerWithPatch() throws Exception {
        // Initialize the database
        insertedCrowdfundingProjectOwner = crowdfundingProjectOwnerRepository.saveAndFlush(crowdfundingProjectOwner);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the crowdfundingProjectOwner using partial update
        CrowdfundingProjectOwner partialUpdatedCrowdfundingProjectOwner = new CrowdfundingProjectOwner();
        partialUpdatedCrowdfundingProjectOwner.setId(crowdfundingProjectOwner.getId());

        partialUpdatedCrowdfundingProjectOwner.name(UPDATED_NAME).imageUrl(UPDATED_IMAGE_URL);

        restCrowdfundingProjectOwnerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCrowdfundingProjectOwner.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCrowdfundingProjectOwner))
            )
            .andExpect(status().isOk());

        // Validate the CrowdfundingProjectOwner in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCrowdfundingProjectOwnerUpdatableFieldsEquals(
            partialUpdatedCrowdfundingProjectOwner,
            getPersistedCrowdfundingProjectOwner(partialUpdatedCrowdfundingProjectOwner)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCrowdfundingProjectOwner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        crowdfundingProjectOwner.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCrowdfundingProjectOwnerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, crowdfundingProjectOwner.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(crowdfundingProjectOwner))
            )
            .andExpect(status().isBadRequest());

        // Validate the CrowdfundingProjectOwner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCrowdfundingProjectOwner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        crowdfundingProjectOwner.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCrowdfundingProjectOwnerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(crowdfundingProjectOwner))
            )
            .andExpect(status().isBadRequest());

        // Validate the CrowdfundingProjectOwner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCrowdfundingProjectOwner() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        crowdfundingProjectOwner.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCrowdfundingProjectOwnerMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(crowdfundingProjectOwner))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CrowdfundingProjectOwner in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCrowdfundingProjectOwner() throws Exception {
        // Initialize the database
        insertedCrowdfundingProjectOwner = crowdfundingProjectOwnerRepository.saveAndFlush(crowdfundingProjectOwner);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the crowdfundingProjectOwner
        restCrowdfundingProjectOwnerMockMvc
            .perform(delete(ENTITY_API_URL_ID, crowdfundingProjectOwner.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return crowdfundingProjectOwnerRepository.count();
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

    protected CrowdfundingProjectOwner getPersistedCrowdfundingProjectOwner(CrowdfundingProjectOwner crowdfundingProjectOwner) {
        return crowdfundingProjectOwnerRepository.findById(crowdfundingProjectOwner.getId()).orElseThrow();
    }

    protected void assertPersistedCrowdfundingProjectOwnerToMatchAllProperties(CrowdfundingProjectOwner expectedCrowdfundingProjectOwner) {
        assertCrowdfundingProjectOwnerAllPropertiesEquals(
            expectedCrowdfundingProjectOwner,
            getPersistedCrowdfundingProjectOwner(expectedCrowdfundingProjectOwner)
        );
    }

    protected void assertPersistedCrowdfundingProjectOwnerToMatchUpdatableProperties(
        CrowdfundingProjectOwner expectedCrowdfundingProjectOwner
    ) {
        assertCrowdfundingProjectOwnerAllUpdatablePropertiesEquals(
            expectedCrowdfundingProjectOwner,
            getPersistedCrowdfundingProjectOwner(expectedCrowdfundingProjectOwner)
        );
    }
}
