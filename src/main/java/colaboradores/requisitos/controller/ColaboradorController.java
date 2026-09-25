package colaboradores.requisitos.controller;

import colaboradores.requisitos.repository.ColaboradoresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
@RequestMapping("/colaboradores")
public class ColaboradorController {

    @Autowired
    ColaboradoresRepository colaboradoresRepository;

    @GetMapping
    public ResponseEntity getAllColaboradores(){

        return ResponseEntity.ok(colaboradoresRepository.findAll());
    }
}
