package br.pucminas.carrental.dto;

import java.util.List;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroClienteRequest(
		@NotBlank @Size(min = 4, max = 50) String login,
		@NotBlank @Size(min = 6, max = 72) String senha,
		@NotBlank String rg,
		@NotBlank @CPF String cpf,
		@NotBlank String nome,
		@NotBlank String endereco,
		@NotBlank String profissao,
		@Valid @Size(max = 3, message = "O cliente pode ter no máximo 3 rendimentos.") List<RendimentoRequest> rendimentos) {
}
