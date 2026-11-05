package br.pucminas.carrental.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PedidoAluguel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private LocalDate dataInicio;

	@Column(nullable = false)
	private LocalDate dataFim;

	@Column(nullable = false, updatable = false)
	private LocalDateTime dataCriacao;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusPedido status;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cliente_id", nullable = false)
	private Cliente cliente;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "automovel_id", nullable = false)
	private Automovel automovel;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "avaliador_id")
	private Agente avaliador;

	@PrePersist
	void prePersist() {
		dataCriacao = LocalDateTime.now();
		if (status == null) {
			status = StatusPedido.EM_ANALISE;
		}
	}

	public void aprovar(Agente agente) {
		avaliar(agente, StatusPedido.APROVADO);
	}

	public void reprovar(Agente agente) {
		avaliar(agente, StatusPedido.REPROVADO);
	}

	private void avaliar(Agente agente, StatusPedido novoStatus) {
		if (status != StatusPedido.EM_ANALISE) {
			throw new IllegalStateException("Só é possível avaliar pedidos com status EM_ANALISE.");
		}
		this.avaliador = agente;
		this.status = novoStatus;
	}

	// US11
	public void modificar(Automovel automovel, LocalDate dataInicio, LocalDate dataFim) {
		if (status != StatusPedido.EM_ANALISE) {
			throw new IllegalStateException("Só é possível modificar pedidos com status EM_ANALISE.");
		}
		this.automovel = automovel;
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
	}

	// US12
	public void cancelar() {
		if (status != StatusPedido.EM_ANALISE && status != StatusPedido.APROVADO) {
			throw new IllegalStateException("Só é possível cancelar pedidos em análise ou aprovados.");
		}
		this.status = StatusPedido.CANCELADO;
	}

	// US17
	public void contratar() {
		if (status != StatusPedido.APROVADO) {
			throw new IllegalStateException("Só é possível executar o contrato de pedidos aprovados.");
		}
		this.status = StatusPedido.CONTRATADO;
	}

}
