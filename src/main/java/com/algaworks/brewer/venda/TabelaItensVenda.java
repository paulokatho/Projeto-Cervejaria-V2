package com.algaworks.brewer.venda;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;

/***
 * 
 * @author Katho
 * Tabela que contem os itens da venda
 *
 */
public class TabelaItensVenda {

	private List<ItemVenda> itens = new ArrayList();
	
	//utilizando java 8 para realizar o valorTotal .stream é um tipo de iterador
	public BigDecimal getValorTotal() {
	
		return itens.stream()
				.map(ItemVenda::getValorTotal)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO); 
				
	}
	
	public void adicionarItem(Cerveja cerveja, Integer quantidade) {
		ItemVenda itemVenda = new ItemVenda();
		itemVenda.setCerveja(cerveja);
		itemVenda.setQuantidade(quantidade);
		itemVenda.setValorUnitario(cerveja.getValor());
		
		itens.add(itemVenda);
	}
}
