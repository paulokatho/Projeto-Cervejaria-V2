package com.algaworks.brewer.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.Grupos;
import com.algaworks.brewer.repository.Usuarios;
import com.algaworks.brewer.repository.filter.UsuarioFilter;
import com.algaworks.brewer.service.CadastroUsuarioService;
import com.algaworks.brewer.service.StatusUsuario;
import com.algaworks.brewer.service.exception.EmailUsuarioJaCadastradoException;
import com.algaworks.brewer.service.exception.SenhaObrigatoriaUsuarioException;

@Controller
@RequestMapping("/usuarios")
public class UsuariosController {
	
	@Autowired
	private CadastroUsuarioService cadastroUsuarioService;

	@Autowired
	private Grupos grupos;
	
	@Autowired
	private Usuarios usuarios;
	
	@RequestMapping("/novo")
	public ModelAndView novo(Usuario usuario) {
		ModelAndView mv = new ModelAndView("usuario/CadastroUsuario");
		mv.addObject("grupos", grupos.findAll());
		return mv;
	}
	
	@PostMapping({"/novo", "{\\+d}" })
	public ModelAndView salvar(@Valid Usuario usuario, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return novo(usuario);
		}
		
		try {
			cadastroUsuarioService.salvar(usuario);
		} catch (EmailUsuarioJaCadastradoException e) {
			result.rejectValue("email", e.getMessage(), e.getMessage());
			return novo(usuario);
		} catch (SenhaObrigatoriaUsuarioException e) {
			result.rejectValue("senha", e.getMessage(), e.getMessage());
			return novo(usuario);
		}
		
		attributes.addFlashAttribute("mensagem", "Usuário salvo com sucesso");
		return new ModelAndView("redirect:/usuarios/novo");
	}
	
	@GetMapping
	public ModelAndView pesquisar(UsuarioFilter usuarioFilter
			, @PageableDefault(size = 3) Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("/usuario/PesquisaUsuarios");
		/*dica: para implementar o filtrar(usuarioFilter) temos que acrescentar esse método no filter, usuariosQueries e UsuariosImpl
			e também no UsuariosImpl para pegar a sessão usar o Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class),
			utilizar esse padrão para pegar sessão.
			Também criar UsuarioGrupo e UsuarioGrupoId em Models
		*/ 
		mv.addObject("grupos", grupos.findAll());
		PageWrapper<Usuario> paginaWrapper = new PageWrapper<>(usuarios.filtrar(usuarioFilter, pageable)
				, httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		//mv.addObject("usuarios", usuarios.filtrar(usuarioFilter));//foi tirado na aula 21-6 01:35
		return mv;
	}
	
	//Criar PesquisaUsuarios, brewer.css, UsuariosController, multiselecao.js	
	@PutMapping("/status")
	@ResponseStatus(HttpStatus.OK)
	public void atualizarStatus(@RequestParam("codigos[]")Long[] codigos, @RequestParam("status") StatusUsuario statusUsuario) {
		//Arrays.asList(codigos).forEach(System.out::println);// para testes se esta recebendo o código
		//System.out.println("Status: " + status); //teste para ver se está recebendo o status (ativado, desativado)
		
		/*Vamos passar para a classe serviço para tratarmos essas informações
		Também ver no package Serviço o Enum 'StatusUsuario' que foi feito para o otimizar caso seja tenha mais opção de estavo e não só (ativo/desativado)*/
		cadastroUsuarioService.alterarStatus(codigos, statusUsuario);
		
		/*
		 	Recaptulando aula 21.4.
		 	
		 	Criamos o Enun StatusUsuario para ficar mais facil implementar novas regras que não sejam somente ativo e inativo.
		 	Em cadastro usuario service foi criado o método alterarStatus que recebe esse enum e não precisa implementar regras, pois a regra já está no 
		 		StatusUsuario e o spring data já consegue interpretar o que está vindo da tela quando o botão ativo/inativo é clicado.
		 	Também na classe Usuario.java foi criado preUpdate() que para confirmar a senha, pois o security verifica se a senha do usuario para ver se
		 		ele deixa ou não ativar/desativar, isso é feito antes de fazer o update de fato.
		 		Foi colocado a assintura @DynamicUpdate que faz a atualização somente do campo/atributo que realmente está sendo alterado e não de todos
		 			os atributos de Usuario.
		 	Aqui em usuariosController estamos recebendo um array de codigos[] e um enumerador StatusUsuario que chega com o que o usuario
		 		clicou (ativo/inativo) na tela de pesquisaUsuarios
		 	Tem o javascript multiselecao.js que pegamos o parametro 'data-status' do thymeleaf na pagina pesquisaUsuarios.html (status: ativo/inativo) pra recebermos
		 		o status e conseguirmos pegar ele no UsuariosController.
		 	
		 	Em brewer.css foi somente acrescentado '.table-usuarios-col-status: width = 30px;'
		 */
	}
	
	/**
	 * Aqui temos que tratar o relacionamento com grupo, pois tem o ralacionamento manyToMany e por default tudo que tem 'ToMany' não inicializado
	 * 	com o objeto principal, nesse caso usuario.
	 * Em Usuario.java no @ManyToMany não podemos usar o fechType.EAGER para inicializar grupos e corrigir o problema, pois nao queremos carregar todos os 
	 * 	grupos quando Usuario for carregado. Então vamos inicializar somente quando nós precisarmos. Isso quer dizer buscar Usuario já com os grupos só na 
	 * 	edição.
	 * E por isso vamos inicializar o usuario com os grupos no método, usuarios.buscarComGrupos(codigo), buscando por código.
	 * Mudar UsuariosQueries
	 * 
	 * @param usuario
	 * @return
	 */
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {//o Long codigo é recebe o "/{codigo}", nesse formato para corrigir problema de edição com ManyToMany de grupos.
		Usuario usuario = usuarios.buscarComGrupos(codigo);
		ModelAndView mv = novo(usuario);
		mv.addObject(usuario);
		
		return mv;
	}
}
