package colaboradores.requisitos.repository;

import colaboradores.requisitos.entity.Producao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProducaoRepository extends JpaRepository<Producao, String> {
}
