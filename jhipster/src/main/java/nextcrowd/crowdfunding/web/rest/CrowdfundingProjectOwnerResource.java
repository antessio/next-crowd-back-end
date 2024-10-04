package nextcrowd.crowdfunding.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import nextcrowd.crowdfunding.domain.CrowdfundingProjectOwner;
import nextcrowd.crowdfunding.repository.CrowdfundingProjectOwnerRepository;
import nextcrowd.crowdfunding.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link nextcrowd.crowdfunding.domain.CrowdfundingProjectOwner}.
 */
@RestController
@RequestMapping("/api/crowdfunding-project-owners")
@Transactional
public class CrowdfundingProjectOwnerResource {

    private static final Logger LOG = LoggerFactory.getLogger(CrowdfundingProjectOwnerResource.class);

    private static final String ENTITY_NAME = "crowdfundingProjectOwner";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CrowdfundingProjectOwnerRepository crowdfundingProjectOwnerRepository;

    public CrowdfundingProjectOwnerResource(CrowdfundingProjectOwnerRepository crowdfundingProjectOwnerRepository) {
        this.crowdfundingProjectOwnerRepository = crowdfundingProjectOwnerRepository;
    }

    /**
     * {@code POST  /crowdfunding-project-owners} : Create a new crowdfundingProjectOwner.
     *
     * @param crowdfundingProjectOwner the crowdfundingProjectOwner to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new crowdfundingProjectOwner, or with status {@code 400 (Bad Request)} if the crowdfundingProjectOwner has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CrowdfundingProjectOwner> createCrowdfundingProjectOwner(
        @Valid @RequestBody CrowdfundingProjectOwner crowdfundingProjectOwner
    ) throws URISyntaxException {
        LOG.debug("REST request to save CrowdfundingProjectOwner : {}", crowdfundingProjectOwner);
        if (crowdfundingProjectOwner.getId() != null) {
            throw new BadRequestAlertException("A new crowdfundingProjectOwner cannot already have an ID", ENTITY_NAME, "idexists");
        }
        crowdfundingProjectOwner = crowdfundingProjectOwnerRepository.save(crowdfundingProjectOwner);
        return ResponseEntity.created(new URI("/api/crowdfunding-project-owners/" + crowdfundingProjectOwner.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, crowdfundingProjectOwner.getId().toString()))
            .body(crowdfundingProjectOwner);
    }

    /**
     * {@code PUT  /crowdfunding-project-owners/:id} : Updates an existing crowdfundingProjectOwner.
     *
     * @param id the id of the crowdfundingProjectOwner to save.
     * @param crowdfundingProjectOwner the crowdfundingProjectOwner to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated crowdfundingProjectOwner,
     * or with status {@code 400 (Bad Request)} if the crowdfundingProjectOwner is not valid,
     * or with status {@code 500 (Internal Server Error)} if the crowdfundingProjectOwner couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CrowdfundingProjectOwner> updateCrowdfundingProjectOwner(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CrowdfundingProjectOwner crowdfundingProjectOwner
    ) throws URISyntaxException {
        LOG.debug("REST request to update CrowdfundingProjectOwner : {}, {}", id, crowdfundingProjectOwner);
        if (crowdfundingProjectOwner.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, crowdfundingProjectOwner.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!crowdfundingProjectOwnerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        crowdfundingProjectOwner = crowdfundingProjectOwnerRepository.save(crowdfundingProjectOwner);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, crowdfundingProjectOwner.getId().toString()))
            .body(crowdfundingProjectOwner);
    }

    /**
     * {@code PATCH  /crowdfunding-project-owners/:id} : Partial updates given fields of an existing crowdfundingProjectOwner, field will ignore if it is null
     *
     * @param id the id of the crowdfundingProjectOwner to save.
     * @param crowdfundingProjectOwner the crowdfundingProjectOwner to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated crowdfundingProjectOwner,
     * or with status {@code 400 (Bad Request)} if the crowdfundingProjectOwner is not valid,
     * or with status {@code 404 (Not Found)} if the crowdfundingProjectOwner is not found,
     * or with status {@code 500 (Internal Server Error)} if the crowdfundingProjectOwner couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CrowdfundingProjectOwner> partialUpdateCrowdfundingProjectOwner(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CrowdfundingProjectOwner crowdfundingProjectOwner
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CrowdfundingProjectOwner partially : {}, {}", id, crowdfundingProjectOwner);
        if (crowdfundingProjectOwner.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, crowdfundingProjectOwner.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!crowdfundingProjectOwnerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CrowdfundingProjectOwner> result = crowdfundingProjectOwnerRepository
            .findById(crowdfundingProjectOwner.getId())
            .map(existingCrowdfundingProjectOwner -> {
                if (crowdfundingProjectOwner.getName() != null) {
                    existingCrowdfundingProjectOwner.setName(crowdfundingProjectOwner.getName());
                }
                if (crowdfundingProjectOwner.getImageUrl() != null) {
                    existingCrowdfundingProjectOwner.setImageUrl(crowdfundingProjectOwner.getImageUrl());
                }

                return existingCrowdfundingProjectOwner;
            })
            .map(crowdfundingProjectOwnerRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, crowdfundingProjectOwner.getId().toString())
        );
    }

    /**
     * {@code GET  /crowdfunding-project-owners} : get all the crowdfundingProjectOwners.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of crowdfundingProjectOwners in body.
     */
    @GetMapping("")
    public List<CrowdfundingProjectOwner> getAllCrowdfundingProjectOwners() {
        LOG.debug("REST request to get all CrowdfundingProjectOwners");
        return crowdfundingProjectOwnerRepository.findAll();
    }

    /**
     * {@code GET  /crowdfunding-project-owners/:id} : get the "id" crowdfundingProjectOwner.
     *
     * @param id the id of the crowdfundingProjectOwner to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the crowdfundingProjectOwner, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CrowdfundingProjectOwner> getCrowdfundingProjectOwner(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CrowdfundingProjectOwner : {}", id);
        Optional<CrowdfundingProjectOwner> crowdfundingProjectOwner = crowdfundingProjectOwnerRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(crowdfundingProjectOwner);
    }

    /**
     * {@code DELETE  /crowdfunding-project-owners/:id} : delete the "id" crowdfundingProjectOwner.
     *
     * @param id the id of the crowdfundingProjectOwner to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCrowdfundingProjectOwner(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CrowdfundingProjectOwner : {}", id);
        crowdfundingProjectOwnerRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
