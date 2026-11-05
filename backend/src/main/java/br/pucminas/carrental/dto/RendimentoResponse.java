package br.pucminas.carrental.dto;

import java.math.BigDecimal;

public record RendimentoResponse(
		Long id,
		String entidadeEmpregadora,
		BigDecimal valor) {
}
