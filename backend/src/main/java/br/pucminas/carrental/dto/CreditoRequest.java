package br.pucminas.carrental.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CreditoRequest(
		@NotNull @Positive BigDecimal valor,
		@NotNull @PositiveOrZero @DecimalMax("100") BigDecimal taxaJuros,
		@NotNull @Min(1) @Max(120) Integer prazoMeses) {
}
