package com.algaworks.brewer.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.algaworks.brewer.model.Venda;

@Component
public class Mailer {

	@Autowired
	private static Logger logger = LoggerFactory.getLogger(Mailer.class);
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private TemplateEngine thymeleaf;//thymeleaf que vai renderizar o html da pagina ResumoVenda quando o WebConfig carregar o thymeleaf
	
//	@Async
//	public void enviar(Venda venda) {
//		Context context = new Context();//vamos setar algumas variaveis e algumas coisas no context. Esse conteúdo será passado para o ResumoVenda.html
//		context.setVariable("venda", venda);//essa venda vai para o th:block/th:each
//				
//		try {
//			String email = thymeleaf.process("mail/ResumoVenda", context);//processando o corpo do email que está na pasta mail e arquivo ResumoVenda
//			MimeMessage mimeMessage = mailSender.createMimeMessage();
//			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");//é para enviar mensagem com html e outroas coisas, multipart = true, pois tem imagens e encoding = utf-8
//			helper.setFrom("pkatho@gmail.com");
//			helper.setTo(venda.getCliente().getEmail());
//			helper.setSubject("Brewer - Venda realizada!");
//			helper.setText(email, true);//texto do email é a pagina html e vai conter imagem = true
//			
//			mailSender.send(mimeMessage);			
//		} catch (MessagingException e) { //pode ser feita uma tratativa para salvar no banco de dados ou para armazenar e ir verificando os que não foram e fazer reenvios
//			logger.error("Erro enviando e-mail", e);
//		}
		
		
//	}
	
	
	
	
	
		/******	Método para testar se realmente está funcionando a mensageria assíncrona ******/
//	public void enviar() {
//		System.out.println(">>>> enviando e-mail...");
//		
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		
//		System.out.println(">>>>> e-mail enviando!");
//	}
	
/***	Método para testar o envio de mensagens assíncronas - Aula 24-4***/

//	@Autowired
//	private JavaMailSender mailSender;//configuramos o mailSender em MailConfig
	
	@Async
	public void enviar(Venda venda) {
		SimpleMailMessage mensagem = new SimpleMailMessage();
		mensagem.setFrom("pkatho@gmail.com");
		mensagem.setTo(venda.getCliente().getEmail());
		mensagem.setSubject("Venda Efetuada");
		mensagem.setText("Obrigado, venda processada com sucesso!");
		
		mailSender.send(mensagem);
	}

	
}
