package colaboradores.requisitos.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ColaboradorPagamento {

    private String matricula;

    private String nome;

    private BigDecimal salario;

    private BigDecimal adicional;

    private BigDecimal total;
}
