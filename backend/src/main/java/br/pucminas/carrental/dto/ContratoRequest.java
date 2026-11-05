package br.pucminas.carrental.dto;

import br.pucminas.carrental.model.TipoProprietario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ContratoRequest(
		@NotNull TipoProprietario tipoProprietario,
		// Opcional: apenas agentes do tipo Banco (US18)
		@Valid CreditoRequest credito) {
}
