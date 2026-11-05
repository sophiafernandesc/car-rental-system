package br.pucminas.carrental.dto;

import java.math.BigDecimal;
import java.util.List;

public record DadosFinanceirosResponse(
		String nome,
		String profissao,
		List<RendimentoResponse> rendimentos,
		BigDecimal rendaTotal) {
}
