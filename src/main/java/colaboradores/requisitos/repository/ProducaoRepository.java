package colaboradores.requisitos.repository;

import colaboradores.requisitos.entity.Producao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProducaoRepository extends JpaRepository<Producao, String> {

    @Query("SELECT SUM(p.valorUnidade) FROM Producao p")
    BigDecimal sumTotalProducao();

    @Query("SELECT SUM(p.valorUnidade) FROM Producao p WHERE p.idMatricula = :idMatricula")
    BigDecimal sumValorByColaboradorId(@Param("idMatricula") String idMatricula);
}
