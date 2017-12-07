package com.algaworks.brewer.storage;

import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.dto.FotoDTO;

public class FotoStorageRunnable implements Runnable {

	private MultipartFile[] files;
	private DeferredResult<FotoDTO> resultado;
	private FotoStorage fotoStorage;
	
	public FotoStorageRunnable(MultipartFile[] files, DeferredResult<FotoDTO> resultado, FotoStorage fotoStorage) {
		this.files = files;
		this.resultado = resultado;
		this.fotoStorage = fotoStorage;
	}

	@Override
	public void run() {
		//String nomeFoto = this.fotoStorage.salvarTemporariamente(files);--saiu aula 28.05 04:36
		String nomeFoto = this.fotoStorage.salvar(files);
		String contentType = files[0].getContentType();
		resultado.setResult(new FotoDTO(nomeFoto, contentType, fotoStorage.getUrl(nomeFoto)));//Esse FotoDto é o que é retornado para o js em cerveja.upload-foto.js. Na classe FotoDTO vamos criar mais um atributo Aula 28-05 17:58
		//Passando getUrl() que foi adicionado na aula 28-5.
	}

}
