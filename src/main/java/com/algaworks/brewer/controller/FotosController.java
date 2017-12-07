package com.algaworks.brewer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.dto.FotoDTO;
import com.algaworks.brewer.storage.FotoStorage;
import com.algaworks.brewer.storage.FotoStorageRunnable;

/***
 * 
 * @author Katho
 * Foi alterado o modo de salvar as fotos na aula 28:05.
 * Explicação na Classe FotoStorage e ServiceConfig
 */

@RestController
@RequestMapping("/fotos")
public class FotosController {

	@Autowired
	private FotoStorage fotoStorage;
	
	@PostMapping
	public DeferredResult<FotoDTO> upload(@RequestParam("files[]") MultipartFile[] files) {
		DeferredResult<FotoDTO> resultado = new DeferredResult<>();

		Thread thread = new Thread(new FotoStorageRunnable(files, resultado, fotoStorage));
		thread.start();
		
		return resultado;
	}
	
	/*@GetMapping("/temp/{nome:.*}")-- Tirado na aula 28.05 05:09 *** Tem comentario na classe Foto Storage
	public byte[] recuperarFotoTemporaria(@PathVariable String nome) {
		return fotoStorage.recuperarFotoTemporaria(nome);
	}*/
	
	@GetMapping("/{nome:.*}")
	public byte[] recuperar(@PathVariable String nome) {
		return fotoStorage.recuperar(nome);
	}
	
}
