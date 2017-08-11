package session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

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
@SessionScope
@Component
public class TabelaItensVenda {

	private List<ItemVenda> itens = new ArrayList<>();
	
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
	
}
