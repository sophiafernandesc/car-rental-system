package br.pucminas.carrental.model;

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
public class Automovel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// RENAVAM
	@Column(nullable = false, unique = true)
	private String matricula;

	@Column(nullable = false)
	private int ano;

	@Column(nullable = false)
	private String marca;

	@Column(nullable = false)
	private String modelo;

	@Column(nullable = false, unique = true, length = 7)
	private String placa;

}
