package colaboradores.requisitos.service;

import colaboradores.requisitos.entity.Colaborador;
import colaboradores.requisitos.entity.TipoColaborador;
import colaboradores.requisitos.exception.ConflictException;
import colaboradores.requisitos.exception.ResourceNotFoundException;
import colaboradores.requisitos.repository.ColaboradorRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Testes unitarios de ColaboradorService")
class ColaboradorServiceTest {

    @Mock
    private ColaboradorRepository colaboradorRepository;

    @InjectMocks
    private ColaboradorService colaboradorService;

    private static final String MATRICULA = "1001";
    private static final String NOME = "Ana Souza";
    private static final BigDecimal SALARIO = new BigDecimal("2000.00");

    private Colaborador colaboradorPadrao() {
        return colaborador(MATRICULA, NOME, SALARIO, TipoColaborador.PADRAO);
    }

    private Colaborador colaborador(String matricula, String nome, BigDecimal salario, TipoColaborador tipo) {
        Colaborador colaborador = new Colaborador();
        colaborador.setMatricula(matricula);
        colaborador.setNome(nome);
        colaborador.setSalario(salario);
        colaborador.setTipo(tipo);
        return colaborador;
    }

    @Test
    @DisplayName("CT001 | RF001/RF002 | deve salvar colaborador quando matricula ainda nao existe")
    void deve_salvarColaborador_quando_matriculaAindaNaoExiste() {
        Colaborador colaborador = colaboradorPadrao();
        when(colaboradorRepository.existsByMatricula(MATRICULA)).thenReturn(false);
        when(colaboradorRepository.save(colaborador)).thenReturn(colaborador);

        Colaborador retorno = colaboradorService.cadastroColaborador(colaborador);

        assertThat(retorno).isEqualTo(colaborador);
        assertThat(retorno.getMatricula()).isEqualTo(MATRICULA);
        assertThat(retorno.getNome()).isEqualTo(NOME);
        assertThat(retorno.getSalario()).isEqualByComparingTo(SALARIO);
        assertThat(retorno.getTipo()).isEqualTo(TipoColaborador.PADRAO);
        verify(colaboradorRepository, times(1)).save(colaborador);
    }

    @Test
    @DisplayName("CT002 | RN001 | deve lancar ConflictException quando matricula ja existe")
    void deve_lancarConflictException_quando_matriculaJaExiste() {
        Colaborador colaborador = colaboradorPadrao();
        when(colaboradorRepository.existsByMatricula(MATRICULA)).thenReturn(true);

        assertThatThrownBy(() -> colaboradorService.cadastroColaborador(colaborador))
                .isInstanceOf(ConflictException.class);

        verify(colaboradorRepository, never()).save(any(Colaborador.class));
    }

    @Test
    @DisplayName("CT003 | RN003 | deve aceitar cadastro quando salario base e zero")
    void deve_aceitarCadastro_quando_salarioBaseEZero() {
        Colaborador colaborador = colaborador(MATRICULA, NOME, new BigDecimal("0.00"), TipoColaborador.PADRAO);
        when(colaboradorRepository.existsByMatricula(MATRICULA)).thenReturn(false);
        when(colaboradorRepository.save(colaborador)).thenReturn(colaborador);

        Colaborador retorno = colaboradorService.cadastroColaborador(colaborador);

        assertThat(retorno).isEqualTo(colaborador);
        verify(colaboradorRepository, times(1)).save(colaborador);
    }

    @ParameterizedTest(name = "nome = [{index}] ''{0}''")
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    @DisplayName("CT004 | RN002 | deve lancar excecao quando nome esta em branco")
    void deve_lancarExcecao_quando_nomeEstaEmBranco(String nome) {
        Colaborador colaborador = colaborador(MATRICULA, nome, SALARIO, TipoColaborador.PADRAO);
        when(colaboradorRepository.existsByMatricula(MATRICULA)).thenReturn(false);

        assertThatThrownBy(() -> colaboradorService.cadastroColaborador(colaborador))
                .isInstanceOf(RuntimeException.class);

        verify(colaboradorRepository, never()).save(any(Colaborador.class));
    }

    @Test
    @DisplayName("CT005 | RN003 | deve lancar excecao quando salario base e negativo")
    void deve_lancarExcecao_quando_salarioBaseENegativo() {
        Colaborador colaborador = colaborador(MATRICULA, NOME, new BigDecimal("-1.00"), TipoColaborador.PADRAO);
        when(colaboradorRepository.existsByMatricula(MATRICULA)).thenReturn(false);

        assertThatThrownBy(() -> colaboradorService.cadastroColaborador(colaborador))
                .isInstanceOf(RuntimeException.class);

        verify(colaboradorRepository, never()).save(any(Colaborador.class));
    }

    @Test
    @DisplayName("CT006 | RF005 | deve retornar todos os colaboradores quando existem cadastrados")
    void deve_retornarTodosOsColaboradores_quando_existemCadastrados() {
        List<Colaborador> colaboradores = List.of(
                colaborador("1001", "Ana Souza", new BigDecimal("2000.00"), TipoColaborador.PADRAO),
                colaborador("1002", "Bruno Lima", new BigDecimal("3000.00"), TipoColaborador.COMISSIONADO),
                colaborador("1003", "Carla Dias", new BigDecimal("4000.00"), TipoColaborador.PRODUCAO)
        );
        when(colaboradorRepository.findAll()).thenReturn(colaboradores);

        List<Colaborador> retorno = colaboradorService.getAllColaboradores();

        assertThat(retorno).hasSize(3).isEqualTo(colaboradores);
        assertThat(retorno)
                .extracting(Colaborador::getMatricula, Colaborador::getNome, Colaborador::getTipo)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("1001", "Ana Souza", TipoColaborador.PADRAO),
                        org.assertj.core.groups.Tuple.tuple("1002", "Bruno Lima", TipoColaborador.COMISSIONADO),
                        org.assertj.core.groups.Tuple.tuple("1003", "Carla Dias", TipoColaborador.PRODUCAO)
                );
    }

    @Test
    @DisplayName("CT007 | RF005/RF009 | deve retornar dados do colaborador quando matricula existe")
    void deve_retornarDadosDoColaborador_quando_matriculaExiste() {
        Colaborador colaborador = colaboradorPadrao();
        when(colaboradorRepository.findByMatricula(MATRICULA)).thenReturn(Optional.of(colaborador));

        Colaborador retorno = colaboradorService.findColaboradorByMatricula(MATRICULA);

        assertThat(retorno).isEqualTo(colaborador);
        assertThat(retorno.getMatricula()).isEqualTo(MATRICULA);
        assertThat(retorno.getNome()).isEqualTo(NOME);
        assertThat(retorno.getSalario()).isEqualByComparingTo(SALARIO);
        assertThat(retorno.getTipo()).isEqualTo(TipoColaborador.PADRAO);
    }

    @Test
    @DisplayName("CT008 | RF005/RF009 | deve lancar ResourceNotFoundException quando matricula nao existe")
    void deve_lancarResourceNotFoundException_quando_matriculaNaoExiste() {
        when(colaboradorRepository.findByMatricula("9999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> colaboradorService.findColaboradorByMatricula("9999"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("CT009 | RF009 | deve excluir colaborador quando matricula existe")
    void deve_excluirColaborador_quando_matriculaExiste() {
        when(colaboradorRepository.existsByMatricula(MATRICULA)).thenReturn(true);

        colaboradorService.deleteColaboradorByMatricula(MATRICULA);

        verify(colaboradorRepository, times(1)).deleteByMatricula(MATRICULA);
    }

    @Test
    @DisplayName("CT010 | RF009 | deve lancar ResourceNotFoundException quando matricula nao existe e exclusao e solicitada")
    void deve_lancarResourceNotFoundException_quando_matriculaNaoExisteEExclusaoESolicitada() {
        when(colaboradorRepository.existsByMatricula("9999")).thenReturn(false);

        assertThatThrownBy(() -> colaboradorService.deleteColaboradorByMatricula("9999"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(colaboradorRepository, never()).deleteByMatricula(anyString());
    }

    @Test
    @Disabled("RF009 - alteração não implementada")
    @DisplayName("CT011 | RF009 | deve atualizar nome e salario quando colaborador existe")
    void deve_atualizarNomeESalario_quando_colaboradorExiste() {
        // Intencao (RF009): alterar o colaborador de matricula 1001 existente,
        // mudando o nome para "Ana Souza Lima" e o salario para 2500.00.
        // Bloqueado: ColaboradorService nao possui metodo de alteracao (RF009 incompleta em src/main),
        // entao o teste fica desabilitado ate que a regra seja implementada.
    }
}
