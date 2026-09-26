package colaboradores.requisitos.controller;

import colaboradores.requisitos.entity.Colaboradores;
import colaboradores.requisitos.repository.ColaboradoresRepository;
import colaboradores.requisitos.service.ColaboradoresService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/colaboradores")
public class ColaboradorController {

    @Autowired
    ColaboradoresService colaboradoresService;

    @GetMapping
    public ResponseEntity<List<Colaboradores>> getAllColaboradores(){

        return ResponseEntity.ok(colaboradoresService.getAllColaboradores());
    }

    @GetMapping("/{matricula}")
    public ResponseEntity<Colaboradores> getColaboradorByMatricula(@PathVariable String matricula){

        return ResponseEntity.ok(colaboradoresService.getColaboradorByMatricula(matricula));
    }
}
