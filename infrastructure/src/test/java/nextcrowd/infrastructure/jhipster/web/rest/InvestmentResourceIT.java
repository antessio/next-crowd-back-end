package nextcrowd.infrastructure.jhipster.web.rest;

import static nextcrowd.infrastructure.jhipster.domain.InvestmentAsserts.*;
import static nextcrowd.infrastructure.jhipster.web.rest.TestUtil.createUpdateProxyForBean;
import static nextcrowd.infrastructure.jhipster.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import nextcrowd.infrastructure.jhipster.IntegrationTest;
import nextcrowd.infrastructure.jhipster.domain.Investment;
import nextcrowd.infrastructure.jhipster.repository.InvestmentRepository;
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
 * Integration tests for the {@link InvestmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InvestmentResourceIT {

    private static final String DEFAULT_BAKER_ID = "AAAAAAAAAA";
    private static final String UPDATED_BAKER_ID = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_MONEY_TRANSFER_ID = "AAAAAAAAAA";
    private static final String UPDATED_MONEY_TRANSFER_ID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/investments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInvestmentMockMvc;

    private Investment investment;

    private Investment insertedInvestment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Investment createEntity() {
        return new Investment().bakerId(DEFAULT_BAKER_ID).amount(DEFAULT_AMOUNT).moneyTransferId(DEFAULT_MONEY_TRANSFER_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Investment createUpdatedEntity() {
        return new Investment().bakerId(UPDATED_BAKER_ID).amount(UPDATED_AMOUNT).moneyTransferId(UPDATED_MONEY_TRANSFER_ID);
    }

    @BeforeEach
    public void initTest() {
        investment = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedInvestment != null) {
            investmentRepository.delete(insertedInvestment);
            insertedInvestment = null;
        }
    }

    @Test
    @Transactional
    void createInvestment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Investment
        var returnedInvestment = om.readValue(
            restInvestmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(investment)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Investment.class
        );

        // Validate the Investment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertInvestmentUpdatableFieldsEquals(returnedInvestment, getPersistedInvestment(returnedInvestment));

        insertedInvestment = returnedInvestment;
    }

    @Test
    @Transactional
    void createInvestmentWithExistingId() throws Exception {
        // Create the Investment with an existing ID
        investment.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInvestmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(investment)))
            .andExpect(status().isBadRequest());

        // Validate the Investment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBakerIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        investment.setBakerId(null);

        // Create the Investment, which fails.

        restInvestmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(investment)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        investment.setAmount(null);

        // Create the Investment, which fails.

        restInvestmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(investment)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInvestments() throws Exception {
        // Initialize the database
        insertedInvestment = investmentRepository.saveAndFlush(investment);

        // Get all the investmentList
        restInvestmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(investment.getId().intValue())))
            .andExpect(jsonPath("$.[*].bakerId").value(hasItem(DEFAULT_BAKER_ID)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].moneyTransferId").value(hasItem(DEFAULT_MONEY_TRANSFER_ID)));
    }

    @Test
    @Transactional
    void getInvestment() throws Exception {
        // Initialize the database
        insertedInvestment = investmentRepository.saveAndFlush(investment);

        // Get the investment
        restInvestmentMockMvc
            .perform(get(ENTITY_API_URL_ID, investment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(investment.getId().intValue()))
            .andExpect(jsonPath("$.bakerId").value(DEFAULT_BAKER_ID))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.moneyTransferId").value(DEFAULT_MONEY_TRANSFER_ID));
    }

    @Test
    @Transactional
    void getNonExistingInvestment() throws Exception {
        // Get the investment
        restInvestmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInvestment() throws Exception {
        // Initialize the database
        insertedInvestment = investmentRepository.saveAndFlush(investment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the investment
        Investment updatedInvestment = investmentRepository.findById(investment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInvestment are not directly saved in db
        em.detach(updatedInvestment);
        updatedInvestment.bakerId(UPDATED_BAKER_ID).amount(UPDATED_AMOUNT).moneyTransferId(UPDATED_MONEY_TRANSFER_ID);

        restInvestmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedInvestment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedInvestment))
            )
            .andExpect(status().isOk());

        // Validate the Investment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInvestmentToMatchAllProperties(updatedInvestment);
    }

    @Test
    @Transactional
    void putNonExistingInvestment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        investment.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInvestmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, investment.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(investment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Investment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInvestment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        investment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvestmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(investment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Investment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInvestment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        investment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvestmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(investment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Investment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInvestmentWithPatch() throws Exception {
        // Initialize the database
        insertedInvestment = investmentRepository.saveAndFlush(investment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the investment using partial update
        Investment partialUpdatedInvestment = new Investment();
        partialUpdatedInvestment.setId(investment.getId());

        partialUpdatedInvestment.bakerId(UPDATED_BAKER_ID).moneyTransferId(UPDATED_MONEY_TRANSFER_ID);

        restInvestmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInvestment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInvestment))
            )
            .andExpect(status().isOk());

        // Validate the Investment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInvestmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInvestment, investment),
            getPersistedInvestment(investment)
        );
    }

    @Test
    @Transactional
    void fullUpdateInvestmentWithPatch() throws Exception {
        // Initialize the database
        insertedInvestment = investmentRepository.saveAndFlush(investment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the investment using partial update
        Investment partialUpdatedInvestment = new Investment();
        partialUpdatedInvestment.setId(investment.getId());

        partialUpdatedInvestment.bakerId(UPDATED_BAKER_ID).amount(UPDATED_AMOUNT).moneyTransferId(UPDATED_MONEY_TRANSFER_ID);

        restInvestmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInvestment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInvestment))
            )
            .andExpect(status().isOk());

        // Validate the Investment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInvestmentUpdatableFieldsEquals(partialUpdatedInvestment, getPersistedInvestment(partialUpdatedInvestment));
    }

    @Test
    @Transactional
    void patchNonExistingInvestment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        investment.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInvestmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, investment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(investment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Investment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInvestment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        investment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvestmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(investment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Investment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInvestment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        investment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInvestmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(investment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Investment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInvestment() throws Exception {
        // Initialize the database
        insertedInvestment = investmentRepository.saveAndFlush(investment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the investment
        restInvestmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, investment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return investmentRepository.count();
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

    protected Investment getPersistedInvestment(Investment investment) {
        return investmentRepository.findById(investment.getId()).orElseThrow();
    }

    protected void assertPersistedInvestmentToMatchAllProperties(Investment expectedInvestment) {
        assertInvestmentAllPropertiesEquals(expectedInvestment, getPersistedInvestment(expectedInvestment));
    }

    protected void assertPersistedInvestmentToMatchUpdatableProperties(Investment expectedInvestment) {
        assertInvestmentAllUpdatablePropertiesEquals(expectedInvestment, getPersistedInvestment(expectedInvestment));
    }
}
