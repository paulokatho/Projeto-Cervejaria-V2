package session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
		ItemVenda itemVenda = new ItemVenda();
		itemVenda.setCerveja(cerveja);
		itemVenda.setQuantidade(quantidade);
		itemVenda.setValorUnitario(cerveja.getValor());
		
		itens.add(itemVenda);
	}
	
	public int total() {
		return itens.size();
	}
}
