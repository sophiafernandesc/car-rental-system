package br.pucminas.carrental.dto;

import br.pucminas.carrental.model.TipoProprietario;
import jakarta.validation.constraints.NotNull;

public record ContratoRequest(
		@NotNull TipoProprietario tipoProprietario) {
}
