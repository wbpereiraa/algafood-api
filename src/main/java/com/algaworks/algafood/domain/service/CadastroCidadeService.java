package com.algaworks.algafood.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.algaworks.algafood.domain.exception.CidadeNaoEncontradaException;
import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.model.Cidade;
import com.algaworks.algafood.domain.model.Estado;
import com.algaworks.algafood.domain.repository.CidadeRepository;

@Service
public class CadastroCidadeService {

	private static final String MSG_NAO_PODE_SER_REMOVIDA_POIS_ESTA_EM_USO 
		= "Cidade de código %d não pode ser removida, pois está em uso";

	@Autowired
	private CidadeRepository cidadeRepository;
	
	@Autowired
    private CadastroEstadoService cadastroEstado;;

	public Cidade salvar (Cidade cidade) {
		Long estadoId = cidade.getEstado().getId();
		
        Estado estado = cadastroEstado.buscarOuFalhar(estadoId);
        
        cidade.setEstado(estado);
        
        return cidadeRepository.save(cidade);
	} 
	
	public void excluir(Long cidadeId) {
		try {
			if (!cidadeRepository.existsById(cidadeId)) {
				throw new CidadeNaoEncontradaException(cidadeId);
		
			}
			cidadeRepository.deleteById(cidadeId);
			
		}catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format(MSG_NAO_PODE_SER_REMOVIDA_POIS_ESTA_EM_USO, cidadeId));
		}
	}
	
	public Cidade buscarOuFalhar(Long cidadeId) {
		return cidadeRepository.findById(cidadeId)
				.orElseThrow(() -> new CidadeNaoEncontradaException(cidadeId));
	}
}
