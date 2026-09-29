package colaboradores.requisitos.service;

import colaboradores.requisitos.entity.Comissao;
import colaboradores.requisitos.repository.ComissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    @Autowired
    ComissaoRepository comissaoRepository;

    public Comissao postComissao(Comissao comissao){

        return comissaoRepository.save(comissao);
    }
}
