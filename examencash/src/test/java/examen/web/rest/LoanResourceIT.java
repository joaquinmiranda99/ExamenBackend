package examen.web.rest;

import examen.ExamencashApp;
import examen.domain.Loan;
import examen.domain.Usuario;
import examen.repository.LoanRepository;
import examen.service.LoanService;
import examen.service.dto.LoanDTO;
import examen.service.mapper.LoanMapper;
import examen.service.dto.LoanCriteria;
import examen.service.LoanQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link LoanResource} REST controller.
 */
@SpringBootTest(classes = ExamencashApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class LoanResourceIT {

    private static final Float DEFAULT_TOTAL = 1F;
    private static final Float UPDATED_TOTAL = 2F;
    private static final Float SMALLER_TOTAL = 1F - 1F;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanMapper loanMapper;

    @Autowired
    private LoanService loanService;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLoanMockMvc;

    private Loan loan;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Loan createEntity(EntityManager em) {
        Loan loan = new Loan()
            .total(DEFAULT_TOTAL);
        return loan;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Loan createUpdatedEntity(EntityManager em) {
        Loan loan = new Loan()
            .total(UPDATED_TOTAL);
        return loan;
    }

    @BeforeEach
    public void initTest() {
        loan = createEntity(em);
    }

    @Test
    @Transactional
    public void createLoan() throws Exception {
        int databaseSizeBeforeCreate = loanRepository.findAll().size();
        // Create the Loan
        LoanDTO loanDTO = loanMapper.toDto(loan);
        restLoanMockMvc.perform(post("/api/loans")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(loanDTO)))
            .andExpect(status().isCreated());

        // Validate the Loan in the database
        List<Loan> loanList = loanRepository.findAll();
        assertThat(loanList).hasSize(databaseSizeBeforeCreate + 1);
        Loan testLoan = loanList.get(loanList.size() - 1);
        assertThat(testLoan.getTotal()).isEqualTo(DEFAULT_TOTAL);
    }

    @Test
    @Transactional
    public void createLoanWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = loanRepository.findAll().size();

        // Create the Loan with an existing ID
        loan.setId(1L);
        LoanDTO loanDTO = loanMapper.toDto(loan);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLoanMockMvc.perform(post("/api/loans")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(loanDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Loan in the database
        List<Loan> loanList = loanRepository.findAll();
        assertThat(loanList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkTotalIsRequired() throws Exception {
        int databaseSizeBeforeTest = loanRepository.findAll().size();
        // set the field null
        loan.setTotal(null);

        // Create the Loan, which fails.
        LoanDTO loanDTO = loanMapper.toDto(loan);


        restLoanMockMvc.perform(post("/api/loans")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(loanDTO)))
            .andExpect(status().isBadRequest());

        List<Loan> loanList = loanRepository.findAll();
        assertThat(loanList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLoans() throws Exception {
        // Initialize the database
        loanRepository.saveAndFlush(loan);

        // Get all the loanList
        restLoanMockMvc.perform(get("/api/loans?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(loan.getId().intValue())))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL.doubleValue())));
    }
    
    @Test
    @Transactional
    public void getLoan() throws Exception {
        // Initialize the database
        loanRepository.saveAndFlush(loan);

        // Get the loan
        restLoanMockMvc.perform(get("/api/loans/{id}", loan.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(loan.getId().intValue()))
            .andExpect(jsonPath("$.total").value(DEFAULT_TOTAL.doubleValue()));
    }


    @Test
    @Transactional
    public void getLoansByIdFiltering() throws Exception {
        // Initialize the database
        loanRepository.saveAndFlush(loan);

        Long id = loan.getId();

        defaultLoanShouldBeFound("id.equals=" + id);
        defaultLoanShouldNotBeFound("id.notEquals=" + id);

        defaultLoanShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultLoanShouldNotBeFound("id.greaterThan=" + id);

        defaultLoanShouldBeFound("id.lessThanOrEqual=" + id);
        defaultLoanShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllLoansByTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        loanRepository.saveAndFlush(loan);

        // Get all the loanList where total equals to DEFAULT_TOTAL
        defaultLoanShouldBeFound("total.equals=" + DEFAULT_TOTAL);

        // Get all the loanList where total equals to UPDATED_TOTAL
        defaultLoanShouldNotBeFound("total.equals=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    public void getAllLoansByTotalIsNotEqualToSomething() throws Exception {
        // Initialize the database
        loanRepository.saveAndFlush(loan);

        // Get all the loanList where total not equals to DEFAULT_TOTAL
        defaultLoanShouldNotBeFound("total.notEquals=" + DEFAULT_TOTAL);

        // Get all the loanList where total not equals to UPDATED_TOTAL
        defaultLoanShouldBeFound("total.notEquals=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    public void getAllLoansByTotalIsInShouldWork() throws Exception {
        // Initialize the database
        loanRepository.saveAndFlush(loan);

        // Get all the loanList where total in DEFAULT_TOTAL or UPDATED_TOTAL
        defaultLoanShouldBeFound("total.in=" + DEFAULT_TOTAL + "," + UPDATED_TOTAL);

        // Get all the loanList where total equals to UPDATED_TOTAL
        defaultLoanShouldNotBeFound("total.in=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    public void getAllLoansByTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        loanRepository.saveAndFlush(loan);

        // Get all the loanList where total is not null
        defaultLoanShouldBeFound("total.specified=true");

        // Get all the loanList where total is null
        defaultLoanShouldNotBeFound("total.specified=false");
    }

    @Test
    @Transactional
    public void getAllLoansByTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        loanRepository.saveAndFlush(loan);

        // Get all the loanList where total is greater than or equal to DEFAULT_TOTAL
        defaultLoanShouldBeFound("total.greaterThanOrEqual=" + DEFAULT_TOTAL);

        // Get all the loanList where total is greater than or equal to UPDATED_TOTAL
        defaultLoanShouldNotBeFound("total.greaterThanOrEqual=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    public void getAllLoansByTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        loanRepository.saveAndFlush(loan);

        // Get all the loanList where total is less than or equal to DEFAULT_TOTAL
        defaultLoanShouldBeFound("total.lessThanOrEqual=" + DEFAULT_TOTAL);

        // Get all the loanList where total is less than or equal to SMALLER_TOTAL
        defaultLoanShouldNotBeFound("total.lessThanOrEqual=" + SMALLER_TOTAL);
    }

    @Test
    @Transactional
    public void getAllLoansByTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        loanRepository.saveAndFlush(loan);

        // Get all the loanList where total is less than DEFAULT_TOTAL
        defaultLoanShouldNotBeFound("total.lessThan=" + DEFAULT_TOTAL);

        // Get all the loanList where total is less than UPDATED_TOTAL
        defaultLoanShouldBeFound("total.lessThan=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    public void getAllLoansByTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        loanRepository.saveAndFlush(loan);

        // Get all the loanList where total is greater than DEFAULT_TOTAL
        defaultLoanShouldNotBeFound("total.greaterThan=" + DEFAULT_TOTAL);

        // Get all the loanList where total is greater than SMALLER_TOTAL
        defaultLoanShouldBeFound("total.greaterThan=" + SMALLER_TOTAL);
    }


    @Test
    @Transactional
    public void getAllLoansByUsuarioIsEqualToSomething() throws Exception {
        // Initialize the database
        loanRepository.saveAndFlush(loan);
        Usuario usuario = UsuarioResourceIT.createEntity(em);
        em.persist(usuario);
        em.flush();
        loan.setUsuario(usuario);
        loanRepository.saveAndFlush(loan);
        Long usuarioId = usuario.getId();

        // Get all the loanList where usuario equals to usuarioId
        defaultLoanShouldBeFound("usuarioId.equals=" + usuarioId);

        // Get all the loanList where usuario equals to usuarioId + 1
        defaultLoanShouldNotBeFound("usuarioId.equals=" + (usuarioId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLoanShouldBeFound(String filter) throws Exception {
        restLoanMockMvc.perform(get("/api/loans?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(loan.getId().intValue())))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL.doubleValue())));

        // Check, that the count call also returns 1
        restLoanMockMvc.perform(get("/api/loans/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLoanShouldNotBeFound(String filter) throws Exception {
        restLoanMockMvc.perform(get("/api/loans?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLoanMockMvc.perform(get("/api/loans/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingLoan() throws Exception {
        // Get the loan
        restLoanMockMvc.perform(get("/api/loans/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLoan() throws Exception {
        // Initialize the database
        loanRepository.saveAndFlush(loan);

        int databaseSizeBeforeUpdate = loanRepository.findAll().size();

        // Update the loan
        Loan updatedLoan = loanRepository.findById(loan.getId()).get();
        // Disconnect from session so that the updates on updatedLoan are not directly saved in db
        em.detach(updatedLoan);
        updatedLoan
            .total(UPDATED_TOTAL);
        LoanDTO loanDTO = loanMapper.toDto(updatedLoan);

        restLoanMockMvc.perform(put("/api/loans")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(loanDTO)))
            .andExpect(status().isOk());

        // Validate the Loan in the database
        List<Loan> loanList = loanRepository.findAll();
        assertThat(loanList).hasSize(databaseSizeBeforeUpdate);
        Loan testLoan = loanList.get(loanList.size() - 1);
        assertThat(testLoan.getTotal()).isEqualTo(UPDATED_TOTAL);
    }

    @Test
    @Transactional
    public void updateNonExistingLoan() throws Exception {
        int databaseSizeBeforeUpdate = loanRepository.findAll().size();

        // Create the Loan
        LoanDTO loanDTO = loanMapper.toDto(loan);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLoanMockMvc.perform(put("/api/loans")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(loanDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Loan in the database
        List<Loan> loanList = loanRepository.findAll();
        assertThat(loanList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteLoan() throws Exception {
        // Initialize the database
        loanRepository.saveAndFlush(loan);

        int databaseSizeBeforeDelete = loanRepository.findAll().size();

        // Delete the loan
        restLoanMockMvc.perform(delete("/api/loans/{id}", loan.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Loan> loanList = loanRepository.findAll();
        assertThat(loanList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
