package com.algaworks.brewer.service.event.venda;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.repository.Cervejas;

@Component //para o spring conseguir mapear 
public class VendaListener {

	@Autowired
	private Cervejas cervejas;
	/**
	 * 
	 * @param vendaEvent
	 * Vamos percorrer a venda e os itens da venda pra encontrar quantas cervejas estão nessa venda
	 * Depois pesquisamos a cerveja de acordo com o código dela para achar qual é
	 * Então setamos a (quantidadeEstoque - item.getQuantidade())
	 * 
	 * Aula 26-7 03:51
	 */
	@EventListener //para conseguir ouvir CadastroVendaService conseguir mandar pra cá tem que colocar essa anotação. Aula 26-7 02:20 
	public void vendaEmitida(VendaEvent vendaEvent) {
		for(ItemVenda item : vendaEvent.getVenda().getItens()) {
			Cerveja cerveja = cervejas.findOne(item.getCerveja().getCodigo());
			cerveja.setQuantidadeEstoque(cerveja.getQuantidadeEstoque() - item.getQuantidade());
			cervejas.save(cerveja);
		}
	}
}
