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

    public String getIdMatricula() {
        return idMatricula;
    }

    public void setIdMatricula(String idMatricula) {
        this.idMatricula = idMatricula;
    }

    public BigDecimal getValorVendas() {
        return valorVendas;
    }

    public void setValorVendas(BigDecimal valorVendas) {
        this.valorVendas = valorVendas;
    }

    public double getPorcentagem() {
        return porcentagem;
    }

    public void setPorcentagem(double porcentagem) {
        this.porcentagem = porcentagem;
    }

    public BigDecimal getComissao() {
        return comissao;
    }

    public void setComissao(BigDecimal comissao) {
        this.comissao = comissao;
    }
}
