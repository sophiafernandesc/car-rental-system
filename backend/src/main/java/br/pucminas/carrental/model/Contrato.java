package br.pucminas.carrental.model;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Contrato {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private LocalDate dataExecucao;

	// Define o proprietário do automóvel
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TipoProprietario tipoProprietario;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "pedido_id", nullable = false, unique = true)
	private PedidoAluguel pedido;

	// Fora do escopo atual (US18)
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "credito_id", unique = true)
	private ContratoCredito credito;

	// US17: a execução do contrato muda o pedido para CONTRATADO
	public Contrato(PedidoAluguel pedido, TipoProprietario tipoProprietario) {
		pedido.contratar();
		this.pedido = pedido;
		this.tipoProprietario = tipoProprietario;
		this.dataExecucao = LocalDate.now();
	}

}
