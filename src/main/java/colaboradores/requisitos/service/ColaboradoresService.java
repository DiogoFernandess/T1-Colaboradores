package colaboradores.requisitos.service;

import colaboradores.requisitos.entity.Colaboradores;
import colaboradores.requisitos.repository.ColaboradoresRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColaboradoresService {

    ColaboradoresRepository colaboradoresRepository;

    public List<Colaboradores> getAllColaboradores (){

        return colaboradoresRepository.findAll();
    }
}
