package com.algaworks.brewer.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FotoStorage {

	public String salvarTemporariamente(MultipartFile[] files);

	public byte[] recuperarFotoTemporaria(String nome);

	public void salvar(String foto);

	public byte[] recuperar(String foto);
	
	public byte[] recuperarThumbnail(String fotoCerveja);//para recuperar thumbnail da imagem para exibir no email em Mailer. Aula 24-6 27:10

	public void excluir(String foto);
	
}
