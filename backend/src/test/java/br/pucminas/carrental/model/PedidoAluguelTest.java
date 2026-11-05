package br.pucminas.carrental.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class PedidoAluguelTest {

	private static PedidoAluguel pedido(StatusPedido status) {
		PedidoAluguel pedido = new PedidoAluguel();
		pedido.setStatus(status);
		return pedido;
	}

	@Test
	void aprovarRegistraAvaliadorEMudaStatus() {
		PedidoAluguel pedido = pedido(StatusPedido.EM_ANALISE);
		Empresa agente = new Empresa();

		pedido.aprovar(agente);

		assertThat(pedido.getStatus()).isEqualTo(StatusPedido.APROVADO);
		assertThat(pedido.getAvaliador()).isSameAs(agente);
	}

	@Test
	void reprovarMudaStatusParaReprovado() {
		PedidoAluguel pedido = pedido(StatusPedido.EM_ANALISE);

		pedido.reprovar(new Banco());

		assertThat(pedido.getStatus()).isEqualTo(StatusPedido.REPROVADO);
	}

	@Test
	void naoAvaliaPedidoForaDeAnalise() {
		PedidoAluguel pedido = pedido(StatusPedido.APROVADO);

		assertThatThrownBy(() -> pedido.aprovar(new Empresa())).isInstanceOf(IllegalStateException.class);
		assertThatThrownBy(() -> pedido.reprovar(new Empresa())).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void modificaApenasPedidoEmAnalise() {
		Automovel automovel = new Automovel();
		LocalDate inicio = LocalDate.of(2027, 1, 10);
		LocalDate fim = LocalDate.of(2027, 1, 15);

		PedidoAluguel emAnalise = pedido(StatusPedido.EM_ANALISE);
		emAnalise.modificar(automovel, inicio, fim);
		assertThat(emAnalise.getAutomovel()).isSameAs(automovel);
		assertThat(emAnalise.getDataInicio()).isEqualTo(inicio);
		assertThat(emAnalise.getDataFim()).isEqualTo(fim);

		PedidoAluguel aprovado = pedido(StatusPedido.APROVADO);
		assertThatThrownBy(() -> aprovado.modificar(automovel, inicio, fim)).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void cancelaPedidoEmAnaliseOuAprovado() {
		PedidoAluguel emAnalise = pedido(StatusPedido.EM_ANALISE);
		emAnalise.cancelar();
		assertThat(emAnalise.getStatus()).isEqualTo(StatusPedido.CANCELADO);

		PedidoAluguel aprovado = pedido(StatusPedido.APROVADO);
		aprovado.cancelar();
		assertThat(aprovado.getStatus()).isEqualTo(StatusPedido.CANCELADO);
	}

	@Test
	void naoCancelaPedidoContratado() {
		PedidoAluguel pedido = pedido(StatusPedido.CONTRATADO);

		assertThatThrownBy(pedido::cancelar).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void contrataApenasPedidoAprovado() {
		PedidoAluguel aprovado = pedido(StatusPedido.APROVADO);
		aprovado.contratar();
		assertThat(aprovado.getStatus()).isEqualTo(StatusPedido.CONTRATADO);

		PedidoAluguel emAnalise = pedido(StatusPedido.EM_ANALISE);
		assertThatThrownBy(emAnalise::contratar).isInstanceOf(IllegalStateException.class);
	}

}
