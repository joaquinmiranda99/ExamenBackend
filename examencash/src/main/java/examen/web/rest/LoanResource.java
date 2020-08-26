package examen.web.rest;

import examen.service.LoanService;
import examen.web.rest.errors.BadRequestAlertException;
import examen.service.dto.LoanDTO;
import examen.service.dto.LoanCriteria;
import io.github.jhipster.service.filter.LongFilter;
import examen.service.LoanQueryService;
import examen.service.UsuarioService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import examen.service.dto.UsuarioDTO;


import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link examen.domain.Loan}.
 */
@RestController
@RequestMapping("/api")
public class LoanResource {

    private final Logger log = LoggerFactory.getLogger(LoanResource.class);

    private static final String ENTITY_NAME = "loan";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LoanService loanService;

    private final LoanQueryService loanQueryService;

    private final UsuarioService usuarioService;

    public LoanResource(LoanService loanService, LoanQueryService loanQueryService, UsuarioService usuarioService) {
        this.loanService = loanService;
        this.loanQueryService = loanQueryService;
        this.usuarioService = usuarioService;
    }

    /**
     * {@code POST  /loans} : Create a new loan.
     *
     * @param loanDTO the loanDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new loanDTO, or with status {@code 400 (Bad Request)} if the loan has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/loans")
    public ResponseEntity<LoanDTO> createLoan(@Valid @RequestBody LoanDTO loanDTO) throws URISyntaxException {
        log.debug("REST request to save Loan : {}", loanDTO);
        if (loanDTO.getId() != null) {
            throw new BadRequestAlertException("A new loan cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LoanDTO result = loanService.save(loanDTO);
        return ResponseEntity.created(new URI("/api/loans/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /loans} : Updates an existing loan.
     *
     * @param loanDTO the loanDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated loanDTO,
     * or with status {@code 400 (Bad Request)} if the loanDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the loanDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/loans")
    public ResponseEntity<LoanDTO> updateLoan(@Valid @RequestBody LoanDTO loanDTO) throws URISyntaxException {
        log.debug("REST request to update Loan : {}", loanDTO);
        if (loanDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        LoanDTO result = loanService.save(loanDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, loanDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /loans} : get all the loans.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of loans in body.
     */
    @GetMapping("/loans")
    public ResponseEntity<List<LoanDTO>> getAllLoans(LoanCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Loans by criteria: {}", criteria);
        Page<LoanDTO> page = loanQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /loans} : get all the loans.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of loans in body.
     */
    @GetMapping("/loans/persona/{id}")
    public ResponseEntity<UsuarioDTO> getAllLoans(@PathVariable Long id, Pageable pageable) {
        log.debug("REST request to get Loans by idPersona: " + id);
        LoanCriteria criteria = new LoanCriteria();
        LongFilter filter = new LongFilter();
        filter.setEquals(id);
        criteria.setUsuarioId(filter);
        Page<LoanDTO> page = loanQueryService.findByCriteria(criteria, pageable);
        Optional<UsuarioDTO> usuario = usuarioService.findOne(id);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        if (usuario.isPresent()) {
            usuario.get().setLoans(page.getContent());
            return ResponseEntity.ok().headers(headers).body(usuario.get());
        }
        return ResponseEntity.ok().headers(headers).body(null);
    }

    /**
     * {@code GET  /loans/count} : count all the loans.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/loans/count")
    public ResponseEntity<Long> countLoans(LoanCriteria criteria) {
        log.debug("REST request to count Loans by criteria: {}", criteria);
        return ResponseEntity.ok().body(loanQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /loans/:id} : get the "id" loan.
     *
     * @param id the id of the loanDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the loanDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/loans/{id}")
    public ResponseEntity<LoanDTO> getLoan(@PathVariable Long id) {
        log.debug("REST request to get Loan : {}", id);
        Optional<LoanDTO> loanDTO = loanService.findOne(id);
        return ResponseUtil.wrapOrNotFound(loanDTO);
    }

    /**
     * {@code DELETE  /loans/:id} : delete the "id" loan.
     *
     * @param id the id of the loanDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/loans/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        log.debug("REST request to delete Loan : {}", id);
        loanService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
