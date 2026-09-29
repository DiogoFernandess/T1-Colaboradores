package colaboradores.requisitos.entity;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class FolhaResumo {

    private long colaboradores;

    private int colaboradoresComissionados;

    private int colaboradoresProducao;

    private int colaboradoresPadrao;

    private BigDecimal totalPagamentoComissao;

    private BigDecimal totalPagamentoProducao;

    private BigDecimal totalPagamentoPadrao;

    private BigDecimal pagamentoTotal;

}
