package colaboradores.requisitos.service;

import colaboradores.requisitos.controller.ColaboradorController;
import colaboradores.requisitos.entity.Colaborador;
import colaboradores.requisitos.exception.ConflictException;
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

    public List<Colaborador> getAllColaboradores (){

        return colaboradoresRepository.findAll();
    }

    public Colaborador findColaboradorByMatricula(String matricula){

        return colaboradoresRepository.findByMatricula(matricula)
                    .orElseThrow(
                            ()-> new ResourceNotFoundException("Matricula not found " + matricula)
                    );
        }

    public Colaborador cadastroColaborador(Colaborador colaborador){

        matriculaExist(colaborador.getMatricula());

        return colaboradoresRepository.save(colaborador);
    }

    public void matriculaExist(String matricula){
        try {
            boolean exite = verifyMatriculaExist(matricula);
            if (exite) {
                throw new ConflictException("Email já cadastrado" + matricula);
            }
        } catch (ConflictException e){
            throw new ConflictException("Email já cadastrado" + e.getCause());
        }
    }


    public boolean verifyMatriculaExist(String matricula){
        return colaboradoresRepository.existsByMatricula(matricula);
    }
}
