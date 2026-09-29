package colaboradores.requisitos.repository;

import colaboradores.requisitos.entity.Comissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ComissaoRepository extends JpaRepository<Comissao, String> {

    @Query("SELECT COALESCE(SUM(c.valor), 0) FROM Comissao c")
    BigDecimal sumTotalComissoes();

    @Query("SELECT COALESCE(SUM(c.valor), 0) FROM Comissao c WHERE c.colaborador.idMatricula = :idMatricula")
    BigDecimal sumValorByColaboradorId(@Param("idMatricula") String matricula);
}
