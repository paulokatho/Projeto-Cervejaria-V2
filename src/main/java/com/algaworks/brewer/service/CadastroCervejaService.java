package com.algaworks.brewer.service;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.storage.FotoStorage;

@Service
public class CadastroCervejaService {

	@Autowired
	private Cervejas cervejas;
	
	//@Autowired -- tirado na aula 28.05 05:00 *** Ver a classe FotoStorage, tem um comentário lá.
	//private ApplicationEventPublisher publisher;
	
	@Autowired
	private FotoStorage fotoStorage;
	
	@Transactional
	public void salvar(Cerveja cerveja) {
		cervejas.save(cerveja);
		
		//publisher.publishEvent(new CervejaSalvaEvent(cerveja));
	}
	
	@Transactional
	public void excluir(Cerveja cerveja) {
		try {
			String foto = cerveja.getFoto();
			cervejas.delete(cerveja);
			cervejas.flush();//pede pra apagar a cerveja no delete e o flush executa, mas se tiver violação de chave ele emite o PersistenceException e assim tratamos o erro
			fotoStorage.excluir(foto);
		}catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar cerveja. Já foi utilizada em alguma venda.");
		}
	}
	
}
