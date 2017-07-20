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
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.repository.Clientes;
import com.algaworks.brewer.repository.Estados;
import com.algaworks.brewer.repository.filter.ClienteFilter;
import com.algaworks.brewer.service.CadastroClienteService;
import com.algaworks.brewer.service.exception.CpfCnpjClienteJaCadastradoException;


@Controller
@RequestMapping("/clientes")
public class ClientesController {

	@Autowired
	private Estados estados;
	
	@Autowired
	private CadastroClienteService cadastroClienteService;
	
	@Autowired
	private Clientes clientes;
	
	@RequestMapping("/novo")
	public ModelAndView novo(Cliente cliente) {
		ModelAndView mv = new ModelAndView("cliente/CadastroCliente");
		mv.addObject("tiposPessoa", TipoPessoa.values());
		mv.addObject("estados", estados.findAll());
		return mv;
	}
	
	@PostMapping("/novo")
	public ModelAndView salvar(@Valid Cliente cliente, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return novo(cliente);
		}
		
		try {
			cadastroClienteService.salvar(cliente);
		} catch (CpfCnpjClienteJaCadastradoException e) {
			//aqui é uma maneira de rejeitar um valor sem ser pelas anotações do bean validation (não pode esquecer do return novo(cliente), se não ele vai passar para mensagem de Cliente salvo com sucesso!)
			//é possivel rejeitar qualquer valor que seja necessário dessa maneira.
			result.rejectValue("cpfOuCnpj", e.getMessage(), e.getMessage());//cpfOuCnpj é o nome do field que estamos querendo rejeitar
			return novo(cliente);
		}
		
		attributes.addFlashAttribute("mensagem", "Cliente salvo com sucesso!");
		return new ModelAndView("redirect:/clientes/novo");
	}
	
	@GetMapping
	public ModelAndView pesquisar(ClienteFilter clienteFilter, BindingResult result
					, @PageableDefault(size = 3) Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("cliente/PesquisaClientes");
		mv.addObject("tiposPessoa", TipoPessoa.values());
		
		PageWrapper<Cliente> paginaWrapper = new PageWrapper<>(clientes.filtrar(clienteFilter, pageable)
				, httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
	
	/* Se for um get normal vai para a pesquisa acima, se for um get com contentype do tipo APPLICATION_JSON_VALUE ele vai nesse request	 
	 */
	@RequestMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody List<Cliente> pesquisar(String nome) { //para conseguir tranformar o List Cliente em Json para exibir na tela é preciso do @ResponseBody
		validarTamanhoNome(nome);
		return clientes.findByNomeStartingWithIgnoreCase(nome);//Deu para fazer a pesquisa assim, mas se o nome ficar muito grande o ideal é criar a pesquisa "manual" e só de criar em clientes já vai funcionar
	}

	private void validarTamanhoNome(String nome) {
		if (StringUtils.isEmpty(nome) || nome.length() < 3) {
			throw new IllegalArgumentException();//aqui se digitar algo invalido no modal pesqRapidaCliente dará um erro 500 e capturamos ele em cliente.pesquisa-rapida.js no onPesquisaRapidaClicado() dentro do Ajax ('error')
		}
	}
	
	//Em controller/handler tem uma exception tratada que server para toda a aplicação, para ver que é possivel fazer das duas formas.
	//Esse metodo trata somente a excessão deste controller e somente esse controller
	//Aqui estamos mudando de erro 500 para 400 que é pois o cliente digitou algo errado e o erro 400 não estoura no log
	@ExceptionHandler(IllegalArgumentException.class)//passando a classe de exceção que ele vai tratar
	public ResponseEntity<Void> tratarIllegalArgumentException(IllegalArgumentException e) {
		return ResponseEntity.badRequest().build();
	}
}
