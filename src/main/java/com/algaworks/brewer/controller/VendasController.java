package com.algaworks.brewer.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.session.TabelasItensSession;

@Controller
@RequestMapping("/vendas")
public class VendasController {
	
	/*
	 * Houve uma grande mudança na aula 23-11 09:23
	 */
	
	@Autowired
	private Cervejas cervejas;
	
	@Autowired
	private TabelasItensSession tabelaItens;
	
	@GetMapping("/nova")
	public ModelAndView nova() {
		ModelAndView mv = new ModelAndView("venda/CadastroVenda");
		mv.addObject("uuid", UUID.randomUUID().toString()); //gera um id para cada aba que for aberta no browser, para quando estiver logado como mesmo usuario. Aula 23/11 18:00
		return mv;
	}

	@PostMapping("/item")
	public ModelAndView adicionarItem(Long codigoCerveja, String uuid) {
		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		tabelaItens.adicionarItem(uuid, cerveja, 1);
		return mvTabelaItensVenda(uuid);
	}
	
	//O @PathVariable mudou igual ao @Delete, ms deixei do outro jeito pra ter outra opção. Aula 23-11 10:37 da pra ver bem a mudança
	@PutMapping("/item/{codigoCerveja}")
	public ModelAndView alterarQuantidadeItem(@PathVariable Long codigoCerveja
			, Integer quantidade, String uuid) {
		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		tabelaItens.alterarQuantidadeItens(uuid, cerveja, quantidade);
		return mvTabelaItensVenda(uuid);
	}
	
	@DeleteMapping("/item/{uuid}/{codigoCerveja}")
	//O @PathVariable é uma tecnica da juncao do spring mvc com spring jpa
	public ModelAndView excluirItem(@PathVariable("codigoCerveja")Cerveja cerveja
			,@PathVariable String uuid) { //no @PutMapping esta usando o findOne, mas aqui não precisa, pois colocamos o domain no webConfig aula 23-10 08:20
		tabelaItens.excluirItem(uuid, cerveja);
		return mvTabelaItensVenda(uuid);
	}

	private ModelAndView mvTabelaItensVenda(String uuid) {
		ModelAndView mv = new ModelAndView("venda/TabelaItensVenda");
		mv.addObject("itens", tabelaItens.getItens(uuid));
		mv.addObject("valorTotal", tabelaItens.getValorTotal(uuid));//para capturar o valor total para exibir no quadro da tela de vendas. Aula 23:12 07:30
		return mv;
	}

}
