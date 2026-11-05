package br.pucminas.carrental.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ContratoTest {

	private final Cliente cliente = new Cliente();

	private PedidoAluguel pedidoAprovado() {
		PedidoAluguel pedido = new PedidoAluguel();
		pedido.setCliente(cliente);
		pedido.setStatus(StatusPedido.APROVADO);
		return pedido;
	}

	@Test
	void executarContratoMudaPedidoParaContratado() {
		PedidoAluguel pedido = pedidoAprovado();

		Contrato contrato = new Contrato(pedido, TipoProprietario.CLIENTE, new Empresa());

		assertThat(pedido.getStatus()).isEqualTo(StatusPedido.CONTRATADO);
		assertThat(contrato.getDataExecucao()).isEqualTo(LocalDate.now());
	}

	@Test
	void proprietarioClienteEhOClienteDoPedido() {
		Contrato contrato = new Contrato(pedidoAprovado(), TipoProprietario.CLIENTE, new Banco());

		assertThat(contrato.getProprietario()).isSameAs(cliente);
	}

	@Test
	void proprietarioEmpresaOuBancoEhOAgenteQueExecuta() {
		Empresa empresa = new Empresa();
		Banco banco = new Banco();

		assertThat(new Contrato(pedidoAprovado(), TipoProprietario.EMPRESA, empresa).getProprietario()).isSameAs(empresa);
		assertThat(new Contrato(pedidoAprovado(), TipoProprietario.BANCO, banco).getProprietario()).isSameAs(banco);
	}

	@Test
	void agenteNaoRegistraAutomovelEmNomeDeOutroTipo() {
		PedidoAluguel pedido = pedidoAprovado();

		assertThatThrownBy(() -> new Contrato(pedido, TipoProprietario.BANCO, new Empresa()))
				.isInstanceOf(IllegalStateException.class);
		assertThatThrownBy(() -> new Contrato(pedido, TipoProprietario.EMPRESA, new Banco()))
				.isInstanceOf(IllegalStateException.class);
		assertThat(pedido.getStatus()).isEqualTo(StatusPedido.APROVADO);
	}

	@Test
	void naoExecutaContratoDePedidoNaoAprovado() {
		PedidoAluguel pedido = pedidoAprovado();
		pedido.setStatus(StatusPedido.EM_ANALISE);

		assertThatThrownBy(() -> new Contrato(pedido, TipoProprietario.CLIENTE, new Empresa()))
				.isInstanceOf(IllegalStateException.class);
	}

	@Test
	void bancoConcedeCreditoEmSeuNome() {
		Banco banco = new Banco();

		ContratoCredito credito = banco.concederCredito(new BigDecimal("45000"), new BigDecimal("1.49"), 36);

		assertThat(credito.getBanco()).isSameAs(banco);
		assertThat(credito.getPrazoMeses()).isEqualTo(36);
	}

}
