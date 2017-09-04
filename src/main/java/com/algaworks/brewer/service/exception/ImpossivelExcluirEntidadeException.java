package com.algaworks.brewer.service.exception;

public class ImpossivelExcluirEntidadeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ImpossivelExcluirEntidadeException(String msg) {
		super(msg);//super na msg para poder pegar ela no controler. Aula 25-1 27:55
	}
}
