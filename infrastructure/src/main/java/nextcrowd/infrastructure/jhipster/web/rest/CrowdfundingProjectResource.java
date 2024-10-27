package nextcrowd.infrastructure.jhipster.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import nextcrowd.infrastructure.jhipster.domain.CrowdfundingProject;
import nextcrowd.infrastructure.jhipster.repository.CrowdfundingProjectRepository;
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
 * REST controller for managing {@link nextcrowd.infrastructure.jhipster.domain.CrowdfundingProject}.
 */
@RestController
@RequestMapping("/api/crowdfunding-projects")
@Transactional
public class CrowdfundingProjectResource {

    private static final Logger LOG = LoggerFactory.getLogger(CrowdfundingProjectResource.class);

    private static final String ENTITY_NAME = "crowdfundingProject";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CrowdfundingProjectRepository crowdfundingProjectRepository;

    public CrowdfundingProjectResource(CrowdfundingProjectRepository crowdfundingProjectRepository) {
        this.crowdfundingProjectRepository = crowdfundingProjectRepository;
    }

    /**
     * {@code POST  /crowdfunding-projects} : Create a new crowdfundingProject.
     *
     * @param crowdfundingProject the crowdfundingProject to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new crowdfundingProject, or with status {@code 400 (Bad Request)} if the crowdfundingProject has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CrowdfundingProject> createCrowdfundingProject(@Valid @RequestBody CrowdfundingProject crowdfundingProject)
        throws URISyntaxException {
        LOG.debug("REST request to save CrowdfundingProject : {}", crowdfundingProject);
        if (crowdfundingProject.getId() != null) {
            throw new BadRequestAlertException("A new crowdfundingProject cannot already have an ID", ENTITY_NAME, "idexists");
        }
        crowdfundingProject = crowdfundingProjectRepository.save(crowdfundingProject);
        return ResponseEntity.created(new URI("/api/crowdfunding-projects/" + crowdfundingProject.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, crowdfundingProject.getId().toString()))
            .body(crowdfundingProject);
    }

    /**
     * {@code PUT  /crowdfunding-projects/:id} : Updates an existing crowdfundingProject.
     *
     * @param id the id of the crowdfundingProject to save.
     * @param crowdfundingProject the crowdfundingProject to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated crowdfundingProject,
     * or with status {@code 400 (Bad Request)} if the crowdfundingProject is not valid,
     * or with status {@code 500 (Internal Server Error)} if the crowdfundingProject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CrowdfundingProject> updateCrowdfundingProject(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CrowdfundingProject crowdfundingProject
    ) throws URISyntaxException {
        LOG.debug("REST request to update CrowdfundingProject : {}, {}", id, crowdfundingProject);
        if (crowdfundingProject.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, crowdfundingProject.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!crowdfundingProjectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        crowdfundingProject = crowdfundingProjectRepository.save(crowdfundingProject);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, crowdfundingProject.getId().toString()))
            .body(crowdfundingProject);
    }

    /**
     * {@code PATCH  /crowdfunding-projects/:id} : Partial updates given fields of an existing crowdfundingProject, field will ignore if it is null
     *
     * @param id the id of the crowdfundingProject to save.
     * @param crowdfundingProject the crowdfundingProject to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated crowdfundingProject,
     * or with status {@code 400 (Bad Request)} if the crowdfundingProject is not valid,
     * or with status {@code 404 (Not Found)} if the crowdfundingProject is not found,
     * or with status {@code 500 (Internal Server Error)} if the crowdfundingProject couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CrowdfundingProject> partialUpdateCrowdfundingProject(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CrowdfundingProject crowdfundingProject
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CrowdfundingProject partially : {}, {}", id, crowdfundingProject);
        if (crowdfundingProject.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, crowdfundingProject.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!crowdfundingProjectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CrowdfundingProject> result = crowdfundingProjectRepository
            .findById(crowdfundingProject.getId())
            .map(existingCrowdfundingProject -> {
                if (crowdfundingProject.getTitle() != null) {
                    existingCrowdfundingProject.setTitle(crowdfundingProject.getTitle());
                }
                if (crowdfundingProject.getStatus() != null) {
                    existingCrowdfundingProject.setStatus(crowdfundingProject.getStatus());
                }
                if (crowdfundingProject.getRequestedAmount() != null) {
                    existingCrowdfundingProject.setRequestedAmount(crowdfundingProject.getRequestedAmount());
                }
                if (crowdfundingProject.getCollectedAmount() != null) {
                    existingCrowdfundingProject.setCollectedAmount(crowdfundingProject.getCollectedAmount());
                }
                if (crowdfundingProject.getCurrency() != null) {
                    existingCrowdfundingProject.setCurrency(crowdfundingProject.getCurrency());
                }
                if (crowdfundingProject.getImageUrl() != null) {
                    existingCrowdfundingProject.setImageUrl(crowdfundingProject.getImageUrl());
                }
                if (crowdfundingProject.getProjectStartDate() != null) {
                    existingCrowdfundingProject.setProjectStartDate(crowdfundingProject.getProjectStartDate());
                }
                if (crowdfundingProject.getProjectEndDate() != null) {
                    existingCrowdfundingProject.setProjectEndDate(crowdfundingProject.getProjectEndDate());
                }
                if (crowdfundingProject.getNumberOfBackers() != null) {
                    existingCrowdfundingProject.setNumberOfBackers(crowdfundingProject.getNumberOfBackers());
                }
                if (crowdfundingProject.getDescription() != null) {
                    existingCrowdfundingProject.setDescription(crowdfundingProject.getDescription());
                }
                if (crowdfundingProject.getLongDescription() != null) {
                    existingCrowdfundingProject.setLongDescription(crowdfundingProject.getLongDescription());
                }
                if (crowdfundingProject.getProjectVideoUrl() != null) {
                    existingCrowdfundingProject.setProjectVideoUrl(crowdfundingProject.getProjectVideoUrl());
                }
                if (crowdfundingProject.getRisk() != null) {
                    existingCrowdfundingProject.setRisk(crowdfundingProject.getRisk());
                }
                if (crowdfundingProject.getExpectedProfit() != null) {
                    existingCrowdfundingProject.setExpectedProfit(crowdfundingProject.getExpectedProfit());
                }
                if (crowdfundingProject.getMinimumInvestment() != null) {
                    existingCrowdfundingProject.setMinimumInvestment(crowdfundingProject.getMinimumInvestment());
                }

                return existingCrowdfundingProject;
            })
            .map(crowdfundingProjectRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, crowdfundingProject.getId().toString())
        );
    }

    /**
     * {@code GET  /crowdfunding-projects} : get all the crowdfundingProjects.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of crowdfundingProjects in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CrowdfundingProject>> getAllCrowdfundingProjects(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of CrowdfundingProjects");
        Page<CrowdfundingProject> page = crowdfundingProjectRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /crowdfunding-projects/:id} : get the "id" crowdfundingProject.
     *
     * @param id the id of the crowdfundingProject to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the crowdfundingProject, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CrowdfundingProject> getCrowdfundingProject(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CrowdfundingProject : {}", id);
        Optional<CrowdfundingProject> crowdfundingProject = crowdfundingProjectRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(crowdfundingProject);
    }

    /**
     * {@code DELETE  /crowdfunding-projects/:id} : delete the "id" crowdfundingProject.
     *
     * @param id the id of the crowdfundingProject to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCrowdfundingProject(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CrowdfundingProject : {}", id);
        crowdfundingProjectRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
