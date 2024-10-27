package nextcrowd.infrastructure.jhipster.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import nextcrowd.infrastructure.jhipster.domain.ProjectOwner;
import nextcrowd.infrastructure.jhipster.repository.ProjectOwnerRepository;
import nextcrowd.infrastructure.jhipster.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link nextcrowd.infrastructure.jhipster.domain.ProjectOwner}.
 */
@RestController
@RequestMapping("/api/project-owners")
@Transactional
public class ProjectOwnerResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectOwnerResource.class);

    private static final String ENTITY_NAME = "projectOwner";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectOwnerRepository projectOwnerRepository;

    public ProjectOwnerResource(ProjectOwnerRepository projectOwnerRepository) {
        this.projectOwnerRepository = projectOwnerRepository;
    }

    /**
     * {@code POST  /project-owners} : Create a new projectOwner.
     *
     * @param projectOwner the projectOwner to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectOwner, or with status {@code 400 (Bad Request)} if the projectOwner has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProjectOwner> createProjectOwner(@Valid @RequestBody ProjectOwner projectOwner) throws URISyntaxException {
        LOG.debug("REST request to save ProjectOwner : {}", projectOwner);
        if (projectOwner.getId() != null) {
            throw new BadRequestAlertException("A new projectOwner cannot already have an ID", ENTITY_NAME, "idexists");
        }
        projectOwner = projectOwnerRepository.save(projectOwner);
        return ResponseEntity.created(new URI("/api/project-owners/" + projectOwner.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, projectOwner.getId().toString()))
            .body(projectOwner);
    }

    /**
     * {@code PUT  /project-owners/:id} : Updates an existing projectOwner.
     *
     * @param id the id of the projectOwner to save.
     * @param projectOwner the projectOwner to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectOwner,
     * or with status {@code 400 (Bad Request)} if the projectOwner is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectOwner couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectOwner> updateProjectOwner(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProjectOwner projectOwner
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProjectOwner : {}, {}", id, projectOwner);
        if (projectOwner.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectOwner.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectOwnerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        projectOwner = projectOwnerRepository.save(projectOwner);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectOwner.getId().toString()))
            .body(projectOwner);
    }

    /**
     * {@code PATCH  /project-owners/:id} : Partial updates given fields of an existing projectOwner, field will ignore if it is null
     *
     * @param id the id of the projectOwner to save.
     * @param projectOwner the projectOwner to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectOwner,
     * or with status {@code 400 (Bad Request)} if the projectOwner is not valid,
     * or with status {@code 404 (Not Found)} if the projectOwner is not found,
     * or with status {@code 500 (Internal Server Error)} if the projectOwner couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProjectOwner> partialUpdateProjectOwner(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProjectOwner projectOwner
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProjectOwner partially : {}, {}", id, projectOwner);
        if (projectOwner.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectOwner.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectOwnerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProjectOwner> result = projectOwnerRepository
            .findById(projectOwner.getId())
            .map(existingProjectOwner -> {
                if (projectOwner.getName() != null) {
                    existingProjectOwner.setName(projectOwner.getName());
                }
                if (projectOwner.getImageUrl() != null) {
                    existingProjectOwner.setImageUrl(projectOwner.getImageUrl());
                }

                return existingProjectOwner;
            })
            .map(projectOwnerRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectOwner.getId().toString())
        );
    }

    /**
     * {@code GET  /project-owners} : get all the projectOwners.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projectOwners in body.
     */
    @GetMapping("")
    public List<ProjectOwner> getAllProjectOwners(@RequestParam(name = "filter", required = false) String filter) {
        if ("crowdfundingproject-is-null".equals(filter)) {
            LOG.debug("REST request to get all ProjectOwners where crowdfundingProject is null");
            return StreamSupport.stream(projectOwnerRepository.findAll().spliterator(), false)
                .filter(projectOwner -> projectOwner.getCrowdfundingProject() == null)
                .toList();
        }
        LOG.debug("REST request to get all ProjectOwners");
        return projectOwnerRepository.findAll();
    }

    /**
     * {@code GET  /project-owners/:id} : get the "id" projectOwner.
     *
     * @param id the id of the projectOwner to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectOwner, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectOwner> getProjectOwner(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProjectOwner : {}", id);
        Optional<ProjectOwner> projectOwner = projectOwnerRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(projectOwner);
    }

    /**
     * {@code DELETE  /project-owners/:id} : delete the "id" projectOwner.
     *
     * @param id the id of the projectOwner to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectOwner(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProjectOwner : {}", id);
        projectOwnerRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
