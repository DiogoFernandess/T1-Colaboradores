package colaboradores.requisitos.repository;

import colaboradores.requisitos.entity.Colaborador;
import colaboradores.requisitos.entity.TipoColaborador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColaboradorRepository extends JpaRepository<Colaborador, String> {

    Optional<Colaborador> findByMatricula (String matricula);

    boolean existsByMatricula(String matricula);

    TipoColaborador findTipoColaborador(String matricula);
}