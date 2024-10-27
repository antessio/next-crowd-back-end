package nextcrowd.infrastructure.jhipster.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import nextcrowd.infrastructure.jhipster.domain.Investment;
import nextcrowd.infrastructure.jhipster.repository.InvestmentRepository;
import nextcrowd.infrastructure.jhipster.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link nextcrowd.infrastructure.jhipster.domain.Investment}.
 */
@RestController
@RequestMapping("/api/investments")
@Transactional
public class InvestmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(InvestmentResource.class);

    private static final String ENTITY_NAME = "investment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InvestmentRepository investmentRepository;

    public InvestmentResource(InvestmentRepository investmentRepository) {
        this.investmentRepository = investmentRepository;
    }

    /**
     * {@code POST  /investments} : Create a new investment.
     *
     * @param investment the investment to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new investment, or with status {@code 400 (Bad Request)} if the investment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Investment> createInvestment(@Valid @RequestBody Investment investment) throws URISyntaxException {
        LOG.debug("REST request to save Investment : {}", investment);
        if (investment.getId() != null) {
            throw new BadRequestAlertException("A new investment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        investment = investmentRepository.save(investment);
        return ResponseEntity.created(new URI("/api/investments/" + investment.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, investment.getId().toString()))
            .body(investment);
    }

    /**
     * {@code PUT  /investments/:id} : Updates an existing investment.
     *
     * @param id the id of the investment to save.
     * @param investment the investment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated investment,
     * or with status {@code 400 (Bad Request)} if the investment is not valid,
     * or with status {@code 500 (Internal Server Error)} if the investment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Investment> updateInvestment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Investment investment
    ) throws URISyntaxException {
        LOG.debug("REST request to update Investment : {}, {}", id, investment);
        if (investment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, investment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!investmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        investment = investmentRepository.save(investment);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, investment.getId().toString()))
            .body(investment);
    }

    /**
     * {@code PATCH  /investments/:id} : Partial updates given fields of an existing investment, field will ignore if it is null
     *
     * @param id the id of the investment to save.
     * @param investment the investment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated investment,
     * or with status {@code 400 (Bad Request)} if the investment is not valid,
     * or with status {@code 404 (Not Found)} if the investment is not found,
     * or with status {@code 500 (Internal Server Error)} if the investment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Investment> partialUpdateInvestment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Investment investment
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Investment partially : {}, {}", id, investment);
        if (investment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, investment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!investmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Investment> result = investmentRepository
            .findById(investment.getId())
            .map(existingInvestment -> {
                if (investment.getBakerId() != null) {
                    existingInvestment.setBakerId(investment.getBakerId());
                }
                if (investment.getAmount() != null) {
                    existingInvestment.setAmount(investment.getAmount());
                }
                if (investment.getMoneyTransferId() != null) {
                    existingInvestment.setMoneyTransferId(investment.getMoneyTransferId());
                }

                return existingInvestment;
            })
            .map(investmentRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, investment.getId().toString())
        );
    }

    /**
     * {@code GET  /investments} : get all the investments.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of investments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Investment>> getAllInvestments(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Investments");
        Page<Investment> page = investmentRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /investments/:id} : get the "id" investment.
     *
     * @param id the id of the investment to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the investment, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Investment> getInvestment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Investment : {}", id);
        Optional<Investment> investment = investmentRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(investment);
    }

    /**
     * {@code DELETE  /investments/:id} : delete the "id" investment.
     *
     * @param id the id of the investment to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvestment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Investment : {}", id);
        investmentRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
