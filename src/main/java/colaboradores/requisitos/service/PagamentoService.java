package colaboradores.requisitos.service;

import colaboradores.requisitos.entity.*;
import colaboradores.requisitos.exception.ConflictException;
import colaboradores.requisitos.repository.ColaboradorRepository;
import colaboradores.requisitos.repository.ComissaoRepository;
import colaboradores.requisitos.repository.ProducaoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static colaboradores.requisitos.entity.TipoColaborador.*;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    @Autowired
    ComissaoRepository comissaoRepository;

    @Autowired
    ProducaoRepository producaoRepository;

    @Autowired
    ColaboradorService colaboradorService;

    @Autowired
    ColaboradorRepository colaboradorRepository;

    public Comissao postComissao(Comissao comissao){

        TipoColaborador tipo = tipoColaborador(comissao.getIdMatricula());

        if (tipo != COMISSIONADO) {

            throw new ConflictException("Colaborador não pertence a esta categoria");
        }

        return comissaoRepository.save(comissao);
    }

    public Producao postProducao(Producao producao){

        TipoColaborador tipo = tipoColaborador(producao.getIdMatricula());

        if (tipo != PRODUCAO) {

                throw new ConflictException("Colaborador não pertence a esta categoria");
        }

        return producaoRepository.save(producao);
    }

    public TipoColaborador tipoColaborador(String matricula){

        colaboradorService.matriculaNoExist(matricula);

        return colaboradorRepository.findTipoColaborador(matricula);
    }

    public List<ColaboradorPagamento> folha (){

        return colaboradorRepository.findAll()
                .stream()
                .map(this::montarFolhaPagamento) // Reutiliza a mesma função para todos
                .collect(Collectors.toList());
    }

    public FolhaResumo folhaResumo(){

        FolhaResumo resumo = new FolhaResumo();

        resumo.setColaboradores(colaboradorRepository.count());
        resumo.setColaboradoresPadrao(colaboradorRepository.countByTipo(PADRAO));
        resumo.setColaboradoresComissionados(colaboradorRepository.countByTipo(COMISSIONADO));
        resumo.setColaboradoresProducao(colaboradorRepository.countByTipo(PRODUCAO));

        BigDecimal somaSalariosPadrao = colaboradorRepository.sumSalarioByTipo(PADRAO);
        BigDecimal somaSalariosComissao = colaboradorRepository.sumSalarioByTipo(COMISSIONADO)
                        .add(comissaoRepository.sumTotalComissoes());
        BigDecimal somaSalariosProducao = colaboradorRepository.sumSalarioByTipo(PRODUCAO)
                        .add(producaoRepository.sumTotalProducao());

        resumo.setTotalPagamentoPadrao(somaSalariosPadrao);
        resumo.setTotalPagamentoComissao(somaSalariosComissao);
        resumo.setTotalPagamentoProducao(somaSalariosProducao);

        BigDecimal totalGeralColaboradores = somaSalariosPadrao
                .add(somaSalariosComissao)
                .add(somaSalariosProducao);

        resumo.setPagamentoTotal(totalGeralColaboradores);

        return resumo;
    }

    public ColaboradorPagamento getFolhaByMatricula(String matricula){

        Colaborador colaborador = colaboradorRepository.findByMatricula(matricula)
                .orElseThrow(() -> new RuntimeException("Colaborador não encontrado com a matrícula: " + matricula));

        return montarFolhaPagamento(colaborador);
    }

    private ColaboradorPagamento montarFolhaPagamento(Colaborador colaborador) {

        BigDecimal salario = Objects.requireNonNullElse(colaborador.getSalario(), BigDecimal.ZERO);

        BigDecimal adicional = calcularAdicional(colaborador);

        BigDecimal total = salario.add(adicional);

        return ColaboradorPagamento.builder()
                .matricula(colaborador.getMatricula())
                .nome(colaborador.getNome())
                .tipoColaborador(colaborador.getTipoColaborador())
                .salario(salario)
                .adicional(adicional)
                .total(total)
                .build();
    }

    private BigDecimal calcularAdicional(Colaborador colaborador) {
        if (colaborador.getTipoColaborador() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal adicional = switch (colaborador.getTipoColaborador()) {
            case COMISSIONADO -> comissaoRepository.sumValorByColaboradorId(colaborador.getMatricula());
            case PRODUCAO -> producaoRepository.sumValorByColaboradorId(colaborador.getMatricula());
            case PADRAO -> BigDecimal.ZERO;
        };

        return Objects.requireNonNullElse(adicional, BigDecimal.ZERO);
    }
}
