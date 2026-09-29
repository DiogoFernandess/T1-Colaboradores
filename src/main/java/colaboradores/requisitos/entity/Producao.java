package colaboradores.requisitos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Table(name = "producao")
@Entity
@Data
public class Producao {

    @Column
    private String idMatricula;

    @Column
    private int quantidadeProduzida;

    @Column(name = "valor")
    private BigDecimal valorUnidade;

    @Column(name = "total")
    private BigDecimal total;

}
