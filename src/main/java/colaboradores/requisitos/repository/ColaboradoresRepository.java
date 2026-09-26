package colaboradores.requisitos.repository;

import colaboradores.requisitos.entity.Colaboradores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColaboradoresRepository extends JpaRepository<Colaboradores, String> {

    Optional<Colaboradores> findByMatricula (String matricula);
}