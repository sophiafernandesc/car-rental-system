package br.pucminas.carrental.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AutomovelRequest(
		@NotBlank @Pattern(regexp = "\\d{11}", message = "A matrícula (RENAVAM) deve ter 11 dígitos.") String matricula,
		@NotNull @Min(value = 1950, message = "Ano inválido.") Integer ano,
		@NotBlank String marca,
		@NotBlank String modelo,
		@NotBlank @Pattern(regexp = "[A-Za-z]{3}[0-9][A-Za-z0-9][0-9]{2}", message = "Placa inválida (ex.: ABC1D23).") String placa) {
}
