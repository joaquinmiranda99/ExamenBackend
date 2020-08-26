package examen.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import examen.web.rest.TestUtil;

public class LoanDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LoanDTO.class);
        LoanDTO loanDTO1 = new LoanDTO();
        loanDTO1.setId(1L);
        LoanDTO loanDTO2 = new LoanDTO();
        assertThat(loanDTO1).isNotEqualTo(loanDTO2);
        loanDTO2.setId(loanDTO1.getId());
        assertThat(loanDTO1).isEqualTo(loanDTO2);
        loanDTO2.setId(2L);
        assertThat(loanDTO1).isNotEqualTo(loanDTO2);
        loanDTO1.setId(null);
        assertThat(loanDTO1).isNotEqualTo(loanDTO2);
    }
}
