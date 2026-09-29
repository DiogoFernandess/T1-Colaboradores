package colaboradores.requisitos.controller;

import colaboradores.requisitos.entity.Colaborador;
import colaboradores.requisitos.service.ColaboradorService;
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
public class ColaboradorController {

    @Autowired
    ColaboradorService colaboradorService;

    @GetMapping
    public ResponseEntity<List<Colaborador>> getAllColaboradores(){

        return ResponseEntity.ok(colaboradorService.getAllColaboradores());
    }

    @GetMapping("/{matricula}")
    public ResponseEntity<Colaborador> getColaboradorByMatricula(@PathVariable String matricula){

        return ResponseEntity.ok(colaboradorService.findColaboradorByMatricula(matricula));
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Colaborador> cadastroColaborador (Colaborador colaborador){

        return ResponseEntity.ok(colaboradorService.cadastroColaborador(colaborador));
    }
}
