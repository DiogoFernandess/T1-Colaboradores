package colaboradores.requisitos.service;

import colaboradores.requisitos.entity.Comissao;
import colaboradores.requisitos.entity.Producao;
import colaboradores.requisitos.repository.ComissaoRepository;
import colaboradores.requisitos.repository.ProducaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    @Autowired
    ComissaoRepository comissaoRepository;

    @Autowired
    ProducaoRepository producaoRepository;

    public Comissao postComissao(Comissao comissao){

        return comissaoRepository.save(comissao);
    }

    public Producao postProducao(Producao producao){

        return producaoRepository.save(producao);
    }
}
