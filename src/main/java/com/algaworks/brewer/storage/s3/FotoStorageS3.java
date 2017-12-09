package com.algaworks.brewer.storage.s3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.storage.FotoStorage;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;

import net.coobird.thumbnailator.Thumbnails;

/***
 * 
 * @author Katho
 *
 * Para aprender mais sobre a API da S3 de armazenamento na nuvem podemos ir no google e digitar 'Amazon s3 sdk java' e ir aprendendo 
 *  como usar melhor a API.
 *  
 *  Quando esta salvando no try/catch é possivel configurar já o S3 para dar permissão de baixar ou gravar a imagem do S3, pois se não 
 *   der essa permissão ele deixa private e ninguem acessa. Quando sobe imagem manualmente no s3 tem que configurar isso nas propriedades
 *   do repositorio que voce criou, alem de ter que dar essa permissão uma a uma na mão. Aula 28.06 07:36
 */

/*
 * Quando eu quiser ativar o @Profile(prod) temos que clicar duas vezes no nosso servidor tomcat e então clicar em <Open Launch Configuration>
 * 	aba <Environment> clicar em <new>.
 *  Name: spring.profile.active
 *  Value: prod // aqui é o nome do nosso profile de produção 
 *  
 *  Então dessa maneira quando o nosso tomcat subir ele vai salvar nossas fotos lá no amanzonS3. 
 *  Agora temos o profile de produção e local.
 *  
 *  *** EU NÃO FIZ ESSE DE PRODUÇÃO, PORQUE NÃO TENHO CARTÃO DE CREDITO E NÃO DA PRA USAR SEM CADASTRAR UM CARTÃO. ***
 *  	Depois voltar na aula 26.8 16:20 para testar, quando cadastrar um cartão de credito.
 */

@Profile("prod")
@Component
public class FotoStorageS3 implements FotoStorage{
	
	private static final Logger logger = LoggerFactory.getLogger(FotoStorageS3.class);
	
	private static final String BUCKET = "awkatho";
	
	@Autowired
	private AmazonS3 amazonS3;//injetando nosso cliente que vai armazenar as fotos na nuvem. Aula 28.6

	@Override
	public String salvar(MultipartFile[] files) {
		String novoNome = null;
		if(files != null && files.length > 0) {
			MultipartFile arquivo = files[0];
			novoNome = renomearArquivo(arquivo.getOriginalFilename());
			
			try {//sempre pode dar exception aqui, então tem que colocar no try/catch
				AccessControlList acl = new AccessControlList();
				acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);//dando permissão no s3 pra poder inserir e ler as imagens que gravamos lá 
				
				enviarFoto(novoNome, arquivo, acl);				
				enviarThumbnail(novoNome, arquivo, acl);
			} catch (IOException e) {
				throw new RuntimeException("Erro ao gravar imagem no S3", e);
			}
		}
		return novoNome;
	}

	@Override
	public byte[] recuperar(String foto) {
		InputStream is = amazonS3.getObject(BUCKET, foto).getObjectContent();
		try {
			return IOUtils.toByteArray(is);
		} catch (IOException e) {
			logger.error("Não conseguiu recuperar foto do S3", e);
		}
		return null;
	}

	@Override
	public byte[] recuperarThumbnail(String foto) {
		return recuperar(FotoStorage.THUMBNAIL_PREFIX + foto);

	}
	
	@Override
	public void excluir(String foto) {
		amazonS3.deleteObjects(new DeleteObjectsRequest(BUCKET).withKeys(foto, THUMBNAIL_PREFIX + foto));//aqui apagamos tanto  foto quanto o thumbnail la do servidor amazon
		
	}

	@Override
	public String getUrl(String foto) {
		if(!StringUtils.isEmpty(foto)) {
			return "https://s3-sa-east-1.amazonaws.com/awkatho/" + foto; //essa url eu consigo bucket que criamos la na amazon. É so clicar em properties e pegar o de algum foto que está la e tirar o nome da foto
		}
		
		return null;
	}

	private ObjectMetadata enviarFoto(String novoNome, MultipartFile arquivo, AccessControlList acl)
			throws IOException {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(arquivo.getContentType());
		metadata.setContentLength(arquivo.getSize());//sempre setar o tamanho, pois se não ele vai tentar ficar calculando tamanho e pode estourar exception
		amazonS3.putObject(new PutObjectRequest(BUCKET, novoNome, arquivo.getInputStream(), metadata)
					.withAccessControlList(acl));//put é para enviar para a amazon.
		return metadata;
	}

	private void enviarThumbnail(String novoNome, MultipartFile arquivo, AccessControlList acl)
			throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();//aqui já vamos salvar o thumbnail também
		Thumbnails.of(arquivo.getInputStream()).size(40, 68).toOutputStream(os);
		byte[] array = os.toByteArray();		
		InputStream is = new ByteArrayInputStream(array);				
		ObjectMetadata thumbMetadata = new ObjectMetadata();
		thumbMetadata.setContentType(arquivo.getContentType());
		thumbMetadata.setContentLength(array.length);//o size aqui é o array pois a imagem ja foi convertida no byte[] array
		amazonS3.putObject(new PutObjectRequest(BUCKET, THUMBNAIL_PREFIX + novoNome, is, thumbMetadata)
					.withAccessControlList(acl));
	}

}
