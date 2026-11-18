package colaboradores.requisitos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
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
    @DecimalMin(value = "0.00", message = "O valor da venda não pode ser negativo")
    private BigDecimal valorVendas;

    @Column
    @DecimalMin(value = "0.00", message = "A porcentagem não pode ser negativa")
    private double porcentagem;

    @Column
    private BigDecimal comissao;

}
