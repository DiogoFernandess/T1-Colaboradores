package colaboradores.requisitos.service;

import colaboradores.requisitos.entity.*;
import colaboradores.requisitos.exception.ConflictException;
import colaboradores.requisitos.repository.ColaboradorRepository;
import colaboradores.requisitos.repository.ComissaoRepository;
import colaboradores.requisitos.repository.ProducaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    @Autowired
    ComissaoRepository comissaoRepository;

    @Autowired
    ProducaoRepository producaoRepository;

    @Autowired
    ColaboradorService colaboradorService;

    @Autowired
    ColaboradorRepository colaboradorRepository;

    public Comissao postComissao(Comissao comissao){

        TipoColaborador tipo = tipoColaborador(comissao.getIdMatricula());

        if (tipo != TipoColaborador.COMISSIONADO) {

            throw new ConflictException("Colaborador não pertence a esta categoria");
        }

        return comissaoRepository.save(comissao);
    }

    public Producao postProducao(Producao producao){

        TipoColaborador tipo = tipoColaborador(producao.getIdMatricula());

        if (tipo != TipoColaborador.PRODUCAO) {

                throw new ConflictException("Colaborador não pertence a esta categoria");
        }

        return producaoRepository.save(producao);
    }

    public TipoColaborador tipoColaborador(String matricula){

        colaboradorService.matriculaNoExist(matricula);

        return colaboradorRepository.findTipoColaborador(matricula);
    }

    public List<ColaboradorPagamento> folha (){

        return
    }

    public FolhaResumo folhaResumo(){

        return
    }

    public ColaboradorPagamento getFolhaByMatricula(String matricula){

        return
    }
}
