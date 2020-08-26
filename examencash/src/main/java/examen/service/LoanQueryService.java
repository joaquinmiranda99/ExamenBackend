package examen.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import examen.domain.Loan;
import examen.domain.*; // for static metamodels
import examen.repository.LoanRepository;
import examen.service.dto.LoanCriteria;
import examen.service.dto.LoanDTO;
import examen.service.mapper.LoanMapper;

/**
 * Service for executing complex queries for {@link Loan} entities in the database.
 * The main input is a {@link LoanCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LoanDTO} or a {@link Page} of {@link LoanDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LoanQueryService extends QueryService<Loan> {

    private final Logger log = LoggerFactory.getLogger(LoanQueryService.class);

    private final LoanRepository loanRepository;

    private final LoanMapper loanMapper;

    public LoanQueryService(LoanRepository loanRepository, LoanMapper loanMapper) {
        this.loanRepository = loanRepository;
        this.loanMapper = loanMapper;
    }

    /**
     * Return a {@link List} of {@link LoanDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<LoanDTO> findByCriteria(LoanCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Loan> specification = createSpecification(criteria);
        return loanMapper.toDto(loanRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link LoanDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LoanDTO> findByCriteria(LoanCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Loan> specification = createSpecification(criteria);
        return loanRepository.findAll(specification, page)
            .map(loanMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LoanCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Loan> specification = createSpecification(criteria);
        return loanRepository.count(specification);
    }

    /**
     * Function to convert {@link LoanCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Loan> createSpecification(LoanCriteria criteria) {
        Specification<Loan> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Loan_.id));
            }
            if (criteria.getTotal() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotal(), Loan_.total));
            }
            if (criteria.getUsuarioId() != null) {
                specification = specification.and(buildSpecification(criteria.getUsuarioId(),
                    root -> root.join(Loan_.usuario, JoinType.LEFT).get(Usuario_.id)));
            }
        }
        return specification;
    }
}
