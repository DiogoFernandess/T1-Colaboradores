# 04 — Testes de Sistema (Nível 3) — Sistema de Folha de Pagamento

## 1. Identificação do que foi testado

| Item | Valor |
|---|---|
| Branch | `caso-de-testes-sistema-aceitacao` |
| Commit | `76aa874` |
| Casos | CT029 (RF001 a RF008) e CT030 (RNF003), mais uma sonda de JSON (não é caso) |
| Classe de testes | `src/test/java/colaboradores/requisitos/SistemaFolhaPagamentoTest.java` (novo) |
| Execução | `cmd.exe /c gradlew.bat testeSistema` — 3 execuções de teste: 1 falha (CT029, reprovado), 2 passam (CT030, sonda) |

O teste é de nível 3 de verdade: aplicação Spring Boot real (Tomcat em porta aleatória), HTTP real (`java.net.http.HttpClient`) e PostgreSQL real (processo próprio, sem Docker, sem H2, sem mocks).

## 2. Ambiente

| Componente | Versão / detalhe |
|---|---|
| Sistema | Windows 11 (+ WSL2). Gradle executado pelo lado Windows: `cmd.exe /c gradlew.bat ...` |
| Java | Toolchain 21 (JDK 21.0.12 executando os testes) |
| Spring Boot | 4.1.1 |
| Gradle | 9.7.1 |
| Banco de teste | PostgreSQL 17.5 **real**, subido como processo próprio via `io.zonky.test:embedded-postgres:2.1.0` + BOM de binários `embedded-postgres-binaries-bom:17.5.0` |
| Porta do banco | Aleatória (ex.: 50613) — nunca 5432 |
| Banco criado | `folhapagamento_teste` (criado via `CREATE DATABASE` no PostgreSQL embutido) |
| Vida útil | Diretório temporário descartado ao final (`[SISTEMA] PostgreSQL embutido encerrado e diretorio temporario descartado`, PG loga `servidor está parado`) |
| Porta da aplicação | Aleatória (ex.: 50940, 52548) — a 8081 do `application.properties` não é usada |

### Prova de isolamento (pelo log)

- `[SISTEMA] URL do banco de teste (PostgreSQL embutido, processo proprio): jdbc:postgresql://localhost:50613/folhapagamento_teste`
- Hibernate, ao conectar: `Database JDBC URL [jdbc:postgresql://localhost:50613/folhapagamento_teste]`, `Database version: 17.5`
- Dentro dos testes, o log confirma de qual servidor se trata: `[CT029] banco conectado: folhapagamento_teste @ porta 50613`
- A classe **falha na hora** se a URL contiver `:5432/` ou terminar em `/postgres` (verificação em `garantirBancoDeTeste()`, executada no `@BeforeAll`).
- O PostgreSQL 18 do Windows (5432) não foi usado, não foi tocado e nenhum dado de desenvolvimento foi lido ou alterado.
- Tabelas `colaboradores`, `comissao` e `producao` são limpas antes de cada caso (`@BeforeEach`), para um caso não depender do outro.

## 3. O que foi adicionado ao build (`build.gradle`)

Adições (o plugin JaCoCo e a configuração existente foram preservados; `src/main` intocado; nenhum teste existente alterado):

```groovy
// dependências de teste adicionadas
testImplementation platform('io.zonky.test.postgres:embedded-postgres-binaries-bom:17.5.0')
testImplementation 'io.zonky.test:embedded-postgres:2.1.0'
testImplementation 'com.fasterxml.jackson.core:jackson-databind:2.18.3'   // leitura das respostas JSON

// tarefa test passa a excluir as tags de sistema/aceitação
tasks.named('test') {
    useJUnitPlatform { excludeTags 'sistema', 'aceitacao' }
    // ... configuração JaCoCo existente, preservada
}

// nova tarefa separada para os testes de sistema
tasks.register('testeSistema', Test) {
    useJUnitPlatform { includeTags 'sistema' }
    // classes/classpath da sourceSets.test; imprime stdout (evidências dos testes)
}
```

Confirmado por execução: `gradlew.bat test` **não** executa os testes de sistema (não há CT029/CT030/sonda na saída e o PostgreSQL embutido não sobe).

## 4. Resultado dos casos

| Código | Requisito | Resultado esperado | Resultado obtido | Situação | Defeito |
|---|---|---|---|---|---|
| CT029 | RF001 a RF008 | Folhas individuais 2000.00 / 4000.00 / 2000.00; folha geral com 3 registros e os mesmos totais; resumo com 3 colaboradores e total geral 8000.00 | Cadastros: 3× HTTP 200 e banco correto (1001 Ana/2000.00/PADRAO, 2001 Bruno/3000.00/COMISSIONADO, 3001 Carla/1500.00/PRODUCAO). `POST /pagamento/comissao` → **HTTP 500** (ClassCastException); `POST /pagamento/producao` → **HTTP 500** (mesma causa); banco com `comissao []` e `producao []`. Folha 1001 = 2000.00 (ok). Folha 2001 = **3000.0**, adicional **0** (esperado 4000.00 / 1000.00). Folha 3001 = **1500.0**, adicional **0** (esperado 2000.00 / 500.00). Folha geral com 3 registros, totais 2000.0 / 3000.0 / 1500.0. `GET /pagamento/resumo` → **HTTP 500** (NPE — BUG010 com base vazia; a base está vazia por causa do BUG014) | **Reprovado** | BUG014 (causa raiz); observação: BUG013 e BUG010 |
| CT030 | RNF003 | Os mesmos colaboradores e os mesmos valores continuam lá após reiniciar a aplicação | Cadastros: 3× HTTP 200. `POST /pagamento/comissao` e `/producao` → 500 (BUG014); como o objetivo do caso é a persistência (não os endpoints de registro), comissão (2001: 10000.00, 10, **1000.00**) e produção (3001: 100, 5.00, **500.00**) foram inseridas direto no banco de teste (documentado no log). Aplicação derrubada (porta 50940, Hikari encerrado) e reiniciada (porta 52548) **apontando para o mesmo banco** (50613). Depois do reinício: mesma lista de 3 colaboradores (JSON idêntico ao de antes), folha geral idêntica byte a byte, banco idêntico, total do 1001 = 2000.00. Totais após reinício: 1001=2000.0, 2001=4000.0, 3001=1505.0 (1505.0 por causa do BUG011, consistente antes e depois) | **Aprovado** | — (observação: passos de registro falham pelo BUG014) |
| Sonda JSON (não é caso) | — | Registrar status e efeito de um POST com corpo JSON válido | Ver seção 5 | Sonda executada | BUG013 |

Nenhum caso ficou bloqueado: o PostgreSQL embutido subiu no Windows em todas as execuções e a aplicação rodou por inteiro.

## 5. Sonda do JSON (não é um caso de teste)

POST `/colaboradores/cadastro` com `Content-Type: application/json` e corpo válido
`{"matricula":"9001","nome":"Sonda Json","salario":1000.00,"tipo":"PADRAO"}`:

- **Status HTTP 500** — no servidor: `IdentifierGenerationException: Identifier of entity 'Colaborador' must be manually assigned before calling 'persist()'`, com o SQL `where c1_0.matricula is null`: os parâmetros vieram vazios, ou seja, **o corpo JSON foi ignorado** (não há `@RequestBody`).
- Verificação no banco: `GET /colaboradores` → 200 com lista vazia; a matrícula 9001 **não foi persistida**.
- Conclusão: a aplicação só aceita `application/x-www-form-urlencoded`. Registrado como **BUG013** (observação no CT029; não muda a situação do CT029, que usa formulário).

## 6. Defeitos novos registrados

### BUG013 — POSTs não leem o corpo JSON: faltam `@RequestBody`

| Campo | Valor |
|---|---|
| Esperado | `POST /colaboradores/cadastro` (e `/pagamento/comissao`, `/pagamento/producao`) com corpo JSON (`application/json`) é interpretado pela aplicação (RF001/RF003/RF004) |
| Obtido | Corpo JSON ignorado (parâmetros nulos): cadastro → HTTP 500 com `IdentifierGenerationException` ("must be manually assigned before calling 'persist()'"); nada é persistido. Só funciona `application/x-www-form-urlencoded` |
| Severidade | **Alta** — a API se comporta como incompatível com clientes REST que enviam JSON; com JSON o sistema retorna 500 em vez de erro de validação |
| Prioridade | **Média** — há via alternativa de uso (formulário), mas o contrato REST está quebrado |
| Status | Novo |
| Responsável | A definir |
| Caso de teste | Sonda JSON (observação no CT029) |

### BUG014 — Registrar comissão/produção sempre falha com HTTP 500 (ClassCastException em `findTipoColaboradorByMatricula`)

| Campo | Valor |
|---|---|
| Esperado | `POST /pagamento/comissao` e `POST /pagamento/producao` com matrícula válida retornam 200 e gravam o registro (RF003/RF004/RN008) |
| Obtido | **Sempre HTTP 500**, para qualquer matrícula, com `java.lang.ClassCastException: class colaboradores.requisitos.entity.Colaborador cannot be cast to class colaboradores.requisitos.entity.TipoColaborador` em `ColaboradorRepository.findTipoColaboradorByMatricula` → `PagamentoService.tipoColaborador (linha 65)` → `PagamentoService.postComissao/postProducao`. O proxy Spring Data monta uma consulta que devolve a entidade `Colaborador` inteira e o cast para `TipoColaborador` quebra. Nenhuma comissão ou produção consegue ser registrada pela API |
| Severidade | **Crítica** — bloqueia RF003 e RF004 por completo no sistema real (nenhum registro de comissão/produção é aceito) |
| Prioridade | **Alta** — corrigir desbloqueia o fluxo principal de pagamento |
| Status | Novo |
| Responsável | A definir |
| Caso de teste | CT029 (observação no CT030) |

Observações relacionadas (defeitos já registrados, confirmados nesta execução):

- **BUG010** disparou no CT029: com as tabelas de comissão/produção vazias (por causa do BUG014), `GET /pagamento/resumo` retornou 500 por NullPointerException nos somatórios.
- **BUG011** confirmado no CT030 (via dados preparados no banco): com produção gravada (100 un. × 5.00 = 500.00), a folha do 3001 mostrou adicional 5.00 (soma `SUM(valor)`) e total 1505.00 em vez de 2000.00.
- **BUG003 e BUG004 não puderam ser observados nesta execução**: o BUG014 impede que as linhas cheguem ao banco pela API. Ao corrigir BUG014, reexecutar `testeSistema` para verificá-los.

## 7. Totais

| Situação | Quantidade | Casos |
|---|---|---|
| Planejados | 2 | CT029, CT030 |
| Executados | 2 | CT029, CT030 |
| Aprovados | 1 | CT030 |
| Reprovados | 1 | CT029 |
| Bloqueados | 0 | — |
| Sonda (não é caso) | 1 executada | Sonda JSON → corpos JSON ignorados (BUG013) |
| Defeitos novos | 2 | BUG013 (Alta/Média), BUG014 (Crítica/Alta) |

Resumo da tarefa `testeSistema`: `3 tests completed, 1 failed` (a falha é o CT029 reprovado — resultado esperado do teste diante dos defeitos; a tarefa sinaliza falha porque o caso reprova).

## 8. Comandos para repetir

```bat
cd C:\Users\LENOVO\IdeaProjects\T1-Colaboradoresatt
cmd.exe /c gradlew.bat testeSistema      :: testes de sistema (sobe PostgreSQL embutido)
cmd.exe /c gradlew.bat test              :: testes unitários (NÃO executa os de sistema)
```

Do WSL: `cmd.exe /c "cd /d C:\Users\LENOVO\IdeaProjects\T1-Colaboradoresatt && gradlew.bat testeSistema"`

Relatórios HTML: `build/reports/tests/testeSistema/index.html` e `build/reports/tests/test/index.html`.

Na primeira execução, o Gradle baixa do Maven Central o `embedded-postgres` e os binários do PostgreSQL 17 (dezenas de MB).

## 9. Limitações

- A execução precisa ser feita pelo lado Windows (Java e rede para os binários do PostgreSQL embutido); do WSL o Gradle não encontra o Java no PATH.
- No CT030, os registros de comissão e produção foram inseridos direto no banco de teste porque a API de registro falha (BUG014). O caso verifica persistência (RNF003), que é o objetivo dele; a falha dos endpoints de registro está registrada como defeito (BUG014) via CT029.
- O CT029 reprova na primeira divergência estrutural (BUG014); por isso BUG003/BUG004/BUG011 não puderam ser exercitados pelo caminho da API nesta execução.
- `gradlew.bat test` (unidade): **ApplicationTests falhou** — `contextLoads` não consegue carregar o contexto porque o `application.properties` aponta para o banco de desenvolvimento (`localhost:5432/postgres`), inacessível daqui (`Unable to determine Dialect without JDBC metadata`). Registrado, não corrigido, conforme instruído. Os testes unitários existentes (`ColaboradorServiceTest`, `PagamentoServiceTest`) também rodaram nessa tarefa: 29 execuções, 14 falhas, 1 ignorado — comportamento pré-existente do código de produção contra esses testes, fora do escopo deste relatório e não alterado por esta entrega.
