package colaboradores.requisitos.service;

import colaboradores.requisitos.entity.Colaboradores;
import colaboradores.requisitos.exception.ResourceNotFoundException;
import colaboradores.requisitos.repository.ColaboradoresRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColaboradoresService {

    @Autowired
    ColaboradoresRepository colaboradoresRepository;

    public List<Colaboradores> getAllColaboradores (){

        return colaboradoresRepository.findAll();
    }

    public Colaboradores getColaboradorByMatricula(String matricula){

        return colaboradoresRepository.findByMatricula(matricula)
                    .orElseThrow(
                            ()-> new ResourceNotFoundException("Matricula not found " + matricula)
                    );
        }
}
