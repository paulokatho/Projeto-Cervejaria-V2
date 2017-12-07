package com.algaworks.brewer.storage.local;

import static java.nio.file.FileSystems.getDefault;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.storage.FotoStorage;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

@Profile("local")//Colocado na aula 28.05 para o Spring saber qual dos 2 bens ele deve injetar no FotosController. Explicação ServiceConfig
@Component
public class FotoStorageLocal implements FotoStorage {

	private static final Logger logger = LoggerFactory.getLogger(FotoStorageLocal.class);
	private static final String THUMBNAIL_PREFIX = "thumbnail.";
	
	private Path local;
	//private Path localTemporario; -- saiu aula 28.05
	
	public FotoStorageLocal() {
		this(getDefault().getPath(System.getenv("HOME"), ".brewerfotos"));
	}
	
	public FotoStorageLocal(Path path) {
		this.local = path;
		criarPastas();
	}

	@Override
	public String salvar(MultipartFile[] files) {
		String novoNome = null;
		if (files != null && files.length > 0) {
			MultipartFile arquivo = files[0];
			novoNome = renomearArquivo(arquivo.getOriginalFilename());
			try {
				arquivo.transferTo(new File(this.local.toAbsolutePath().toString() + getDefault().getSeparator() + novoNome));
			} catch (IOException e) {
				throw new RuntimeException("Erro salvando a foto", e);
			}
			try {
				Thumbnails.of(this.local.resolve(novoNome).toString()).size(40, 68).toFiles(Rename.PREFIX_DOT_THUMBNAIL);
			} catch (IOException e) {
				throw new RuntimeException("Erro gerando thumbnail", e);
			}
		}
		
		return novoNome;
	}
	
	/*@Override -- Saiu na aula 28.05 03:50
	public byte[] recuperarFotoTemporaria(String nome) {
		try {
			return Files.readAllBytes(this.localTemporario.resolve(nome));
		} catch (IOException e) {
			throw new RuntimeException("Erro lendo a foto temporária", e);
		}
	}*/
	
/*	@Override --Saiu aula 28.05
	public void salvar(String foto) {
		try {
			Files.move(this.localTemporario.resolve(foto), this.local.resolve(foto));
		} catch (IOException e) {
			throw new RuntimeException("Erro movendo a foto para destino final", e);
		}
		
		try {
			Thumbnails.of(this.local.resolve(foto).toString()).size(40, 68).toFiles(Rename.PREFIX_DOT_THUMBNAIL);
		} catch (IOException e) {
			throw new RuntimeException("Erro gerando thumbnail", e);
		}
	}*/
	
	@Override
	public byte[] recuperar(String nome) {
		try {
			return Files.readAllBytes(this.local.resolve(nome));
		} catch (IOException e) {
			throw new RuntimeException("Erro lendo a foto", e);
		}
	}

	@Override
	public byte[] recuperarThumbnail(String fotoCerveja) {
		return recuperar(THUMBNAIL_PREFIX + fotoCerveja);
		//para recuperar foto para o email em Mailer. Aula 24-6 28:30
		//THUMBNAIL_PREFIX para excluir foto de cerveja. Aula 25-1 33:37
	}
	
	@Override
	public void excluir(String foto) {//Aula 25-1 33:55
		try {
			Files.deleteIfExists(this.local.resolve(foto));//apaga somente se a foto existir
			Files.deleteIfExists(this.local.resolve(THUMBNAIL_PREFIX + foto));
		} catch (IOException e) {//lança somente warn para não para o sistema e envia o nome da foto exibe a mensagem de erro
			logger.warn(String.format("Erro apagando foto '%s'. Mensagem: %s", foto, e.getMessage()));//
		}
		
	}
	
	@Override
	public String getUrl(String foto) {
		
		return "http://localhost:8080/brewer/fotos/" + foto;
	}

	private void criarPastas() {
		try {
			Files.createDirectories(this.local);
			//this.localTemporario = getDefault().getPath(this.local.toString(), "temp"); -- não vamos mais criar essa pasta temporaria, saiu aula 28:05
			//Files.createDirectories(this.localTemporario);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Pastas criadas para salvar fotos.");
				logger.debug("Pasta default: " + this.local.toAbsolutePath());
				//logger.debug("Pasta temporária: " + this.localTemporario.toAbsolutePath());--saiu aula 28.05
			}
		} catch (IOException e) {
			throw new RuntimeException("Erro criando pasta para salvar foto", e);
		}
	}
	
	private String renomearArquivo(String nomeOriginal) {
		String novoNome = UUID.randomUUID().toString() + "_" + nomeOriginal;
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Nome original: %s, novo nome: %s", nomeOriginal, novoNome));
		}
		
		return novoNome;
		
	}


}
