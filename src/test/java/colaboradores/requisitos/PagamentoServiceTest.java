package colaboradores.requisitos;

import colaboradores.requisitos.dto.ColaboradorPagamentoDto;
import colaboradores.requisitos.dto.FolhaResumoDto;
import colaboradores.requisitos.entity.Colaborador;
import colaboradores.requisitos.entity.Comissao;
import colaboradores.requisitos.entity.Producao;
import colaboradores.requisitos.entity.TipoColaborador;
import colaboradores.requisitos.exception.ConflictException;
import colaboradores.requisitos.exception.ResourceNotFoundException;
import colaboradores.requisitos.repository.ColaboradorRepository;
import colaboradores.requisitos.repository.ComissaoRepository;
import colaboradores.requisitos.repository.ProducaoRepository;
import colaboradores.requisitos.service.ColaboradorService;
import colaboradores.requisitos.service.PagamentoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Testes unitarios de PagamentoService")
class PagamentoServiceTest {

    @Mock
    private ComissaoRepository comissaoRepository;

    @Mock
    private ProducaoRepository producaoRepository;

    @Mock
    private ColaboradorRepository colaboradorRepository;

    @Mock
    private ColaboradorService colaboradorService;

    @InjectMocks
    private PagamentoService pagamentoService;

    private static final String MATRICULA_PADRAO = "1001";
    private static final String MATRICULA_COMISSIONADO = "2001";
    private static final String MATRICULA_PRODUCAO = "3001";

    private Colaborador colaborador(String matricula, String nome, BigDecimal salario, TipoColaborador tipo) {
        Colaborador colaborador = new Colaborador();
        colaborador.setMatricula(matricula);
        colaborador.setNome(nome);
        colaborador.setSalario(salario);
        colaborador.setTipo(tipo);
        return colaborador;
    }

    private Colaborador colaboradorPadrao() {
        return colaborador(MATRICULA_PADRAO, "Ana Souza", new BigDecimal("2000.00"), TipoColaborador.PADRAO);
    }

    private Colaborador colaboradorComissionado() {
        return colaborador(MATRICULA_COMISSIONADO, "Bruno Lima", new BigDecimal("3000.00"), TipoColaborador.COMISSIONADO);
    }

    private Colaborador colaboradorProducao() {
        return colaborador(MATRICULA_PRODUCAO, "Carla Dias", new BigDecimal("1500.00"), TipoColaborador.PRODUCAO);
    }

    @Test
    @DisplayName("CT012 | RF003/RF006/RN008 | deve calcular comissao quando comissionado registra vendas e percentual")
    void deveCalcularComissao() {
        Comissao comissao = new Comissao();
        comissao.setIdMatricula(MATRICULA_COMISSIONADO);
        comissao.setValorVendas(new BigDecimal("10000.00"));
        comissao.setPorcentagem(10.0);
        // campo comissao NAO informado — deve ser calculado pelo sistema (RN008)

        when(colaboradorRepository.findTipoColaboradorByMatricula(MATRICULA_COMISSIONADO))
                .thenReturn(TipoColaborador.COMISSIONADO);
        when(comissaoRepository.save(any(Comissao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        pagamentoService.postComissao(comissao);

        ArgumentCaptor<Comissao> captor = ArgumentCaptor.forClass(Comissao.class);
        verify(comissaoRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getComissao())
                .isNotNull()
                .isEqualByComparingTo(new BigDecimal("1000.00"));
    }

    @Test
    @DisplayName("CT013 | RF004/RF006/RN008 | deve calcular total quando producao registra quantidade e valor por unidade")
    void deveCalcularTotalDeProducao() {
        Producao producao = new Producao();
        producao.setIdMatricula(MATRICULA_PRODUCAO);
        producao.setQuantidadeProduzida(100);
        producao.setValorUnidade(new BigDecimal("5.00"));
        // campo total NAO informado — deve ser calculado pelo sistema (RN008)

        when(colaboradorRepository.findTipoColaboradorByMatricula(MATRICULA_PRODUCAO))
                .thenReturn(TipoColaborador.PRODUCAO);
        when(producaoRepository.save(any(Producao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        pagamentoService.postProducao(producao);

        ArgumentCaptor<Producao> captor = ArgumentCaptor.forClass(Producao.class);
        verify(producaoRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getTotal())
                .isNotNull()
                .isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("CT014 | RF002/RF006/RF007 | deve retornar salario final igual a base quando colaborador e padrao")
    void deveRetornarSalarioFinalIgualBaseQuandoColaboradorEPadrao() {
        when(colaboradorRepository.findByMatricula(MATRICULA_PADRAO))
                .thenReturn(Optional.of(colaboradorPadrao()));

        ColaboradorPagamentoDto dto = pagamentoService.getFolhaByMatricula(MATRICULA_PADRAO);

        assertThat(dto.getMatricula()).isEqualTo(MATRICULA_PADRAO);
        assertThat(dto.getNome()).isEqualTo("Ana Souza");
        assertThat(dto.getTipoColaborador()).isEqualTo(TipoColaborador.PADRAO);
        assertThat(dto.getSalario()).isEqualByComparingTo(new BigDecimal("2000.00"));
        assertThat(dto.getAdicional()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(dto.getTotal()).isEqualByComparingTo(new BigDecimal("2000.00"));
    }

    @Test
    @DisplayName("CT015 | RF003/RF006/RF007 | deve somar comissao ao salario quando colaborador e comissionado")
    void deveSomarComissaoAoSalarioQuandoColaboradorEComissionado() {
        when(colaboradorRepository.findByMatricula(MATRICULA_COMISSIONADO))
                .thenReturn(Optional.of(colaboradorComissionado()));
        when(comissaoRepository.sumComissaoByMatricula(MATRICULA_COMISSIONADO))
                .thenReturn(new BigDecimal("1000.00"));

        ColaboradorPagamentoDto dto = pagamentoService.getFolhaByMatricula(MATRICULA_COMISSIONADO);

        assertThat(dto.getSalario()).isEqualByComparingTo(new BigDecimal("3000.00"));
        assertThat(dto.getAdicional()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(dto.getTotal()).isEqualByComparingTo(new BigDecimal("4000.00"));
    }

    @Test
    @DisplayName("CT016 | RF004/RF006/RF007 | deve somar produtividade ao salario quando colaborador e de producao")
    void deveSomarProdutividadeAoSalarioQuandoColaboradorEDeProducao() {
        when(colaboradorRepository.findByMatricula(MATRICULA_PRODUCAO))
                .thenReturn(Optional.of(colaboradorProducao()));
        when(producaoRepository.sumValorByColaboradorId(MATRICULA_PRODUCAO))
                .thenReturn(new BigDecimal("500.00"));

        ColaboradorPagamentoDto dto = pagamentoService.getFolhaByMatricula(MATRICULA_PRODUCAO);

        assertThat(dto.getSalario()).isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(dto.getAdicional()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(dto.getTotal()).isEqualByComparingTo(new BigDecimal("2000.00"));
    }

    @Test
    @DisplayName("CT017 | RF007 | deve lancar ResourceNotFoundException exatamente quando matricula nao existe")
    void deveLancarExceptionQuandoMatriculaNaoExiste() {
        when(colaboradorRepository.findByMatricula("9999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pagamentoService.getFolhaByMatricula("9999"))
                .isExactlyInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("CT018 | RF007 | deve retornar folha completa quando existem colaboradores dos tres tipos")
    void deveRetornarFolhaCompleta() {
        when(colaboradorRepository.findAll()).thenReturn(List.of(
                colaboradorPadrao(),
                colaboradorComissionado(),
                colaboradorProducao()
        ));
        when(comissaoRepository.sumComissaoByMatricula(MATRICULA_COMISSIONADO))
                .thenReturn(new BigDecimal("1000.00"));
        when(producaoRepository.sumValorByColaboradorId(MATRICULA_PRODUCAO))
                .thenReturn(new BigDecimal("500.00"));

        List<ColaboradorPagamentoDto> folha = pagamentoService.folha();

        assertThat(folha).hasSize(3);
        assertThat(folha).extracting(ColaboradorPagamentoDto::getMatricula)
                .containsExactly(MATRICULA_PADRAO, MATRICULA_COMISSIONADO, MATRICULA_PRODUCAO);
        assertThat(folha).extracting(ColaboradorPagamentoDto::getNome)
                .containsExactly("Ana Souza", "Bruno Lima", "Carla Dias");
        assertThat(folha).extracting(ColaboradorPagamentoDto::getTipoColaborador)
                .containsExactly(TipoColaborador.PADRAO, TipoColaborador.COMISSIONADO, TipoColaborador.PRODUCAO);
        assertThat(folha.get(0).getTotal()).isEqualByComparingTo(new BigDecimal("2000.00"));
        assertThat(folha.get(1).getTotal()).isEqualByComparingTo(new BigDecimal("4000.00"));
        assertThat(folha.get(2).getTotal()).isEqualByComparingTo(new BigDecimal("2000.00"));
    }

    @Test
    @DisplayName("CT019 | RF003 | deve lancar ConflictException quando comissao e registrada para colaborador padrao")
    void deveLancarExceptionQuandoComissaoERegistradaParaColaboradorPadrao() {
        Comissao comissao = new Comissao();
        comissao.setIdMatricula(MATRICULA_PADRAO);
        comissao.setValorVendas(new BigDecimal("10000.00"));
        comissao.setPorcentagem(10.0);

        when(colaboradorRepository.findTipoColaboradorByMatricula(MATRICULA_PADRAO))
                .thenReturn(TipoColaborador.PADRAO);

        assertThatThrownBy(() -> pagamentoService.postComissao(comissao))
                .isInstanceOf(ConflictException.class);

        verify(comissaoRepository, never()).save(any(Comissao.class));
    }

    @Test
    @DisplayName("CT020 | RF004 | deve lancar ConflictException quando producao e registrada para comissionado")
    void deveLancarExceptionQuandoProducaoERegistradaParaComissionado() {
        Producao producao = new Producao();
        producao.setIdMatricula(MATRICULA_COMISSIONADO);
        producao.setQuantidadeProduzida(100);
        producao.setValorUnidade(new BigDecimal("5.00"));

        when(colaboradorRepository.findTipoColaboradorByMatricula(MATRICULA_COMISSIONADO))
                .thenReturn(TipoColaborador.COMISSIONADO);

        assertThatThrownBy(() -> pagamentoService.postProducao(producao))
                .isInstanceOf(ConflictException.class);

        verify(producaoRepository, never()).save(any(Producao.class));
    }

    @Test
    @DisplayName("CT021 | RN004 | deve lancar excecao quando vendas sao negativas")
    void deveLancarExcecaoQuandoVendasSaoNegativas() {
        Comissao comissao = new Comissao();
        comissao.setIdMatricula(MATRICULA_COMISSIONADO);
        comissao.setValorVendas(new BigDecimal("-100.00"));
        comissao.setPorcentagem(10.0);

        when(colaboradorRepository.findTipoColaboradorByMatricula(MATRICULA_COMISSIONADO))
                .thenReturn(TipoColaborador.COMISSIONADO);

        assertThatThrownBy(() -> pagamentoService.postComissao(comissao))
                .isInstanceOf(RuntimeException.class);

        verify(comissaoRepository, never()).save(any(Comissao.class));
    }

    @Test
    @DisplayName("CT022 | RN005 | deve lancar excecao quando percentual e negativo")
    void deveLancarExcecaoQuandoPercentualENegativo() {
        Comissao comissao = new Comissao();
        comissao.setIdMatricula(MATRICULA_COMISSIONADO);
        comissao.setValorVendas(new BigDecimal("10000.00"));
        comissao.setPorcentagem(-5.0);

        when(colaboradorRepository.findTipoColaboradorByMatricula(MATRICULA_COMISSIONADO))
                .thenReturn(TipoColaborador.COMISSIONADO);

        assertThatThrownBy(() -> pagamentoService.postComissao(comissao))
                .isInstanceOf(RuntimeException.class);

        verify(comissaoRepository, never()).save(any(Comissao.class));
    }

    @Test
    @DisplayName("CT023 | RN006 | deve lancar excecao quando quantidade produzida e negativa")
    void deveLancarExcecaoQuandoQuantidadeProduzidaENegativa() {
        Producao producao = new Producao();
        producao.setIdMatricula(MATRICULA_PRODUCAO);
        producao.setQuantidadeProduzida(-1);
        producao.setValorUnidade(new BigDecimal("5.00"));

        when(colaboradorRepository.findTipoColaboradorByMatricula(MATRICULA_PRODUCAO))
                .thenReturn(TipoColaborador.PRODUCAO);

        assertThatThrownBy(() -> pagamentoService.postProducao(producao))
                .isInstanceOf(RuntimeException.class);

        verify(producaoRepository, never()).save(any(Producao.class));
    }

    @Test
    @DisplayName("CT024 | RN007 | deve lancar excecao quando valor por unidade e negativo")
    void deveLancarExcecaoQuandoValorPorUnidadeENegativo() {
        Producao producao = new Producao();
        producao.setIdMatricula(MATRICULA_PRODUCAO);
        producao.setQuantidadeProduzida(100);
        producao.setValorUnidade(new BigDecimal("-0.01"));

        when(colaboradorRepository.findTipoColaboradorByMatricula(MATRICULA_PRODUCAO))
                .thenReturn(TipoColaborador.PRODUCAO);

        assertThatThrownBy(() -> pagamentoService.postProducao(producao))
                .isInstanceOf(RuntimeException.class);

        verify(producaoRepository, never()).save(any(Producao.class));
    }

    @Test
    @DisplayName("CT025 | RF008 | deve retornar resumo com totais por categoria quando folha tem colaboradores dos tres tipos")
    void deveRetornarResumoComTotalPorCategoria() {
        when(colaboradorRepository.count()).thenReturn(3L);
        when(colaboradorRepository.countByTipo(TipoColaborador.PADRAO)).thenReturn(1);
        when(colaboradorRepository.countByTipo(TipoColaborador.COMISSIONADO)).thenReturn(1);
        when(colaboradorRepository.countByTipo(TipoColaborador.PRODUCAO)).thenReturn(1);
        when(colaboradorRepository.sumSalarioByTipo(TipoColaborador.PADRAO))
                .thenReturn(new BigDecimal("2000.00"));
        when(colaboradorRepository.sumSalarioByTipo(TipoColaborador.COMISSIONADO))
                .thenReturn(new BigDecimal("3000.00"));
        when(colaboradorRepository.sumSalarioByTipo(TipoColaborador.PRODUCAO))
                .thenReturn(new BigDecimal("1500.00"));
        when(comissaoRepository.sumTotalComissoes()).thenReturn(new BigDecimal("1000.00"));
        when(producaoRepository.sumTotalProducao()).thenReturn(new BigDecimal("500.00"));

        FolhaResumoDto resumo = pagamentoService.folhaResumo();

        assertThat(resumo.getColaboradores()).isEqualTo(3);
        assertThat(resumo.getColaboradoresPadrao()).isEqualTo(1);
        assertThat(resumo.getColaboradoresComissionados()).isEqualTo(1);
        assertThat(resumo.getColaboradoresProducao()).isEqualTo(1);
        assertThat(resumo.getTotalPagamentoPadrao()).isEqualByComparingTo(new BigDecimal("2000.00"));
        assertThat(resumo.getTotalPagamentoComissao()).isEqualByComparingTo(new BigDecimal("4000.00"));
        assertThat(resumo.getTotalPagamentoProducao()).isEqualByComparingTo(new BigDecimal("2000.00"));
        assertThat(resumo.getPagamentoTotal()).isEqualByComparingTo(new BigDecimal("8000.00"));
    }

    @Test
    @DisplayName("CT026 | RF008 | deve retornar resumo zerado sem lancar excecao quando nao ha colaboradores")
    void deveRetornarResumoZerado() {
        when(colaboradorRepository.count()).thenReturn(0L);
        when(colaboradorRepository.countByTipo(any(TipoColaborador.class))).thenReturn(0);
        when(colaboradorRepository.sumSalarioByTipo(any(TipoColaborador.class))).thenReturn(null);
        when(comissaoRepository.sumTotalComissoes()).thenReturn(null);
        when(producaoRepository.sumTotalProducao()).thenReturn(null);

        FolhaResumoDto resumo = assertDoesNotThrow(() -> pagamentoService.folhaResumo());

        assertThat(resumo.getColaboradores()).isZero();
        assertThat(resumo.getColaboradoresPadrao()).isZero();
        assertThat(resumo.getColaboradoresComissionados()).isZero();
        assertThat(resumo.getColaboradoresProducao()).isZero();
        assertThat(resumo.getTotalPagamentoPadrao()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resumo.getTotalPagamentoComissao()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resumo.getTotalPagamentoProducao()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resumo.getPagamentoTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
