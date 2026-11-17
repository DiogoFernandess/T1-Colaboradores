package colaboradores.requisitos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private String nome;

    @Column
    private BigDecimal salario;

    @Enumerated(EnumType.STRING)
    @Column
    private TipoColaborador tipo;
}
