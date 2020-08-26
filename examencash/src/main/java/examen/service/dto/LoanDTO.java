package examen.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * A DTO for the {@link examen.domain.Loan} entity.
 */
public class LoanDTO implements Serializable {
    
    private Long id;

    @NotNull
    private Float total;


    private Long usuarioId;

    private String usuarioFirstname;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioFirstname() {
        return usuarioFirstname;
    }

    public void setUsuarioFirstname(String usuarioFirstname) {
        this.usuarioFirstname = usuarioFirstname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LoanDTO)) {
            return false;
        }

        return id != null && id.equals(((LoanDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LoanDTO{" +
            "id=" + getId() +
            ", total=" + getTotal() +
            ", usuarioId=" + getUsuarioId() +
            ", usuarioFirstname='" + getUsuarioFirstname() + "'" +
            "}";
    }
}
