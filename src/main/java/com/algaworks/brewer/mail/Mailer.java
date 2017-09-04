package com.algaworks.brewer.mail;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.storage.FotoStorage;

@Component
public class Mailer {

	@Autowired
	private static Logger logger = LoggerFactory.getLogger(Mailer.class);
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private TemplateEngine thymeleaf;//thymeleaf que vai renderizar o html da pagina ResumoVenda quando o WebConfig carregar o thymeleaf
	
	@Autowired
	private FotoStorage fotoStorage;
	
	@Async
	public void enviar(Venda venda) {
		Context context = new Context();//vamos setar algumas variaveis e algumas coisas no context. Esse conteúdo será passado para o ResumoVenda.html
		context.setVariable("venda", venda);//essa venda vai para o th:block/th:each
		context.setVariable("logo", "logo");
				
		Map<String, String> fotos = new HashMap<>();
		boolean adicionarMockCerveja = false;
		for (ItemVenda item : venda.getItens()) {//percorrendo a lista para pegar uma venda que tenha foto e adicionar na variavel cid
			Cerveja cerveja = item.getCerveja();
			if (!StringUtils.isEmpty(cerveja.getFoto())) {//depois de carregar a cerveja, se getFoto NÃO for vazia, executa o codigo abaixo. Aula 24-5 35:55
				String cid = "foto-" + cerveja.getCodigo();//add o nome pra as fotos de cada cerveja do Map
				context.setVariable(cid, cid);
				
				fotos.put(cid, cerveja.getFoto() + "|" + cerveja.getContentType());//jogando o Id-cid, foto e contentType de foto/cerveja dentro do map fotos
			} else {
				adicionarMockCerveja = true;
				context.setVariable("mockCerveja", "mockCerveja");//vamos setar o mock da cerveja no lugar da imagem da cerveja que vai para o email. Aula 24-6 36:30
			}
		}
		
		try {
			String email = thymeleaf.process("mail/ResumoVenda", context);//processando o corpo do email que está na pasta mail e arquivo ResumoVenda
			
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");//é para enviar mensagem com html e outroas coisas, multipart = true, pois tem imagens e encoding = utf-8
			helper.setFrom("pkatho@gmail.com");
			helper.setTo(venda.getCliente().getEmail());
			helper.setSubject(String.format("Brewer - Venda nº %d", venda.getCodigo()));//adicionando o codigo da venda. Para isso adicionou saveAndFlush no cadastroVendaService e no controller venda recebeu o retorno de saveAndFlush. Aula 24-7 08:18
			helper.setText(email, true);//texto do email é a pagina html e vai conter imagem = true
			
			helper.addInline("logo", new ClassPathResource("static/images/logo-gray.png"));//para utilizar o th:src no ResumoVenda
			
			if(adicionarMockCerveja) {//se o mockCerveja for true, não existe a imagem da cerveja na cerveja e adiciona o mock dela no email
				helper.addInline("mockCerveja", new ClassPathResource("static/images/cerveja-mock.png"));
			}
			
			for (String cid : fotos.keySet()) {//percorrendo todos os 'cid' do HashMap de fotos
				String[] fotoContentType = fotos.get(cid).split("\\|");//no split tem que ter 2 barras para o sistema não achar que é 'ou'. Isso é uma expressao regular
				String foto = fotoContentType[0];
				String contentType = fotoContentType[1];
				byte[] arrayFoto = fotoStorage.recuperarThumbnail(foto);//recuperando o thumbnail de cada foto
				helper.addInline(cid, new ByteArrayResource(arrayFoto), contentType);//para cada Id(cid) de foto de cerveja, tenho que processar e inserir no ByteArrayResource				
			}
			
			
			mailSender.send(mimeMessage);			
		} catch (MessagingException e) { //pode ser feita uma tratativa para salvar no banco de dados ou para armazenar e ir verificando os que não foram e fazer reenvios
			logger.error("Erro enviando e-mail", e);
		}
		
		
	}
	
	
	
	
	
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
/*	
	@Async
	public void enviar(Venda venda) {
		SimpleMailMessage mensagem = new SimpleMailMessage();
		mensagem.setFrom("pkatho@gmail.com");
		mensagem.setTo(venda.getCliente().getEmail());
		mensagem.setSubject("Venda Efetuada");
		mensagem.setText("Obrigado, venda processada com sucesso!");
		
		mailSender.send(mensagem);
	}
*/
	
}
