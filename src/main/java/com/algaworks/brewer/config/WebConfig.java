package com.algaworks.brewer.config;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeansException;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.algaworks.brewer.config.format.BigDecimalFormatter;
import com.algaworks.brewer.controller.CervejasController;
import com.algaworks.brewer.controller.converter.CidadeConverter;
import com.algaworks.brewer.controller.converter.EstadoConverter;
import com.algaworks.brewer.controller.converter.EstiloConverter;
import com.algaworks.brewer.controller.converter.GrupoConverter;
import com.algaworks.brewer.session.TabelasItensSession;
import com.algaworks.brewer.thymeleaf.BrewerDialect;
import com.github.mxab.thymeleaf.extras.dataattribute.dialect.DataAttributeDialect;
import com.google.common.cache.CacheBuilder;

import nz.net.ultraq.thymeleaf.LayoutDialect;

@Configuration
@ComponentScan(basePackageClasses = { CervejasController.class, TabelasItensSession.class })
@EnableWebMvc
@EnableSpringDataWebSupport
@EnableCaching
@EnableAsync //habilita mensagens assíncronas do Mailer, mas tem que colocar @Async na classe Mailer também. Aula 24-3 02:38
public class WebConfig extends WebMvcConfigurerAdapter implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Bean
	public ViewResolver viewResolver() {
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(templateEngine());
		resolver.setCharacterEncoding("UTF-8");
		return resolver;
	}

	@Bean
	public TemplateEngine templateEngine() {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setEnableSpringELCompiler(true);
		engine.setTemplateResolver(templateResolver());
		
		engine.addDialect(new LayoutDialect());
		engine.addDialect(new BrewerDialect());
		engine.addDialect(new DataAttributeDialect());
		engine.addDialect(new SpringSecurityDialect());
		return engine;
	}

	private ITemplateResolver templateResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setCharacterEncoding("UTF-8");
		resolver.setApplicationContext(applicationContext);
		resolver.setPrefix("classpath:/templates/");
		resolver.setSuffix(".html");
		resolver.setTemplateMode(TemplateMode.HTML);
		return resolver;
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
	}
	
	@Bean
	public FormattingConversionService mvcConversionService() {
		DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
		conversionService.addConverter(new EstiloConverter());
		conversionService.addConverter(new CidadeConverter());
		conversionService.addConverter(new EstadoConverter());
		conversionService.addConverter(new GrupoConverter());
		
		/*Recurso do Spring. Formato para garantir que venha com pelo menos 1 casa decimal a esquerda, mas ele utiliza o Locale do browser 
		sendo assim, se o browser estiver em ingles ele não consegue converter para bigDecimal. 
		Por isso não podemos usar o bigDecimalFormater e nem o integerFormater.
		Vamos criar um formatador de tipo com a ajuda do Spring e está dentro o package config.format. Aula 27-2 11:15
		*/
		//NumberStyleFormatter bigDecimalFormatter = new NumberStyleFormatter("#,##0.00");
		BigDecimalFormatter bigDecimalFormatter	 = new BigDecimalFormatter("#,##0.00");
		conversionService.addFormatterForFieldType(BigDecimal.class, bigDecimalFormatter);
		
		//NumberStyleFormatter integerFormatter = new NumberStyleFormatter("#,##0");
		BigDecimalFormatter integerFormatter	 = new BigDecimalFormatter("#,##0");//o integer funciona utilizando a classe que criamos BigDecimalFormatter só mudando o padrão "#,##0"
		conversionService.addFormatterForFieldType(Integer.class, integerFormatter);
		
				//API datas do java 8 
		//- aula 18-5 - usado na tela de cadastro de usuário, mas é usado para qualquer campo data
		/*Também foi utilizado o bootstrap-datepicker para limitar o que é digitado no input data / importar no javascript/vendors
			- bootstrap-datepicker.min.js
			- bootstrap-datepicker.pt-BR.min.js
		*/	
		DateTimeFormatterRegistrar dateTimeFormatter = new DateTimeFormatterRegistrar();
		dateTimeFormatter.setDateFormatter(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		dateTimeFormatter.setTimeFormatter(DateTimeFormatter.ofPattern("HH:mm"));
		dateTimeFormatter.registerFormatters(conversionService);
		
		return conversionService;
	}
	
	/* . LocaleResolver é utilizado pra forçar o browser para exibir tudo em portugues
	 * . Ele foi colocado aqui para funcionar legal o NumberStyleFormatter utilizado no framework para formatação de moedas.
	 * . Foi retirado na aula 27-2 00:50 para implementar a internacionalização.
	@Bean
	public LocaleResolver localeResolver() {
		return new FixedLocaleResolver(new Locale("pt", "BR"));
	}*/
	
	/* Aula 17.1 e 17.2
	 *	Preciso deste método quando vou deixar algum método em cache, nesse caso esta´o cidades controller da aula 17-1 
	 */
/*	@Bean //esse cache não é muito recomendado por ser bem limitado, somente para aplicações bem pequenas ou coisas bem simples
	public CacheManager cacheManager() {
		return new ConcurrentMapCacheManager();
	}*/
	
	@Bean
	public CacheManager cacheManager() {
		CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder() //aqui está criando as opções que eu quero utilizar no meu cache
				.maximumSize(3)//somente 3 estados no comboEstados no cadastro de cliente
				.expireAfterAccess(20, TimeUnit.SECONDS);
		
		//Para usar esse Manager do Guava foi necessário acrescentar no pom.xml a dependencia dele para o 'spring context support'
		GuavaCacheManager cacheManager = new GuavaCacheManager();
		cacheManager.setCacheBuilder(cacheBuilder);//estas são as opções que eu quero para o cacheManager
		return cacheManager;
	}
	
	/***
	 * 	Nos campos data quanto utilizamos o data-picker do bootstrap, se digitamos alguma data inválida, ex: 90/00/0000 ele gera uma 
	 * exception na tela e esse método é para deixar a mensagem de erro mais amigável para o usuário.
	 * 
	 */
	@Bean
	public MessageSource messageSource () {
		ReloadableResourceBundleMessageSource bundle = new ReloadableResourceBundleMessageSource();
		bundle.setBasename("classpath:/messages");//esse messages.properties esta dentro da pasta src/main/resources. Também visto na aula 27-2 01:03
		/*
		 *	//http://www.utf8-chartable.de/
		 *	Este site serve para buscar o código utf-8 referente ao caracter que queremos buscar, ex: á. É só pegar o codigo, ex: EE09 referente ao á
		 *	Está sendo usado em message.properties / aula 18-5 
		 *
		 *	Lá no messages.properties:
		 *		typeMismatch.java.time.LocalDate = {0} inv\u00E1lida
		 *		usuario.dataNascimento = Data de Nascimento
		 *
		 *** usuario.dataNascimento vai para o 'zero' de {0} e esse valor é exibido na tela.
		 */
		bundle.setDefaultEncoding("UTF-8");//Link pra configuração utf-8 caracteres especiais pra usar no properties : http://www.utf8-chartable.de/
		return bundle;
	}
	
	//aula 23-10 08:20
	//precisa desse domain pra poder substituir o findOne no VendasController no @DeleteMapping... public ModelAndView excluirItem(@PathVariable("codigoCerveja")Cerveja cerveja)
	@Bean
	public DomainClassConverter<FormattingConversionService> domainClassConverter() {
		return new DomainClassConverter<FormattingConversionService>(mvcConversionService());
	}
	
	/*** Para realizar a internacionalização da validação que aparece na tela.
	 *   Vamos ter que acrescentar aqui um bean e sobrecarregar um método Aula 27-3 01:10 até 01:44
	 *   OBS: Olhar depois do metodo getValidator algumas observações. */
	@Bean
	public LocalValidatorFactoryBean validator() {
		LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
		//O setValidationMessage fala onde estão as mensagens de validação. E passamos o messageSource que sabe onde esta a nossa MessageProperties
		validatorFactoryBean.setValidationMessageSource(messageSource());
		
		return validatorFactoryBean;
	}
	
	/*** E sobrescrevendo o método getValidator da classe extendida WebMvcConfigurerAdapter e retornando o validator() O Spring já
	 * 	 vai saber buscar as mensagens de validação dos arquivos messages_en_US.properties, por exemplo.*/
	@Override
	public Validator getValidator() {
		return validator();
	}
	
	/* Temos que pensar nas assinaturas de validação que estão nas classes, por exemplo cerveja (@NotBlank(message = "SKU é obrigatório"))
	 * Para a validação @NotBlank, por exemplo, temos que encontrar qual é a chave (chave do @NotBlank) dessa assinatura, 
	 * 	encontrar onde ela está implementada.
	 * Para isso vamos nos pacotes .jar do MavenDependencies e vamos procurar o .jar hibernate-validator abrir o pacote dele, vai ter uns 
	 *  arquivos validationMessages.properties e aí escolher o arquivo pt_BR.properties. Essa chave se encontra dentro desse arquivo.
	 * La dentro vai ter algo como isso: org.hibernate.validator.constraints.NotBlank.message = N\u00E3o pode estar em branco
	 * Essa chave é que vamos copiar e colar no nosso messages.properties. Só conferir la no nosso messages_pt_BR.properties o NotBlank, vai
	 *  estar que a mensagem "é obrigatorio", mas com a mascara certinho.
	 * O que estiver lá entre {0} é para poder exibir o nome do campo, por exemplo 'SKU'. Ex: SKU é obrigatório.
	 * Conferir a Aula 27-3 03:10
	 * 
	 * O sistema para funcionar legal a internacionalização tem que ir traduzindo tudo, coisa a coisa e também prestar atenção no esquema
	 *  da moeda do país, pois a lígua muda, mas o dinheiro não...
	 * Pegar o que está no hibernate.validation>validationMessages e ir traduzindo tudo, por exemplo, o que tem no @NotBlank, @NotNull, etc. 
	 * Aí posso tirar o que está escrito na assinatura, onde está @NotBlank("SKU é obrigatorio"), 
	 *  deixo somente @NotBlank que ele vai pegar do messages.properties.
	 * Também tem que pegar as chaves de validação que estão no thymeleaf, por exemplo na pagina de CadastroVenda o campo 
	 * 	th:text="#{venda.cadastro.titulo}. Aula 27-8
	 * 
	 * */
	
}
