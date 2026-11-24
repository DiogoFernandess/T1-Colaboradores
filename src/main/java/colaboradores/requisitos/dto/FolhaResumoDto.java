package colaboradores.requisitos.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class FolhaResumoDto {

    private long colaboradores;

    private int colaboradoresComissionados;

    private int colaboradoresProducao;

    private int colaboradoresPadrao;

    private BigDecimal totalPagamentoComissao;

    private BigDecimal totalPagamentoProducao;

    private BigDecimal totalPagamentoPadrao;

    private BigDecimal pagamentoTotal;

    public long getColaboradores() {
        return colaboradores;
    }

    public void setColaboradores(long colaboradores) {
        this.colaboradores = colaboradores;
    }

    public int getColaboradoresComissionados() {
        return colaboradoresComissionados;
    }

    public void setColaboradoresComissionados(int colaboradoresComissionados) {
        this.colaboradoresComissionados = colaboradoresComissionados;
    }

    public int getColaboradoresProducao() {
        return colaboradoresProducao;
    }

    public void setColaboradoresProducao(int colaboradoresProducao) {
        this.colaboradoresProducao = colaboradoresProducao;
    }

    public int getColaboradoresPadrao() {
        return colaboradoresPadrao;
    }

    public void setColaboradoresPadrao(int colaboradoresPadrao) {
        this.colaboradoresPadrao = colaboradoresPadrao;
    }

    public BigDecimal getTotalPagamentoComissao() {
        return totalPagamentoComissao;
    }

    public void setTotalPagamentoComissao(BigDecimal totalPagamentoComissao) {
        this.totalPagamentoComissao = totalPagamentoComissao;
    }

    public BigDecimal getTotalPagamentoProducao() {
        return totalPagamentoProducao;
    }

    public void setTotalPagamentoProducao(BigDecimal totalPagamentoProducao) {
        this.totalPagamentoProducao = totalPagamentoProducao;
    }

    public BigDecimal getTotalPagamentoPadrao() {
        return totalPagamentoPadrao;
    }

    public void setTotalPagamentoPadrao(BigDecimal totalPagamentoPadrao) {
        this.totalPagamentoPadrao = totalPagamentoPadrao;
    }

    public BigDecimal getPagamentoTotal() {
        return pagamentoTotal;
    }

    public void setPagamentoTotal(BigDecimal pagamentoTotal) {
        this.pagamentoTotal = pagamentoTotal;
    }
}
