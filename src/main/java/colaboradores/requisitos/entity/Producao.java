package colaboradores.requisitos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Table(name = "producao")
@Entity
@Data
public class Producao {

    @Id
    @Column
    private String idMatricula;

    @Column
    @DecimalMin(value = "0.00", message = "A quantidade produzida não pode ser negativa")
    private int quantidadeProduzida;

    @Column(name = "valor")
    @DecimalMin(value = "0.00", message = "O valor por unidade não pode ser negativo")
    private BigDecimal valorUnidade;

    @Column(name = "total")
    private BigDecimal total;

    public String getIdMatricula() {
        return idMatricula;
    }

    public void setIdMatricula(String idMatricula) {
        this.idMatricula = idMatricula;
    }

    public int getQuantidadeProduzida() {
        return quantidadeProduzida;
    }

    public void setQuantidadeProduzida(int quantidadeProduzida) {
        this.quantidadeProduzida = quantidadeProduzida;
    }

    public BigDecimal getValorUnidade() {
        return valorUnidade;
    }

    public void setValorUnidade(BigDecimal valorUnidade) {
        this.valorUnidade = valorUnidade;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
