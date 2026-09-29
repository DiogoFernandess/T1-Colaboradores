package colaboradores.requisitos.repository;

import colaboradores.requisitos.entity.Colaborador;
import colaboradores.requisitos.entity.TipoColaborador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ColaboradorRepository extends JpaRepository<Colaborador, String> {

    Optional<Colaborador> findByMatricula (String matricula);

    boolean existsByMatricula(String matricula);

    TipoColaborador findTipoColaboradorByMatricula(String matricula);

    int countByTipo(TipoColaborador tipo);

    @Query("SELECT SUM(c.salario) FROM Colaborador c WHERE c.tipo = :tipo")
    BigDecimal sumSalarioByTipo(@Param("tipo") TipoColaborador tipo);
}