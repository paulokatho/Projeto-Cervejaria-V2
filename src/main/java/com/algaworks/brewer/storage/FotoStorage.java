package com.algaworks.brewer.storage;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;


/***
 * 
 * @author Katho
 * Na aula 28:05 em torno de 04:45 para traz, mexemos no codigo pra tirar o armazenamento da foto temporaria, pois para armazenar
 *  na amazon s3 daria muito trabalho.
 * Mexemos nas classes FotoStorage, FotoStorageLocal, FotoStorageRunnable, apagamos classe cerveja listener do pacote
 *  service.event.cerveja. Apagamos o pacote todo do event.cerveja no tempo 04:55.
 * E dentro do pacote service na classe CadastroCervejaService tiramos do metodo salvar o publishEvent e também a injeção de dependencia
 *  dele la dentro. Vai estar comentado lá.
 * Também mexemos na classe FotoController no metodo recuperaFotoTemporaria...apagamos ela.
 * Foi retirado o @Bean da foto da classe ServiceConfig. Tem explicação lá.
 * Transformamos a fotoStorageLocal em um @Component
 * E também a classe FotoStorageS3 virou @Component
 *  - Esses dois ultimos implementam a interface FotoStorage
 * Quando 2 @Component utilizam a mesma interface temos que ter um lugar que irá scanear eles.
 *  Para isso no ServiceConfig Scaneamos ele lá
 *  Também temos que mudar no CadastroVenda.html, onde fazemos a busc da cerveja. Aula 28.05 36:40
 *   - Para isso Temos que configurar TemplateAutocompleteCerveja.html também
 *   
 *  renomearArquivo() foi colocado na aula 28.6 03:35, a partir do java 8 é possivel colocar metodo na interface
 */
public interface FotoStorage { 

	//public String salvarTemporariamente(MultipartFile[] files); -- Metodo alterado aula 28.5 03:03
	
	public final String THUMBNAIL_PREFIX = "thumbnail.";//usado em CervejaEntityListener Aula 28.05 33:25
	
	public String salvar(MultipartFile[] files);

	//public byte[] recuperarFotoTemporaria(String nome);//Metodo retirado na aula 28.05 igual o salvarTemporariamente

	//public void salvar(String foto); //Metodo retirado na aula 28.05 igual o salvarTemporariamente

	public byte[] recuperar(String foto);
	
	public byte[] recuperarThumbnail(String fotoCerveja);//para recuperar thumbnail da imagem para exibir no email em Mailer. Aula 24-6 27:10

	public void excluir(String foto);

	public String getUrl(String foto);
	
	default String renomearArquivo(String nomeOriginal) { //agora da pra usar esse metodo nas duas classe concretas ( FotoStorageLocal e FotoStorageS3)
		return UUID.randomUUID().toString() + "_" + nomeOriginal;
		
	}
	
}
