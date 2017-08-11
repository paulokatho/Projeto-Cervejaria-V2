package com.algaworks.brewer.controller;

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

import session.TabelaItensVenda;

@Controller
@RequestMapping("/vendas")
public class VendasController {
	
	@Autowired
	private Cervejas cervejas;
	
	@Autowired
	private TabelaItensVenda tabelaItensVenda;
	
	@GetMapping("/nova")
	public String nova() {
		return "venda/CadastroVenda";
	}

	@PostMapping("/item")
	public ModelAndView adicionarItem(Long codigoCerveja) {
		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		tabelaItensVenda.adicionarItem(cerveja, 1);
		return mvTabelaItensVenda();
	}
	
	@PutMapping("/item/{codigoCerveja}")
	public ModelAndView alterarQuantidadeItem(@PathVariable Long codigoCerveja, Integer quantidade) {
		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		tabelaItensVenda.alterarQuantidadeItens(cerveja, quantidade);
		return mvTabelaItensVenda();
	}
	
	@DeleteMapping("/item/{codigoCerveja}")
	//O @PathVariable é uma tecnica da juncao do spring mvc com spring jpa
	public ModelAndView excluirItem(@PathVariable("codigoCerveja")Cerveja cerveja) { //no @PutMapping esta usando o findOne, mas aqui não precisa, pois colocamos o domain no webConfig aula 23-10 08:20
		tabelaItensVenda.excluirItem(cerveja);
		return mvTabelaItensVenda();
	}

	private ModelAndView mvTabelaItensVenda() {
		ModelAndView mv = new ModelAndView("venda/TabelaItensVenda");
		mv.addObject("itens", tabelaItensVenda.getItens());
		return mv;
	}

}
