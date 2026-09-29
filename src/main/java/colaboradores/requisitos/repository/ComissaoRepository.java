package colaboradores.requisitos.repository;

import colaboradores.requisitos.entity.Comissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface ComissaoRepository extends JpaRepository<Comissao, String> {

    @Query("SELECT COALESCE(SUM(c.valor), 0) FROM Comissao c")
    BigDecimal sumTotalComissoes();

}
