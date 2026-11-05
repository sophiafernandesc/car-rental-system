package br.pucminas.carrental.dto;

public record AutomovelResponse(
		Long id,
		String matricula,
		int ano,
		String marca,
		String modelo,
		String placa) {
}
