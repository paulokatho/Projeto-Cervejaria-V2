package com.algaworks.brewer.session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;

/***
 * 
 * @author Katho
 * Tabela que contem os itens da venda
 * É necessário criar um objeto desse para cada usuário que logar na aplicação e para isso a anotação @Scope
 *
 */
//antes do eclipse 4.3 tinha que declarar o @Scope como abaixo
//@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
//@SessionScope
//@Component
class TabelaItensVenda { //Essa classe na aula 23-11 08:27 foi tirada de publica para poder ser acessada só por TabelasItensSession

	private String uuid; //aula 23-11 18:00
 	private List<ItemVenda> itens = new ArrayList<>();
 	
 	public TabelaItensVenda(String uuid) {
		super();
		this.uuid = uuid;
	}
	
	//utilizando java 8 para realizar o valorTotal .stream é um tipo de iterador
	public BigDecimal getValorTotal() {
	
		return itens.stream()
				.map(ItemVenda::getValorTotal)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO); 
				
	}
	
	public void adicionarItem(Cerveja cerveja, Integer quantidade) {
		Optional<ItemVenda> itemVendaOptional = buscarItemPorCerveja(cerveja);
		
		ItemVenda itemVenda = null;
		if(itemVendaOptional.isPresent()) {
			itemVenda = itemVendaOptional.get();
			itemVenda.setQuantidade(itemVenda.getQuantidade() + quantidade);
		} else {
			itemVenda = new ItemVenda();
			itemVenda.setCerveja(cerveja);
			itemVenda.setQuantidade(quantidade);
			itemVenda.setValorUnitario(cerveja.getValor());		
			itens.add(0, itemVenda);			
		}	
	}
	
	public void alterarQuantidadeItens(Cerveja cerveja, Integer quantidade) {
		ItemVenda itemVenda = buscarItemPorCerveja(cerveja).get();
		itemVenda.setQuantidade(quantidade);
	}
	
	public void excluirItem(Cerveja cerveja) { //aula 23-10 00:20
		int indice = IntStream.range(0, itens.size())
				.filter(i -> itens.get(i).getCerveja().equals(cerveja))
				.findAny().getAsInt(); //intStream do java8. Com esse codigo vai percorrer o itens até o tamanho dele e filtra se o getCerveja é igual o parametro cerveja na posicao 'i' 
		itens.remove(indice);
	}
	
	public int total() {
		return itens.size();
	}

	public List<ItemVenda> getItens() {
		return itens; //para retornar os itens em VendasController - aula 02:35
	}
	
	private Optional<ItemVenda> buscarItemPorCerveja(Cerveja cerveja) { //aula 23-8 12:00
		return itens.stream()
				.filter(i -> i.getCerveja().equals(cerveja))
				.findAny();
	}
	
	public String getUuid() {
		return uuid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TabelaItensVenda other = (TabelaItensVenda) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
	
}
