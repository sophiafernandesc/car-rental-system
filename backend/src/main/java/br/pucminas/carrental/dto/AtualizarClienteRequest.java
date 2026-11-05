package br.pucminas.carrental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarClienteRequest(
		@NotBlank String nome,
		@NotBlank String endereco,
		@NotBlank String profissao,
		@Size(min = 6, max = 72) String novaSenha) {
}
