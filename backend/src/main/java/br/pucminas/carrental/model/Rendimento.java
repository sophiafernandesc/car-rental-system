package br.pucminas.carrental.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Rendimento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String entidadeEmpregadora;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal valor;

	public Rendimento(String entidadeEmpregadora, BigDecimal valor) {
		this.entidadeEmpregadora = entidadeEmpregadora;
		this.valor = valor;
	}

}
