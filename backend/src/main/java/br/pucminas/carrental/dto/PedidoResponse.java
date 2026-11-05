package br.pucminas.carrental.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PedidoResponse(
		Long id,
		String status,
		LocalDate dataInicio,
		LocalDate dataFim,
		LocalDateTime dataCriacao,
		AutomovelResponse automovel,
		String clienteNome,
		// Base para o parecer financeiro do agente (US16)
		BigDecimal clienteRendaTotal,
		String avaliador) {
}
