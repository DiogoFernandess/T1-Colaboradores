package colaboradores.requisitos.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Table(name = "comissao")
@Entity
@Data
public class Comissao {

    @Id
    @Column(name = "idMatricula")
    private String idMatricula;

    @Column
    private BigDecimal valorVendas;

    @Column
    private double porcentagem;

    @Column
    private BigDecimal comissao;

}
