package colaboradores.requisitos.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class FolhaResumo {

    private int colaboradores;

    private int colaboradoresComissionados;

    private int colaboradoresProducao;

    private int colaboradoresPadrao;

    private BigDecimal valorComissao;

    private BigDecimal valorProducao;

    private BigDecimal valorPadrao;

    private BigDecimal valorTotal;
}
