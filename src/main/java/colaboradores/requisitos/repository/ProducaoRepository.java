package colaboradores.requisitos.repository;

import colaboradores.requisitos.entity.Producao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface ProducaoRepository extends JpaRepository<Producao, String> {

    @Query("SELECT COALESCE(SUM(p.valor), 0) FROM Producao p")
    BigDecimal sumTotalProducao();
}
