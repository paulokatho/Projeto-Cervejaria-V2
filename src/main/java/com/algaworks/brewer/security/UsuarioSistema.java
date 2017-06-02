package com.algaworks.brewer.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.algaworks.brewer.model.Usuario;

//classe usadad para guardar o usuario do sistema que está logado
public class UsuarioSistema extends User{

	private static final long serialVersionUID = 1L;
	
	private Usuario usuario;
	
	public UsuarioSistema(Usuario usuario, Collection<? extends GrantedAuthority> authorities) {
		super(usuario.getEmail(), usuario.getSenha(), authorities);	//cria essa super classe para setar o usuario e verificar se o email e a senha estão corretos, esse é o objeto que estamos criando no User que extendemos no "extends"
		this.usuario = usuario; //e assim agora no barraNavegacao.html podemos usar o nome de usuario para exibir na tela de quem efetuou o login
	}
	
	public Usuario getUsuario() {
		return usuario;
	}
	
}
