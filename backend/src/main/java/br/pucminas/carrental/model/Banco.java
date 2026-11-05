package br.pucminas.carrental.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;

@Entity
public class Banco extends Agente {

	// US18
	public ContratoCredito concederCredito(BigDecimal valor, BigDecimal taxaJuros, int prazoMeses) {
		return new ContratoCredito(this, valor, taxaJuros, prazoMeses);
	}

}
