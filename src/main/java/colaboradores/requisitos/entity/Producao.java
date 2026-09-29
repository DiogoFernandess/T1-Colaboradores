package colaboradores.requisitos.entity;

import jakarta.persistence.Column;

import java.math.BigDecimal;

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
