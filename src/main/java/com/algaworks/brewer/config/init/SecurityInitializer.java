
package com.algaworks.brewer.config.init;

import java.util.EnumSet;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.SessionTrackingMode;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.filter.CharacterEncodingFilter;

public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {

	 @Override
	 protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
		
		//servletContex armazena o valor o jSessionId em cookie ao invés de exibir na url do browser
		// para configurarmos o tempo que a sessão fica ativa antes de sair qdo ninguem mexe ficará dentro de web.xml
		servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
		 
		FilterRegistration.Dynamic characterEncodingFilter = servletContext.addFilter("encodingFilter",
				new CharacterEncodingFilter());		
	    characterEncodingFilter.setInitParameter("encoding", "UTF-8"); //tipo de encoding
	    characterEncodingFilter.setInitParameter("forceEncoding", "true"); //forca usar o encoding sempre
	    characterEncodingFilter.addMappingForUrlPatterns(null, false, "/*"); //onde? em todas as url's
	 }
}