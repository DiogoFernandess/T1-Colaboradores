package colaboradores.requisitos.controller;

import colaboradores.requisitos.entity.Colaborador;
import colaboradores.requisitos.service.ColaboradorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping("/colaboradores")
@Tag(name = "Colaborador", description = "Cadastro de Colaborador e Visualização")
public class ColaboradorController {

    @Autowired
    ColaboradorService colaboradorService;

    @GetMapping
    @Operation(summary = "Busca Colaboradores", description = "Pega Todos os Dados de Todos os Colaboradores")
    public ResponseEntity<List<Colaborador>> getAllColaboradores(){

        return ResponseEntity.ok(colaboradorService.getAllColaboradores());
    }

    @GetMapping("/{matricula}")
    @Operation(summary = "Busca Colaborador por Matricula", description = "Busca os Dados de Colaborador por Matricula")
    public ResponseEntity<Colaborador> getColaboradorByMatricula(@PathVariable String matricula){

        return ResponseEntity.ok(colaboradorService.findColaboradorByMatricula(matricula));
    }

    @PostMapping("/cadastro")
    @Operation(summary = "Cadastra Colaborador", description = "Cadastra os Dados de Colaborador")
    public ResponseEntity<Colaborador> cadastroColaborador (Colaborador colaborador){

        return ResponseEntity.ok(colaboradorService.cadastroColaborador(colaborador));
    }
}
