package nextcrowd.infrastructure.jhipster.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import nextcrowd.infrastructure.jhipster.domain.ProjectReward;
import nextcrowd.infrastructure.jhipster.repository.ProjectRewardRepository;
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
 * REST controller for managing {@link nextcrowd.infrastructure.jhipster.domain.ProjectReward}.
 */
@RestController
@RequestMapping("/api/project-rewards")
@Transactional
public class ProjectRewardResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectRewardResource.class);

    private static final String ENTITY_NAME = "projectReward";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectRewardRepository projectRewardRepository;

    public ProjectRewardResource(ProjectRewardRepository projectRewardRepository) {
        this.projectRewardRepository = projectRewardRepository;
    }

    /**
     * {@code POST  /project-rewards} : Create a new projectReward.
     *
     * @param projectReward the projectReward to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectReward, or with status {@code 400 (Bad Request)} if the projectReward has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProjectReward> createProjectReward(@Valid @RequestBody ProjectReward projectReward) throws URISyntaxException {
        LOG.debug("REST request to save ProjectReward : {}", projectReward);
        if (projectReward.getId() != null) {
            throw new BadRequestAlertException("A new projectReward cannot already have an ID", ENTITY_NAME, "idexists");
        }
        projectReward = projectRewardRepository.save(projectReward);
        return ResponseEntity.created(new URI("/api/project-rewards/" + projectReward.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, projectReward.getId().toString()))
            .body(projectReward);
    }

    /**
     * {@code PUT  /project-rewards/:id} : Updates an existing projectReward.
     *
     * @param id the id of the projectReward to save.
     * @param projectReward the projectReward to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectReward,
     * or with status {@code 400 (Bad Request)} if the projectReward is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectReward couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectReward> updateProjectReward(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProjectReward projectReward
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProjectReward : {}, {}", id, projectReward);
        if (projectReward.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectReward.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectRewardRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        projectReward = projectRewardRepository.save(projectReward);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectReward.getId().toString()))
            .body(projectReward);
    }

    /**
     * {@code PATCH  /project-rewards/:id} : Partial updates given fields of an existing projectReward, field will ignore if it is null
     *
     * @param id the id of the projectReward to save.
     * @param projectReward the projectReward to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectReward,
     * or with status {@code 400 (Bad Request)} if the projectReward is not valid,
     * or with status {@code 404 (Not Found)} if the projectReward is not found,
     * or with status {@code 500 (Internal Server Error)} if the projectReward couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProjectReward> partialUpdateProjectReward(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProjectReward projectReward
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProjectReward partially : {}, {}", id, projectReward);
        if (projectReward.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectReward.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectRewardRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProjectReward> result = projectRewardRepository
            .findById(projectReward.getId())
            .map(existingProjectReward -> {
                if (projectReward.getName() != null) {
                    existingProjectReward.setName(projectReward.getName());
                }
                if (projectReward.getImageUrl() != null) {
                    existingProjectReward.setImageUrl(projectReward.getImageUrl());
                }
                if (projectReward.getDescription() != null) {
                    existingProjectReward.setDescription(projectReward.getDescription());
                }

                return existingProjectReward;
            })
            .map(projectRewardRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectReward.getId().toString())
        );
    }

    /**
     * {@code GET  /project-rewards} : get all the projectRewards.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projectRewards in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ProjectReward>> getAllProjectRewards(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of ProjectRewards");
        Page<ProjectReward> page = projectRewardRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /project-rewards/:id} : get the "id" projectReward.
     *
     * @param id the id of the projectReward to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectReward, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectReward> getProjectReward(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProjectReward : {}", id);
        Optional<ProjectReward> projectReward = projectRewardRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(projectReward);
    }

    /**
     * {@code DELETE  /project-rewards/:id} : delete the "id" projectReward.
     *
     * @param id the id of the projectReward to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectReward(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProjectReward : {}", id);
        projectRewardRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
