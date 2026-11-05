package br.pucminas.carrental.dto;

import java.math.BigDecimal;
import java.util.List;

public record ClienteResponse(
		Long id,
		String login,
		String rg,
		String cpf,
		String nome,
		String endereco,
		String profissao,
		List<RendimentoResponse> rendimentos,
		BigDecimal rendaTotal) {
}
