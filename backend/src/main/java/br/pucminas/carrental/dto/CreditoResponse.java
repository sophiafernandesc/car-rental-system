package br.pucminas.carrental.dto;

import java.math.BigDecimal;

public record CreditoResponse(
		BigDecimal valor,
		BigDecimal taxaJuros,
		int prazoMeses,
		String banco) {
}
