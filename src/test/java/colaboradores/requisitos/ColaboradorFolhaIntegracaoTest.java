package colaboradores.requisitos;

import colaboradores.requisitos.repository.ColaboradorRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Testes de integracao de colaborador e folha (API HTTP)")
class ColaboradorFolhaIntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void limparDados() {
        colaboradorRepository.deleteByMatricula("7001");
        colaboradorRepository.deleteByMatricula("7002");
    }

    @Test
    @DisplayName("CT027 | RF001/RNF003 | deve cadastrar e consultar colaborador quando dados sao validos")
    void deveCadastrarEConsultarColaboradorQuandoDadosSaoValidos() throws Exception {
        String jsonCadastro = """
                {
                    "matricula": "7001",
                    "nome": "Ana Souza",
                    "salario": 2000.00,
                    "tipo": "PADRAO"
                }
                """;

        MvcResult resultadoCadastro = mockMvc.perform(post("/colaboradores/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCadastro))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        MvcResult resultadoConsulta = mockMvc.perform(get("/colaboradores/{matricula}", "7001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value("7001"))
                .andExpect(jsonPath("$.nome").value("Ana Souza"))
                .andExpect(jsonPath("$.tipo").value("PADRAO"))
                .andReturn();

        String corpoConsulta = resultadoConsulta.getResponse().getContentAsString();
        JsonNode no = objectMapper.readTree(corpoConsulta);
        assertThat(new BigDecimal(no.get("salario").asText()))
                .isEqualByComparingTo(new BigDecimal("2000.00"));
    }

    @Test
    @DisplayName("CT028 | RF003/RF006/RF007 | deve calcular folha do comissionado quando vendas e percentual registrados")
    void deveCalcularFolhaDoComissionadoQuando_vendasEPercentualRegistrados() throws Exception {
        String jsonCadastro = """
                {
                    "matricula": "7002",
                    "nome": "Bruno Lima",
                    "salario": 3000.00,
                    "tipo": "COMISSIONADO"
                }
                """;

        mockMvc.perform(post("/colaboradores/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCadastro))
                .andExpect(status().is2xxSuccessful());

        String jsonComissao = """
                {
                    "idMatricula": "7002",
                    "valorVendas": 10000.00,
                    "porcentagem": 10.0
                }
                """;

        mockMvc.perform(post("/pagamento/comissao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonComissao))
                .andExpect(status().is2xxSuccessful());

        MvcResult resultadoFolha = mockMvc.perform(get("/pagamento/folha/{matricula}", "7002"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.matricula").value("7002"))
                .andExpect(jsonPath("$.nome").value("Bruno Lima"))
                .andExpect(jsonPath("$.tipoColaborador").value("COMISSIONADO"))
                .andReturn();

        String corpoFolha = resultadoFolha.getResponse().getContentAsString();
        JsonNode no = objectMapper.readTree(corpoFolha);
        assertThat(new BigDecimal(no.get("salario").asText()))
                .isEqualByComparingTo(new BigDecimal("3000.00"));
        assertThat(new BigDecimal(no.get("adicional").asText()))
                .isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(new BigDecimal(no.get("total").asText()))
                .isEqualByComparingTo(new BigDecimal("4000.00"));
    }
}
