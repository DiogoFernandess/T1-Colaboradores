package colaboradores.requisitos.controller;

import colaboradores.requisitos.entity.ColaboradorPagamento;
import colaboradores.requisitos.entity.Comissao;
import colaboradores.requisitos.entity.FolhaResumo;
import colaboradores.requisitos.entity.Producao;
import colaboradores.requisitos.service.PagamentoService;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
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
public class PagamentoController {

    @Autowired
    PagamentoService pagamentoService;

    @GetMapping("/folha")
    public ResponseEntity<List<ColaboradorPagamento>> getFolha (){

        return ResponseEntity.ok()
    }

    @GetMapping("/folha/{matricula}")
    public ResponseEntity<ColaboradorPagamento> getFolhaByMatricula(@PathVariable String matricula){

        return ResponseEntity.ok(pagamentoService.getFolhaByMatricula(matricula));
    }

    @GetMapping("/resumo")
    public ResponseEntity<FolhaResumo> getFolhaResumo(){

        return ResponseEntity.ok(pagamentoService.folhaResumo());
    }

    @PostMapping
    public ResponseEntity<Comissao> postCommissao (Comissao comissao){


        return ResponseEntity.ok(pagamentoService.postComissao(comissao));
    }

    @PostMapping
    public ResponseEntity<Producao> postProducao (Producao producao){

        return ResponseEntity.ok(pagamentoService.postProducao(producao));
    }
}
