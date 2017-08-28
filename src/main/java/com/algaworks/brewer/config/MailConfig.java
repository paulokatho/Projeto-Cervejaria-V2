package com.algaworks.brewer.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.algaworks.brewer.mail.Mailer;

/***
 * 
 * @author Katho
 * "classpath:env/mail-${ambiente:local}.properties" 
 * . classpath vai procurar dentro do projeto a pasta env e vai concatenar com a string 'mail-' e caso ele não ache a string fica 'mail-local.properties'
 * 	. ambiente é o que será executado, por exemplo: ambiente: local ou homolog
 * . value é para buscar a senha em um arquivo externo que esteja em HOME e tenha o nome brewer-mail.properties
 * . ignoreResourceFound é para não dar erro enquanto a aplicação sobe, pois se não tiver o arquivo brewer-mail.properties, dá erro enquanto sobe 
 * OBS: No servidor tomcat para passar o arquivo mail-homolog.properties na hora que subir o servidor é só dar 2 clicks no servidor e ir na aba Environmente e digita 'Variable: ambiente e Value: homolog'. Com isso roda o arquivo de homolog 
 */
@Configuration
@ComponentScan(basePackageClasses = Mailer.class)//mapeando a classe Mailer, pois Mailer é um @Component
@PropertySource({ "classpath:env/mail-${ambiente:local}.properties" })
@PropertySource(value = { "file://${HOME}/.brewer-mail.properties" }, ignoreResourceNotFound = true)// no windows não funciona como na aula 24-2

//@PropertySources({
//	@PropertySource("classpath:env/mail-${ambiente:local}.properties"),
//	@PropertySource(value = "file:\\C:\\user\\Katho\\.brewer-mail.properties"
//		, ignoreResourceNotFound = true)
//})
public class MailConfig {
	
	@Autowired
	private Environment env;

	@Bean
	public JavaMailSender mailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.sendgrid.net");
		mailSender.setPort(587);
		mailSender.setUsername(env.getProperty("username"));//carrega usuario e senha do properties para questão de segurança
		mailSender.setPassword(env.getProperty("password"));
		
		mailSender.setUsername("PauloKatho");
		mailSender.setPassword("katho203079");
		
		System.out.println(">>> Usuario: " + mailSender.getUsername());
		System.out.println(">>> Password: " + mailSender.getPassword());
		
		
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", true);
		props.put("mail.smtp.starttls.enable", true);
		props.put("mail.debug", false);
		props.put("mail.smtp.connectiontimeout", 10000); // miliseconds

		mailSender.setJavaMailProperties(props);
		
		return mailSender;
	}
	
}
