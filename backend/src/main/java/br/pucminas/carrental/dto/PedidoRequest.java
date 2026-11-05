package br.pucminas.carrental.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

public record PedidoRequest(
		@NotNull Long automovelId,
		@NotNull @FutureOrPresent LocalDate dataInicio,
		@NotNull LocalDate dataFim) {
}
