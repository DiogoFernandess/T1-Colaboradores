package colaboradores.requisitos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Table(name = "colaboradores")
@Entity
@Data
public class Colaborador {

    @Id
    @Column
    private String matricula;

    @Column
    private String nome;

    @Column
    @DecimalMin(value = "0.00", message = "O salário não pode ser negativo")
    private BigDecimal salario;

    @Enumerated(EnumType.STRING)
    @Column
    private TipoColaborador tipo;
}
