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
import jakarta.persistence.ManyToOne;
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

	// Cliente do pedido, ou a Empresa/Banco que executa o contrato
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "proprietario_id")
	private Usuario proprietario;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "pedido_id", nullable = false, unique = true)
	private PedidoAluguel pedido;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "credito_id", unique = true)
	private ContratoCredito credito;

	// US17: a execução do contrato muda o pedido para CONTRATADO.
	// O automóvel fica em nome do cliente ou do próprio agente, que precisa ser do tipo escolhido.
	public Contrato(PedidoAluguel pedido, TipoProprietario tipoProprietario, Agente agente) {
		this.proprietario = switch (tipoProprietario) {
			case CLIENTE -> pedido.getCliente();
			case EMPRESA -> exigirTipo(agente, Empresa.class, "Apenas agentes do tipo Empresa podem registrar o automóvel em nome da empresa.");
			case BANCO -> exigirTipo(agente, Banco.class, "Apenas agentes do tipo Banco podem registrar o automóvel em nome do banco.");
		};
		pedido.contratar();
		this.pedido = pedido;
		this.tipoProprietario = tipoProprietario;
		this.dataExecucao = LocalDate.now();
	}

	private static Agente exigirTipo(Agente agente, Class<? extends Agente> tipo, String mensagem) {
		if (!tipo.isInstance(agente)) {
			throw new IllegalStateException(mensagem);
		}
		return agente;
	}

}
