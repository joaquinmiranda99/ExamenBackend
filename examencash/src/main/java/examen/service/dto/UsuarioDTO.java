package examen.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;

import java.util.List;
import examen.service.dto.LoanDTO;

/**
 * A DTO for the {@link examen.domain.Usuario} entity.
 */
public class UsuarioDTO implements Serializable {
    
    private Long id;

    @NotNull
    private String email;

    private String firstname;

    private String lastname;

    private List<LoanDTO> loans;

    public List<LoanDTO> getLoans () {
        return loans;
    }

    public void setLoans (List<LoanDTO> loans) {
        this.loans = loans;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UsuarioDTO)) {
            return false;
        }

        return id != null && id.equals(((UsuarioDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UsuarioDTO{" +
            "id=" + getId() +
            ", email='" + getEmail() + "'" +
            ", firstname='" + getFirstname() + "'" +
            ", lastname='" + getLastname() + "'" +
            "}";
    }
}
