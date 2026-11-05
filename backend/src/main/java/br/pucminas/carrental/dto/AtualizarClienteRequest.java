package br.pucminas.carrental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarClienteRequest(
		@NotBlank String nome,
		@NotBlank String endereco,
		@NotBlank String profissao,
		// Opcional: enviar null para manter a senha atual
		@Size(min = 6, max = 72) String novaSenha) {
}
