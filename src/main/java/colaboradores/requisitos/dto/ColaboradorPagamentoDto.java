package colaboradores.requisitos.dto;

import colaboradores.requisitos.entity.TipoColaborador;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ColaboradorPagamentoDto {

    private String matricula;

    private String nome;

    private TipoColaborador tipoColaborador;

    private BigDecimal salario;

    private BigDecimal adicional;

    private BigDecimal total;
}
