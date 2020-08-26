package examen.service.mapper;


import examen.domain.*;
import examen.service.dto.LoanDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Loan} and its DTO {@link LoanDTO}.
 */
@Mapper(componentModel = "spring", uses = {UsuarioMapper.class})
public interface LoanMapper extends EntityMapper<LoanDTO, Loan> {

    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.firstname", target = "usuarioFirstname")
    LoanDTO toDto(Loan loan);

    @Mapping(source = "usuarioId", target = "usuario")
    Loan toEntity(LoanDTO loanDTO);

    default Loan fromId(Long id) {
        if (id == null) {
            return null;
        }
        Loan loan = new Loan();
        loan.setId(id);
        return loan;
    }
}
