package br.pucminas.carrental.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class NegocioException extends RuntimeException {

	private final HttpStatus status;

	public NegocioException(HttpStatus status, String mensagem) {
		super(mensagem);
		this.status = status;
	}

}
