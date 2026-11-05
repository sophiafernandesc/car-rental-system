package br.pucminas.carrental.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Fora do MVP
@Entity
@Getter
@Setter
@NoArgsConstructor
public class ContratoCredito {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal valor;

	@Column(nullable = false, precision = 5, scale = 2)
	private BigDecimal taxaJuros;

	@Column(nullable = false)
	private int prazoMeses;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "banco_id", nullable = false)
	private Banco banco;

}
