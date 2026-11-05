package br.pucminas.carrental.exception;

import org.springframework.http.HttpStatus;

public class ConflitoException extends NegocioException {

	public ConflitoException(String mensagem) {
		super(HttpStatus.CONFLICT, mensagem);
	}

}
