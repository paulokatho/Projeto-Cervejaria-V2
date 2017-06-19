package com.algaworks.brewer.config.init;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.algaworks.brewer.config.JPAConfig;
import com.algaworks.brewer.config.SecurityConfig;
import com.algaworks.brewer.config.ServiceConfig;
import com.algaworks.brewer.config.WebConfig;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { JPAConfig.class, ServiceConfig.class, SecurityConfig.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { WebConfig.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}
	
	/* Esse método implementando o CharacterEncondingFilter funcionou até implementarmos o Spring Security, após o security ele não 
	 * 	funciona mais. Ele é desabilitado pelo spring. O método getServletFilters() será impelemntado abaixo e o CharacterEncoding será
	 * 	implementado no SecurityInitializer, nesse filtro de segurança para o que o security entenda e não bloqueie ele.
	 * 
	  @Override
	protected Filter[] getServletFilters() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
		
        return new Filter[] { characterEncodingFilter };
	}*/
	@Override
	protected Filter[] getServletFilters() {
		/* httpPutFormContentFilter usado na tela de pesquisa de usuarios no checkbox para selecionar varios usuarios e quando clicar no 
		botão ativar ele passar o array contendo os valores do codigo do usuario. Se não colocar esse carinha dá um erro de 404 que não 
		permite ser usado o 'PUT'. Aula 21.4 : +~- 17:00 min.
		---Não pode esquecer de colocar em usuariosController no metodo atualizar status @ResponseStatus(HttpStatus.OK)---
		*/
		HttpPutFormContentFilter httpPutFormContentFilter = new HttpPutFormContentFilter();
		return new Filter[] { httpPutFormContentFilter };
	}
	
	@Override
	protected void customizeRegistration(Dynamic registration) {
		registration.setMultipartConfig(new MultipartConfigElement(""));
	}

}
