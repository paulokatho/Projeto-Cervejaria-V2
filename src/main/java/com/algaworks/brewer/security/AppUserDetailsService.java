package com.algaworks.brewer.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.Usuarios;

//Essa classe é usada para fazer o login no sistema
@Service
public class AppUserDetailsService implements UserDetailsService{

	/*precisamos carregar um user pelo username que é o valor do e-mail: aula 19-4
		O username vai ser o email que está na tela de Login.html, ou seja o nome do atributo que está lá.
		Também temos que fazer a busca no repositorio/herper de usuario por email e ativo.
	*/
	
	@Autowired
	private Usuarios usuarios;
	
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {//esse método lança uma exception que temos que tratar no "orElseThrow()"
		Optional<Usuario> usuarioOptional = usuarios.porEmailEAtivo(email);
		//tentamos pegar um usuário, mas caso não consigamos lançamos a exception
		//usamos a expressão lambda "->" para lançar a exception
		Usuario usuario = usuarioOptional.orElseThrow(() -> new UsernameNotFoundException("Usuário e/ou senhas incorretos"));
				
		//return new User(usuario.getEmail(), usuario.getSenha(), new HashSet<>()); //Aqui é onde se cadastra as Role() que esta no SecurityConfig, o HasSet está vazio somente passando a senha e usuario, mas o certo é pegar do banco as Roles()
		return new UsuarioSistema(usuario, getPermissoes(usuario));
	}


	private Collection<? extends GrantedAuthority> getPermissoes(Usuario usuario) {
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		
		// Lista permissoes do usuario
		List<String> permissoes = usuarios.permissoes(usuario);
		//permissoes.forEach(p -> authorities.add(new SimpleGrantedAuthority("ROLE_" + p.toUpperCase())));//ROLE_ concatena com hasRole que tem em SecurityConfig
		permissoes.forEach(p -> authorities.add(new SimpleGrantedAuthority("ROLE_" + p.toUpperCase())));
		return authorities;
		
		/*
	 	Obs: O ROLE_ que está comentado é porque em SecurityConfig no hasRole está somente "CADASTRAR_USUARIO", por exemplo.
	 			E sempre tem que ter o ROLE_ quando se utilizar o hasRole, ou então pode-se acrescentar o ROLE_ direto no banco nas permissoes
	 			assim: ROLE_CADASTRAR_USUARIO. De qualquer forma em algum lugar tem que estar assim.
	 			Mas tem outra opção que é o hasAuthority aí não precisa ter o ROLE_
	 			
	 			Na aula 19-6 foi feito roll back no banco para poder cadastrar o ROLE_ e ficar armazenado no banco certinho, e também tem 
	 			que apagar o schema, permissao, grupo_permissao e usuario_grupo, conforme a aula no minuto 21:48
		 */
	}

}