# 05 — Testes de Aceitação (Nível 4) — Sistema de Folha de Pagamento

## 1. Identificação do que foi testado

| Item | Valor |
|---|---|
| Branch | `caso-de-testes-sistema-aceitacao` |
| Commit testado | `8fc0a8e` (empurrado para `origin` sem PR; o pai é `76aa874`) |
| Casos | CT031 (critério 2) e CT032 (critério 3) |
| Classe de testes | `src/test/java/colaboradores/requisitos/AceitacaoFolhaPagamentoTest.java` (novo) |
| Execução | `cmd.exe /c gradlew.bat testeAceitacao` — 2 execuções de teste, **2 reprovadas** |

Os casos verificam os critérios de aceitação do cliente olhando o sistema como o cliente
olharia: entradas e saídas pela interface HTTP, sem conhecer o código. O resultado
esperado vem do enunciado do cliente (critério 2 — validar dados obrigatórios; critério 3
— cálculo correto por categoria). O teste roda com aplicação Spring Boot real (Tomcat em
porta aleatória), HTTP real (`java.net.http.HttpClient`) e PostgreSQL real (processo
próprio embutido, sem Docker, sem H2, sem mocks).

## 2. Ambiente da execução

| Componente | Versão / detalhe |
|---|---|
| Sistema | Windows 11 (+ WSL2). Gradle executado pelo lado Windows: `cmd.exe /c gradlew.bat ...` |
| Java | Toolchain 21 |
| Spring Boot | 4.1.1 |
| Gradle | 9.7.1 |
| Banco de teste | PostgreSQL 17.5 real (Zonky embedded-postgres 2.1.0), processo próprio, porta aleatória |
| Banco / porta usados | banco `folhapagamento_teste_aceitacao` @ porta 50808 (verificado no log via `current_database() || inet_server_port()`) |
| Aplicação | porta aleatória (62878 no CT032), `ddl-auto=update` |
| Isolamento | classe falha na hora se a URL apontar para `:5432/` ou terminar em `/postgres`; banco de desenvolvimento e PostgreSQL 18 do Windows não foram tocados |
| Correspondência commit × execução | `git diff -w` entre `src/main` do commit e da árvore de trabalho está vazio (as modificações locais pendentes são só de formatação); logo, o que foi executado é o que está commitado |

## 3. Tabela de resultados

| Código | Critério/Requisito | Cenário | Resultado esperado (cliente) | Resultado obtido (real) | Situação final | Defeito |
|---|---|---|---|---|---|---|
| CT031 | Critério 2 / RF001 / RN001–RN007 | Validar dados obrigatórios e valores inválidos, uma tentativa de cada vez | Cada entrada inválida é rejeitada com erro e nada é gravado; salário 0.00 e vendas 0.00 (limites) são aceitos | Nome vazio → **HTTP 200 e gravado** (9101, nome=""); salário -1.00 → **HTTP 200 e gravado** (9102, salario=-1.00); matrícula 1001 repetida → HTTP 500, não gravado (rejeitada); vendas -100.00 → HTTP 500, não gravado (rejeitada); percentual -5 → HTTP 500, não gravado (rejeitada); quantidade -1 → HTTP 500, não gravado (rejeitada); valor/unidade -0.01 → HTTP 500, não gravado (rejeitada); limite salário 0.00 → HTTP 200 e gravado (aceito); limite vendas 0.00 → **HTTP 500 e não gravado** (deveria ser aceito) | **Reprovado** | BUG001, BUG002 (conhecidos); BUG014 (conhecido, causa da tentativa 9); **BUG015 (novo)** — as 5 rejeições vieram como HTTP 500 genérico |
| CT032 | Critério 3 / RF006 / RN008 | Calcular o salário de cada categoria pela folha individual + limites | Padrão 2000.00; comissionado 4000.00 (3000.00 + 10000.00 × 10%); produção 2000.00 (1500.00 + 100 × 5.00); vendas 0.00 → final = base (3000.00); quantidade 0 → final = base (1500.00) | Folha 1001 → total **2000.00** ✓; folha 2001 → adicional 1000.00, total **4000.00** ✓; folha 3001 → adicional 5.00, total **1505.00** ✗ (esperado 2000.00); limite vendas 0.00 → total **3000.00** ✓; limite quantidade 0 → adicional 5.00, total **1505.00** ✗ (esperado 1500.00). Registro via HTTP de comissão/produção falhou com 500 (BUG014); cenário preparado no banco (documentado no log) | **Reprovado** | BUG011 (confirmado 2×, cálculo do adicional de produção); BUG014 (conhecido, bloqueia o registro via API no "Dado") |

## 4. Veredito por critério de aceitação

### Critério 2 — Validar dados obrigatórios: **NÃO ATENDIDO**

- Nome vazio e salário negativo são aceitos com HTTP 200 e gravados no banco (defeitos já
  conhecidos BUG001 e BUG002 — o cadastro não valida nada).
- A validação de vendas/percentual/quantidade/valor por unidade é **inalcançável pela
  interface**: todo `POST /pagamento/comissao` e `/pagamento/producao` cai em
  `ClassCastException` com HTTP 500 (BUG014), antes de qualquer conferência de valor.
- O valor limite "vendas 0.00 deve ser aceito" também falha pelo mesmo BUG014 (HTTP 500,
  nada gravado).
- Todas as rejeições que ocorrem chegam como **HTTP 500 genérico** sem mensagem de erro
  (falta `@ControllerAdvice`) — novo defeito **BUG015**. Pelo critério do cliente
  ("rejeitada e não gravada") as tentativas 3–7 foram rejeitadas e nada gravado, mas por
  queda de exceção, não por validação.

### Critério 3 — Cálculo correto por categoria: **NÃO ATENDIDO**

- O registro de comissão e produção pela API é impossível (BUG014), então no fluxo real do
  cliente o cálculo nunca chega a acontecer.
- Com os dados preparados no banco (valores que o cliente calculou: comissão 1000.00 e
  produção 500.00), o padrão (2000.00) e o comissionado (4000.00) ficam corretos, mas a
  produção dá **1505.00** em vez de 2000.00 — o adicional soma o `valor` da linha
  (5.00) em vez de `quantidade × valor` (BUG011).
- No limite "quantidade 0 → salário final = base", o obtido é **1505.00** em vez de
  1500.00, pelo mesmo BUG011 (mesmo com quantidade 0, o adicional vira 5.00).

## 5. Defeitos novos

| Campo | Conteúdo |
|---|---|
| ID | **BUG015** |
| Descrição | Esperado (RF001, RN001–RN007): toda tentativa de cadastro/registro inválida é rejeitada com um **erro de validação/negócio** utilizável pelo cliente (ex.: HTTP 400/409 com mensagem). Obtido: a aplicação não possui tratamento de exceções (`@ControllerAdvice`), então **todas as rejeições viram HTTP 500 genérico** `{"status":500,"error":"Internal Server Error"}`: matrícula duplicada (`ConflictException`) → 500; vendas/percentual/quantidade/valor inválidos (que hoje caem no BUG014) → 500. O cliente não distingue erro de entrada de falha de servidor e não recebe mensagem de erro utilizável. Evidência do CT031: 5 rejeições como HTTP 500 (tentativas 3, 4, 5, 6 e 7). |
| Severidade | Alta |
| Prioridade | Média |
| Status | Novo |
| Responsável | A definir |
| Caso de teste | CT031 |

Defeitos conhecidos citados (não duplicados): **BUG001** (nome não validado), **BUG002**
(salário negativo aceito), **BUG011** (adicional de produção soma `valor` e não
`quantidade × valor`), **BUG014** (`ClassCastException` em
`findTipoColaboradorByMatricula` bloqueia `POST /pagamento/comissao` e `/producao`).

## 6. Totais por situação (por caso)

| Situação | Quantidade | Casos |
|---|---|---|
| Aprovado | 0 | — |
| Reprovado | 2 | CT031, CT032 |
| Bloqueado | 0 | — |
| **Total executado** | **2** | CT031, CT032 (2 planejados, 2 executados) |

Nada bloqueou a execução: os dois casos rodaram por inteiro contra aplicação real + HTTP
real + PostgreSQL real.

## 7. Comandos para repetir

```
cmd.exe /c gradlew.bat testeAceitacao     # só os testes de aceitação (tag aceitacao)
cmd.exe /c gradlew.bat testeSistema       # só os testes de sistema (tag sistema)
cmd.exe /c gradlew.bat test               # testes unitários (exclui as tags sistema e aceitacao)
```

Confirmação de que as outras tarefas não foram afetadas (executadas após a entrega):

- `testeAceitacao` roda **apenas** CT031 e CT032 (a saída da execução acima mostra só
  esses 2 testes — nenhum CT029/CT030/sonda).
- `testeSistema` usa `includeTags 'sistema'` e portanto não roda CT031/CT032.
- `test` usa `excludeTags 'sistema', 'aceitacao'` e portanto não roda CT031/CT032.
- Execução de confirmação do `test` após a entrega: 29 execuções de teste (14 falhas e 1
  pulo, todas pré-existentes dos testes unitários da equipe, intocadas) — **nenhum** teste
  de aceitação nem de sistema rodou junto (nenhum CT031/CT032/CT029/CT030 apareceu na
  saída).
- Execução de confirmação do `testeSistema` após a entrega: exatamente os mesmos 3 testes
  de sempre (CT029 reprovado, CT030 aprovado, sonda aprovada — 1 falha, idêntico ao
  Prompt 5) e **nenhum** teste de aceitação junto. CT031/CT032 não apareceram.

## 8. Limitações e observações

1. **CT032 — "Dado" preparado no banco**: como o registro via HTTP de comissão/produção
   falha por BUG014, os registros foram inseridos direto no banco de teste com os valores
   que o cliente calculou (comissão 1000.00; produção total 500.00), documentado no log da
   execução. O cálculo `10000.00 × 10%` e `100 × 5.00` é do cliente: o sistema deveria
   calcular (RF003/RF004, BUG003/BUG004), mas isso não é observável enquanto o BUG014
   existir. O caso valida o salário final devolvido pela folha com os dados registrados.
2. **CT031 — tentativas 4, 5, 6, 7 e 9**: a rejeição/erro dessas entradas vem do BUG014
   (queda com `ClassCastException`) e não de uma validação de negócio. O caso foi avaliado
   pelo critério do cliente ("rejeitada e não gravada"), mas não é possível afirmar o que
   a validação faria se o BUG014 fosse corrigido.
3. **CT031 — tentativa 9**: o valor limite "vendas 0.00 deve ser aceito" reprova o caso
   pelo BUG014 (HTTP 500, nada gravado), não por decisão de negócio.
4. As tentativas inválidas de cadastro usaram matrículas novas (9101, 9102) e o Dado do
   CT031 cadastrou também 2001 e 3001 (além da 1001 pedida no enunciado), para que as
   tentativas inválidas de comissão/produção caiam na validação de valores (RN004–RN007)
   e não em "matrícula inexistente".
5. `relatorios-testes/` está no `.gitignore` (verificado com `git check-ignore`): este
   relatório não é versionado, apenas os testes e o `build.gradle` foram enviados ao
   GitHub (commit `8fc0a8e`, push direto na branch, sem PR).
6. Nada foi alterado em `src/main`; nenhum teste existente foi alterado; `ApplicationTests`
   continua com sua falha registrada (aponta para o banco de desenvolvimento), não corrigido.
