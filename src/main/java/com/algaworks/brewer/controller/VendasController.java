package com.algaworks.brewer.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.controller.validator.VendaValidator;
import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.dto.VendaOrigem;
import com.algaworks.brewer.mail.Mailer;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.repository.Vendas;
import com.algaworks.brewer.repository.filter.VendaFilter;
import com.algaworks.brewer.security.UsuarioSistema;
import com.algaworks.brewer.service.CadastroVendaService;
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

	@Autowired
	private CadastroVendaService cadastroVendaService;
	
	@Autowired
	private Vendas vendas;
	
	@Autowired
	private VendaValidator vendaValidator;//aula 23-16 05:57
	
	@Autowired
	private Mailer mailer;//aula 24-3 06:06
	
	@InitBinder("venda")//colocamos o "venda" para não dar erro de inicialização, pois ele não encontra uma classe validadora sem informar a classe que será validada. Aula 23-18 02:48
	public void inicializarValidador(WebDataBinder binder) {
		binder.setValidator(vendaValidator);//Inicia o validador que criamos no package validator. Aula 23-16 06:04
	}
	
	@GetMapping("/nova")
	public ModelAndView nova(Venda venda) {
		ModelAndView mv = new ModelAndView("venda/CadastroVenda");
		//mv.addObject("uuid", UUID.randomUUID().toString()); //gera um id para cada aba que for aberta no browser, para quando estiver logado como mesmo usuario. Aula 23/11 18:00
		
		setUuid(venda);// 25-4 06:20
		
		//para deixar os itens permanentes, caso a tela seja renderizada aula 23-16 21:23
		mv.addObject("itens", venda.getItens());
		//aula 23-16 34:35 - iniciando valores do objeto venda
		mv.addObject("valorFrete", venda.getValorFrete());
		mv.addObject("valorDesconto", venda.getValorDesconto());
		mv.addObject("valorTotalItens", tabelaItens.getValorTotal(venda.getUuid()));
		return mv;
	}
	
	@PostMapping(value = "/nova", params = "salvar") //aula 23-15 11:00
	public ModelAndView salvar(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) { //o @Valid foi retirado pra pra validar dentro do método no vendaValidator. Aula 23:16 13:43
		validarVenda(venda, result);
		if(result.hasErrors()) {//A validação pra salvar aqui vai usar o VendaValidator que criamos no pacote validator. Aula 23-16 06:56
			return nova(venda); //não esquecer de acrescentar no html do cadastroVenda o brewer:message
		}
				
		venda.setUsuario(usuarioSistema.getUsuario());
		
		cadastroVendaService.salvar(venda);
		attributes.addFlashAttribute("mensagem", "Venda salva com sucesso");
		return new ModelAndView("redirect:/vendas/nova");
	}

	
	@PostMapping(value = "/nova", params = "emitir") //aula 23-17 08:04
	public ModelAndView emitir(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) { //o @Valid foi retirado pra pra validar dentro do método no vendaValidator. Aula 23:16 13:43
		validarVenda(venda, result);
		if(result.hasErrors()) {
			return nova(venda); 
		}
				
		venda.setUsuario(usuarioSistema.getUsuario());
		
		cadastroVendaService.emitir(venda);
		attributes.addFlashAttribute("mensagem", "Venda emitida com sucesso");
		return new ModelAndView("redirect:/vendas/nova");
	}
	
	@PostMapping(value = "/nova", params = "enviarEmail") //aula 23-17 08:04
	public ModelAndView enviarEmail(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) { //o @Valid foi retirado pra pra validar dentro do método no vendaValidator. Aula 23:16 13:43
		validarVenda(venda, result);
		if(result.hasErrors()) {
			return nova(venda); 
		}
				
		venda.setUsuario(usuarioSistema.getUsuario());
		
		venda = cadastroVendaService.salvar(venda);//o objeto venda já tem a alteração do banco depois de salvar que alteramos em saveAndFlush no service
		
		mailer.enviar(venda);
		
		//mailer.enviar();
		//System.out.println("### Logo após a chamado ao método enviar."); //PARA TESTAR A MENSAGERIA ASSÍNCRONA AULA 24-3
		
		attributes.addFlashAttribute("mensagem", String.format("Venda nº %d salva e e-mail enviado", venda.getCodigo()));//agora que temos o retorno do que foi salvo no banco podemos informar sucesso na venda código venda.getCodigo no (%d). Aula 24-7 09:44
		return new ModelAndView("redirect:/vendas/nova");
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
	
	@GetMapping
	public ModelAndView pesquisar(VendaFilter vendaFilter,
			@PageableDefault(size = 10) Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("/venda/PesquisaVendas");
		mv.addObject("todosStatus", StatusVenda.values());
		mv.addObject("tiposPessoa", TipoPessoa.values());
		
		PageWrapper<Venda> paginaWrapper = new PageWrapper<>(vendas.filtrar(vendaFilter, pageable)
				, httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		
		return mv;
	}
	/*
	 	Para realizar a edição também tem que mudar o cadastroVenda e PesquisaVenda.html
	 	Também lembrar de setar a dataCriacao() em CadastroVendaService no método salvar. 25-4 10:15
	 */
	@GetMapping("/{codigo}")
	private ModelAndView editar(@PathVariable Long codigo) {//Mesma ideia da edição de usuarios. Buscar vendas com os itens carregados. Aula 25-4 02:30.
		Venda venda = vendas.buscarComItens(codigo);
		
		//Depois que carregamos a 'venda' temos que percorrer ela e inserir as vendas dentro da tabelaVendas. Aula 25-4 04:33
		setUuid(venda);//precisamos mudar a logica antiga aqui e setar o uuid em metodo externo para poder pegar ele lá no novo e depois aqui no adicionarItem, se não vai dar erro. 25-4 06:20
		for (ItemVenda item : venda.getItens()) {
			tabelaItens.adicionarItem(venda.getUuid(), item.getCerveja(), item.getQuantidade());
		}
		
		ModelAndView mv = nova(venda);
		mv.addObject(venda);
		
		return mv;
	}
	
	@PostMapping(value = "/nova", params = "cancelar")
	public ModelAndView cancelar(Venda venda, BindingResult result
				, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		try {
			cadastroVendaService.cancelar(venda);
		} catch (AccessDeniedException e) {
			return new ModelAndView("/403");//pode mandar para qq página, mas vamos ficar com essa mesmo. 25-5 08:50
		}
		
		attributes.addFlashAttribute("mensagem", "Venda cancelada com sucesso");
		return new ModelAndView("redirect:/vendas/" + venda.getCodigo());
	}
	
	@GetMapping("/totalPorMes")
	public @ResponseBody List<VendaMes> listarTotalVendaPorMes() {
		return vendas.totalPorMes();
	}
	
	@GetMapping("/porOrigem")
	public @ResponseBody List<VendaOrigem> vendasPorNacionalidade() {
		return this.vendas.totalPorOrigem();
	}

	private ModelAndView mvTabelaItensVenda(String uuid) {
		ModelAndView mv = new ModelAndView("venda/TabelaItensVenda");
		mv.addObject("itens", tabelaItens.getItens(uuid));
		mv.addObject("valorTotal", tabelaItens.getValorTotal(uuid));//para capturar o valor total para exibir no quadro da tela de vendas. Aula 23:12 07:30
		
		return mv;
	}

	private void validarVenda(Venda venda, BindingResult result) {
		venda.adicionarItens(tabelaItens.getItens(venda.getUuid()));		
		venda.calcularValorTotal();//calcula o valor total antes de entrar na validação da tela para setar o valor total no objeto. Aula 23-16 26:59
		
		//Esse recurso do spring tira a validação na hora que entra na assinatura no @Valid e passa a executar a validação nesse momento os atributos da tela. Aula 23:16 - 14:22
		vendaValidator.validate(venda, result);
	}
	
	private void setUuid(Venda venda) {
		if(StringUtils.isEmpty(venda.getUuid())) {
			venda.setUuid(UUID.randomUUID().toString());			
		}
	}

}
