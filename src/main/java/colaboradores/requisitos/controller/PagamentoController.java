package colaboradores.requisitos.controller;

import colaboradores.requisitos.entity.ColaboradorPagamento;
import colaboradores.requisitos.entity.Comissao;
import colaboradores.requisitos.entity.FolhaResumo;
import colaboradores.requisitos.entity.Producao;
import colaboradores.requisitos.service.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/pagamento")
@Tag(name = "Pagamento", description = "Registro de Comissão, Produção e Geração de Folha de Pagamento")
public class PagamentoController {

    @Autowired
    PagamentoService pagamentoService;

    @GetMapping("/folha")
    @Operation(summary = "Gera Folha de Pagamento", description = "Gera Folha de Pagamento Geral")
    public ResponseEntity<List<ColaboradorPagamento>> getFolha (){

        return ResponseEntity.ok(pagamentoService.folha());
    }

    @GetMapping("/folha/{matricula}")
    @Operation(summary = "Gera Folha de Pagamento de um Colaborador", description = "Gera Folha de Pagamento de um Colaborador com Todos os Dados")
    public ResponseEntity<ColaboradorPagamento> getFolhaByMatricula(@PathVariable String matricula){

        return ResponseEntity.ok(pagamentoService.getFolhaByMatricula(matricula));
    }

    @GetMapping("/resumo")
    @Operation(summary = "Folha Resumida", description = "Gera Folha de Pagamento Resumida")
    public ResponseEntity<FolhaResumo> getFolhaResumo(){

        return ResponseEntity.ok(pagamentoService.folhaResumo());
    }

    @PostMapping("/producao")
    @Operation(summary = "Cadastra uma Produção", description = "Cadastra uma Produção com uma Matricula de Colaborador")
    public ResponseEntity<Producao> postProducao (Producao producao){

        return ResponseEntity.ok(pagamentoService.postProducao(producao));
    }

    @PostMapping("/comissao")
    @Operation(summary = "Cadastra uma Comissão", description = "Cadastra uma Comissão com uma Matricula de Colaborador")
    public ResponseEntity<Comissao> postCommissao (Comissao comissao){


        return ResponseEntity.ok(pagamentoService.postComissao(comissao));
    }
}
