package br.pucminas.carrental.exception;

import org.springframework.http.HttpStatus;

public class CredenciaisInvalidasException extends NegocioException {

	// Mensagem genérica: não indica se o login ou a senha está incorreto (US03)
	public CredenciaisInvalidasException() {
		super(HttpStatus.UNAUTHORIZED, "Login ou senha inválidos.");
	}

}
