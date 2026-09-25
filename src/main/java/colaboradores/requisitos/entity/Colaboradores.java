package colaboradores.requisitos.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Table(name = "colaboradores")
@Entity
@Data
public class Colaboradores {

    @Id
    @Column
    private String matricula;

    @Column
    private String nome;

    @Column
    private BigDecimal salario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoColaborador tipoColaborador;
}
