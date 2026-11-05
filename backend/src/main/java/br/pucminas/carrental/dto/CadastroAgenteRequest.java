package br.pucminas.carrental.dto;

import org.hibernate.validator.constraints.br.CNPJ;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CadastroAgenteRequest(
		@NotBlank @Size(min = 4, max = 50) String login,
		@NotBlank @Size(min = 6, max = 72) String senha,
		@NotBlank @CNPJ String cnpj,
		@NotBlank String razaoSocial,
		@NotBlank @Pattern(regexp = "EMPRESA|BANCO", message = "Tipo deve ser EMPRESA ou BANCO.") String tipo) {
}
