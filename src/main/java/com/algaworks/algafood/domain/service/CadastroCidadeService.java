package com.algaworks.algafood.domain.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Cidade;
import com.algaworks.algafood.domain.model.Estado;
import com.algaworks.algafood.domain.repository.CidadeRepository;
import com.algaworks.algafood.domain.repository.EstadoRepository;

@Service
public class CadastroCidadeService {

	private static final String MSG_NAO_PODE_SER_REMOVIDA_POIS_ESTA_EM_USO 
		= "Cidade de código %d não pode ser removida, pois está em uso";

	private static final String MSG_NAO_EXISTE_UM_CADASTRO_DE_CIDADE_COM_CODIGO 
		= "Não existe um cadastro de cidade com código %d";

	private static final String MSG_NAO_EXISTE_CADASTRO_DE_ESTADO_COM_CODIGO 
		= "Não existe cadastro de estado com código %d";

	@Autowired
	private CidadeRepository cidadeRepository;
	
	@Autowired
    private EstadoRepository estadoRepository;

	public Cidade salvar (Cidade cidade) {
		Long estadoId = cidade.getEstado().getId();
        Optional<Estado> estado = estadoRepository.findById(estadoId);
        
        if (!estadoRepository.existsById(estadoId)) {
            throw new EntidadeNaoEncontradaException(
                String.format(MSG_NAO_EXISTE_CADASTRO_DE_ESTADO_COM_CODIGO, estadoId));
        }
        
        cidade.setEstado(estado.get());
        
        return cidadeRepository.save(cidade);
	} 
	
	public void excluir(Long cidadeId) {
		try {
			if (!cidadeRepository.existsById(cidadeId)) {
				throw new EntidadeNaoEncontradaException(
					String.format(MSG_NAO_EXISTE_UM_CADASTRO_DE_CIDADE_COM_CODIGO, cidadeId));
		
			}
			cidadeRepository.deleteById(cidadeId);
			
		}catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format(MSG_NAO_PODE_SER_REMOVIDA_POIS_ESTA_EM_USO, cidadeId));
		}
	}
	
	public Cidade buscarOuFalhar(Long cidadeId) {
		return cidadeRepository.findById(cidadeId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException(
						String.format(MSG_NAO_EXISTE_UM_CADASTRO_DE_CIDADE_COM_CODIGO, 
								cidadeId)));
	}
}
