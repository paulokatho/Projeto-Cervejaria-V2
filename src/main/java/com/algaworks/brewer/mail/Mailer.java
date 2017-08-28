package com.algaworks.brewer.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.algaworks.brewer.model.Venda;

@Component
@Async
public class Mailer {

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
	
	@Autowired
	private JavaMailSender mailSender;//configuramos o mailSender em MailConfig
	
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
