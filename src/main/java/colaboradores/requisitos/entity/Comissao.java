package colaboradores.requisitos.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Table(name = "comissao")
@Entity
@Data
public class Comissao {

    @Id
    @Column
    private String id_colaborador;

    @Column
    private BigDecimal salario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoColaborador tipoColaborador;
}
