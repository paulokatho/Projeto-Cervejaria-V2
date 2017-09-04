package com.algaworks.brewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.dto.CervejaDTO;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.Origem;
import com.algaworks.brewer.model.Sabor;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.repository.Estilos;
import com.algaworks.brewer.repository.filter.CervejaFilter;
import com.algaworks.brewer.service.CadastroCervejaService;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/cervejas")
public class CervejasController {
	
	@Autowired
	private Estilos estilos;
	
	@Autowired
	private CadastroCervejaService cadastroCervejaService;
	
	@Autowired
	private Cervejas cervejas;

	@RequestMapping("/nova")
	public ModelAndView nova(Cerveja cerveja) {
		ModelAndView mv = new ModelAndView("cerveja/CadastroCerveja");
		mv.addObject("sabores", Sabor.values());
		mv.addObject("estilos", estilos.findAll());
		mv.addObject("origens", Origem.values());
		return mv;
	}
	
	/**
	 * Estamos utilizando aqui a junção do spring (data com jpa)
	 * 
	 * Método que Salva e Edita. Mesmo método http recebe 2 requisições, mas com ações diferentes. Aula 25-2 13:43
	 * {//d+} é uma expressão regular que retornar a url /nova e qualquer número inteiro que venha. Se receber integer quer dizer que é edição, se não insert
	 * @param cerveja
	 * @param result
	 * @param model
	 * @param attributes
	 * @return
	 */
	@RequestMapping(value = {"/nova", "{\\d+}"}, method = RequestMethod.POST)
	public ModelAndView salvar(@Valid Cerveja cerveja, BindingResult result, Model model, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return nova(cerveja);
			//throw new RuntimeException(); //teste para página 500
		}
		
		cadastroCervejaService.salvar(cerveja);
		attributes.addFlashAttribute("mensagem", "Cerveja salva com sucesso!");
		return new ModelAndView("redirect:/cervejas/nova");
	}
	
	@GetMapping //Essa anotação só funciona a partir do java 8
	public ModelAndView pesquisar(CervejaFilter cervejaFilter, BindingResult result
			, @PageableDefault(size = 2) Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("cerveja/PesquisaCervejas");
		mv.addObject("estilos", estilos.findAll());
		mv.addObject("sabores", Sabor.values());
		mv.addObject("origens", Origem.values());
		
		PageWrapper<Cerveja> paginaWrapper = new PageWrapper<>(cervejas.filtrar(cervejaFilter, pageable)
				, httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
	
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<CervejaDTO> pesquisar(String skuOuNome) {
		return cervejas.porSkuOuNome(skuOuNome);
	}
	
	//Para exclusão temos que ter mapeado aqui no controller, também temos o objeto js brewer.dialogo-excluir tratando os eventos na tela. Aula 25-1 12:55
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Cerveja cerveja) {
		try {
			cadastroCervejaService.excluir(cerveja);//na hora de excluir tem que tirar a url após a pagina ser recarregada lá no pageWrapper no .replaceAll("excluido")
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());//e.getMessage vai pegar a mensagem de ImpossivelExcluirEntidadeException
			//esse 'e.' vai para o e.responseText em brewer.dialogo-excluir.js e é possível pegar ele lá pra exibir na tela. Aula 25-1 28:32
		}
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/{codigo}")// Recebemos a requisição e enviamos para a página de cadastro. Aula 25-2 02:58
	public ModelAndView editar(@PathVariable("codigo") Cerveja cerveja) {
		ModelAndView mv = nova(cerveja);
		mv.addObject(cerveja);//aqui podemos inserir a cerveja no método novo, pois o método novo vai carregar a tela com as informações da cerveja para edição.
		return mv;
	}
}
