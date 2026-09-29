package colaboradores.requisitos.repository;

import colaboradores.requisitos.entity.Comissao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComissaoRepository extends JpaRepository<Comissao, String> {
}
