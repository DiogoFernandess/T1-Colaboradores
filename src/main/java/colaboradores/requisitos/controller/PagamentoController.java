package colaboradores.requisitos.controller;

import colaboradores.requisitos.entity.ColaboradorPagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/pagamento")
public class PagamentoController {

    @GetMapping("/folha")
    public ResponseEntity<List<ColaboradorPagamento>> getFolha (){

        return ResponseEntity.ok()
    }

    @GetMapping("/folha/{matricula}")
    public ResponseEntity<ColaboradorPagamento> getFolhaByMatricula(){

        return ResponseEntity.ok()
    }
}
