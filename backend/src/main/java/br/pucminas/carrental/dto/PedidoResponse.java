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
		BigDecimal clienteRendaTotal,
		String avaliador,
		String proprietario,
		String proprietarioNome,
		LocalDate dataContrato,
		CreditoResponse credito) {
}
