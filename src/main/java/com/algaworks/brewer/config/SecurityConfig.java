package com.algaworks.brewer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.algaworks.brewer.security.AppUserDetailsService;

@EnableWebSecurity
@ComponentScan(basePackageClasses = AppUserDetailsService.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

			/*Esse é usado quando fizemos teste, pois ele gera o usuário somente em memória
			@Override
			protected void configure(AuthenticationManagerBuilder auth) throws Exception {			
			auth.inMemoryAuthentication()
			.withUser("admin").password("admin").roles("CADASTRO_CLIENTE");
		}
		*/
		
		@Autowired//injetanto user detail, mas para injetar aqui é necessário a assinatura @ComponentScan na classe e a implementação da classe AppUserDetailsService   
		private UserDetailsService userDetailsService;
		
		/*
		 	Aqui agora é para gravar no banco de dados e utilizar o usuario/senha cadastrado
		 	É necessário chamar o passwordEncoder para encodar a senha.
		 	userDetail, voce vai autenticar o usuário, mas lembra que a senha precisa encodar para fazer a validação. Quem faz a compração
		 		da senha é o spring security, nós somente buscamos no banco de dados pelo e-mail e se está ativo.
		 	O passwordEncoder() dentro dos parenteses serve para avisar o spring security que está encodado e para o security verificar a comparação. 
		 */
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {			
			auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		}
		
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.antMatchers("/layout/**")//tudo o que estiver depois de layout pode liberar(ex: css, js), pois se não na tela não permite exibir esses estilos	
			.antMatchers("/images/**");
	};
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	//*** A ORDEM EM QUE OS ITENS SÃO IMPLEMENTADOS FAZ TODA A DIFERENÇA
		http
			.authorizeRequests()//autoriza as requisições
				.antMatchers("/cidades/nova").hasRole("CADASTRAR_CIDADE")// Role() são recuperadas no banco no grupo de usuarios
				.antMatchers("/usuarios/**").hasRole("CADASTRAR_USUARIO")
				.anyRequest().authenticated()//tudo tem que estar autenticado, daqui para baixo não é nada liberado e tudo que não for autenticado retorna para a tela de login
				.and()
			.formLogin()
				.loginPage("/login")
				.permitAll()
				.and()
			.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.and()
			.exceptionHandling()
				.accessDeniedPage("/403")
				.and()
			.sessionManagement()
				.invalidSessionUrl("/login");
				//.maximumSessions(1) //maximo de sessoes permitidas é somente uma
				//.expiredUrl("/login"); // se uma segunda requisição chegar a primeira é redirecionada para /login
				//.and()
			//.csrf().disable();
		//Obs: Ver em AppUserDetailsService para ver o porque de se usar o hasRole e não hasA uthority
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
