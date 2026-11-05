package br.pucminas.carrental.dto;

public record LoginResponse(
		String token,
		Long id,
		String nome,
		String perfil,
		// EMPRESA ou BANCO para agentes; null para clientes
		String tipoAgente) {
}
