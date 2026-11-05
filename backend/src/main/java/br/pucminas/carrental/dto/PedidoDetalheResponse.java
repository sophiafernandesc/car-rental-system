package br.pucminas.carrental.dto;

public record PedidoDetalheResponse(
		PedidoResponse pedido,
		DadosFinanceirosResponse cliente) {
}
