package com.algaworks.brewer.session;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.algaworks.brewer.model.Cerveja;

@SessionScope
@Component
public class TabelasItensSession {

	//1 sessao do usuario pode ter varias tabelas. Serve para gerenciar as abas abertas no browser. Aula 23:11 07:11
	private Set<TabelaItensVenda> tabelas = new HashSet<>();
	//o set não deixa ter tabela repetida dentro dele

	public void adicionarItem(String uuid, Cerveja cerveja, int quantidade) {
		TabelaItensVenda tabela = buscarTabelaPorUuid(uuid);//verifica se tem na tabela o uuid, se não ele cria uma nova tabela para gerenciar as abas abertas no browser aula 23-11 14;05
		tabela.adicionarItem(cerveja, quantidade);
		tabelas.add(tabela);
	}


	public void alterarQuantidadeItens(String uuid, Cerveja cerveja, Integer quantidade) {
		TabelaItensVenda tabela = buscarTabelaPorUuid(uuid);
		tabela.alterarQuantidadeItens(cerveja, quantidade);
		
	}

	public void excluirItem(String uuid, Cerveja cerveja) {
		TabelaItensVenda tabela = buscarTabelaPorUuid(uuid);
		tabela.excluirItem(cerveja);
		
	}

	public Object getItens(String uuid) {
		return buscarTabelaPorUuid(uuid).getItens();
	}

	private TabelaItensVenda buscarTabelaPorUuid(String uuid) {
		TabelaItensVenda tabela = tabelas.stream()
				.filter(t -> t.getUuid().equals(uuid))
				.findAny()
				.orElse(new TabelaItensVenda(uuid));
		return tabela;
	}
}
