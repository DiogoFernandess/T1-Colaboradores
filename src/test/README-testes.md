# Convenção de testes — T1-Colaboradores

Stack: JUnit 5 (Jupiter 6.0.3) + Mockito 5.23.0 (via `spring-boot-starter-webmvc-test`) + JaCoCo.

## Regras obrigatórias para TODOS os testes

1. **`@DisplayName` em todo método de teste**, no formato:

   ```java
   @DisplayName("CT001 | RF001/RN001 | deve lancar ConflictException quando matricula ja existe")
   ```

   - `CTxxx` — número sequencial do caso de teste (CT001, CT002, …)
   - `RFxxx/RNxxx` — requisito funcional / regra de negócio coberta (ex.: `RF002/RN003`)
   - descrição em português, minúscula, sem ponto final

2. **Nome do método de teste em português**, padrão:

   ```
   deve_<resultado>_quando_<condicao>
   ```

   Exemplos:
   - `deve_lancarConflictException_quando_matriculaJaExiste`
   - `deve_retornarSalarioBase_quando_colaboradorPadrao`
   - `deve_lancarResourceNotFoundException_quando_matriculaNaoExiste`

3. `<resultado>` deve descrever o comportamento observado (exceção lançada, valor retornado, estado salvo), nunca apenas "testar".
4. `<condicao>` deve ser uma condição única e específica (um cenário por teste).
5. Mensagens de exceção devem ser verificadas com `assertThat(ex).hasMessageContaining(...)` (AssertJ) quando a mensagem for parte da especificação.

## Estrutura

```
src/test/java/colaboradores/requisitos/
├── service/    # testes unitários de ColaboradorService e PagamentoService (Mockito puro)
├── controller/ # testes de ColaboradorController e PagamentoController (MockMvc/@WebMvcTest)
└── ApplicationTests.java  # smoke test do contexto Spring (exige PostgreSQL em localhost:5432)
```

## Cobertura

Relatórios JaCoCo: `build/reports/jacoco/test/html/index.html` (HTML) e
`build/reports/jacoco/test/jacocoTestReport.xml` (XML).
A task `test` executa `jacocoTestReport` automaticamente ao final (`finalizedBy`).
