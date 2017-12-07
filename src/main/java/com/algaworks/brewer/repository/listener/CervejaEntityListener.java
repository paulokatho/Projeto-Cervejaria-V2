package com.algaworks.brewer.repository.listener;

import javax.persistence.PostLoad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.storage.FotoStorage;

/***
 * 
 * @author Katho
 *
 * Na tela de pesquisa de cerveja, quando a tela está sendo carregada, ou seja, quando o jpa hibernate acaba de carregar a cerveja do banco
 *  para cada cerveja da classe Cerveja que ela traz do banco ela entra aqui e faz esse @PostLoad
 *  para podermos exibir a foto thumbnail na tela. Aula 28.5 31:51
 *  
 * SpringBeanAutowiringSupport esse cara injeta o @Autowired nessa classe, pois não é o spring que gerencia a classe Cerveja, mas sim o 
 *  JPA Hibernate, então precisamos desse kara para fazer o spring enxergar essa classe.
 * Esse metodo foi pensado para ficar fora da Classe Cerveja, pois aqui podemos injetar qualquer bean do spring e fica desacoplado. Aula 28.05 34:41
 */

public class CervejaEntityListener {

	@Autowired
	private FotoStorage fotoStorage;
	
	@PostLoad
	public void postLoad(final Cerveja cerveja) {//final não me deixa retornar um valor null do metodo, ex return null;
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		cerveja.setUrlFoto(fotoStorage.getUrl(cerveja.getFotoOuMock()));
		cerveja.setUrlThumbnailFoto(fotoStorage.getUrl(FotoStorage.THUMBNAIL_PREFIX + cerveja.getFotoOuMock()));
	}
}
