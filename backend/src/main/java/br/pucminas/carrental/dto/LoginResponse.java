package br.pucminas.carrental.dto;

public record LoginResponse(
		String token,
		Long id,
		String nome,
		String perfil) {
}
