package colaboradores.requisitos.controller;

import colaboradores.requisitos.repository.ColaboradoresRepository;
import colaboradores.requisitos.service.ColaboradoresService;
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
    ColaboradoresService colaboradoresService;

    @GetMapping
    public ResponseEntity getAllColaboradores(){

        return ResponseEntity.ok(colaboradoresService.getAllColaboradores());
    }
}
