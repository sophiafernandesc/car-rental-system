package br.pucminas.carrental.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.pucminas.carrental.dto.ContratoRequest;
import br.pucminas.carrental.dto.PedidoResponse;
import br.pucminas.carrental.exception.ConflitoException;
import br.pucminas.carrental.model.Automovel;
import br.pucminas.carrental.model.Cliente;
import br.pucminas.carrental.model.Empresa;
import br.pucminas.carrental.model.PedidoAluguel;
import br.pucminas.carrental.model.StatusPedido;
import br.pucminas.carrental.model.TipoProprietario;
import br.pucminas.carrental.repository.AgenteRepository;
import br.pucminas.carrental.repository.AutomovelRepository;
import br.pucminas.carrental.repository.ClienteRepository;
import br.pucminas.carrental.repository.ContratoRepository;
import br.pucminas.carrental.repository.PedidoAluguelRepository;

// US16: a aprovação reserva o automóvel, então confere a disponibilidade com o carro bloqueado.
// US12 e US17: alterações do mesmo pedido bloqueiam o pedido, para não cancelar e contratar ao mesmo tempo
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

	private static final long PEDIDO_ID = 1L;
	private static final long AGENTE_ID = 2L;
	private static final long AUTOMOVEL_ID = 3L;
	private static final long CLIENTE_ID = 4L;

	@Mock
	private PedidoAluguelRepository pedidoRepository;
	@Mock
	private ClienteRepository clienteRepository;
	@Mock
	private AutomovelRepository automovelRepository;
	@Mock
	private AgenteRepository agenteRepository;
	@Mock
	private ContratoRepository contratoRepository;

	@InjectMocks
	private PedidoService pedidoService;

	private PedidoAluguel pedido;
	private Empresa agente;

	@BeforeEach
	void setUp() {
		Automovel automovel = new Automovel();
		automovel.setId(AUTOMOVEL_ID);
		automovel.setMarca("Fiat");
		automovel.setModelo("Mobi");

		Cliente cliente = new Cliente();
		cliente.setId(CLIENTE_ID);
		cliente.setNome("Maria Teste");

		pedido = new PedidoAluguel();
		pedido.setId(PEDIDO_ID);
		pedido.setStatus(StatusPedido.EM_ANALISE);
		pedido.setAutomovel(automovel);
		pedido.setCliente(cliente);
		pedido.setDataInicio(LocalDate.of(2027, 3, 1));
		pedido.setDataFim(LocalDate.of(2027, 3, 5));

		agente = new Empresa();
		agente.setRazaoSocial("Locadora Minas Ltda");

		when(pedidoRepository.bloquearParaAlteracao(PEDIDO_ID)).thenReturn(Optional.of(pedido));
	}

	private void prepararAprovacao(boolean automovelReservado) {
		when(agenteRepository.findById(AGENTE_ID)).thenReturn(Optional.of(agente));
		when(automovelRepository.bloquearParaReserva(AUTOMOVEL_ID)).thenReturn(Optional.of(pedido.getAutomovel()));
		when(pedidoRepository.existsOutraReservaNoPeriodo(eq(AUTOMOVEL_ID), eq(PEDIDO_ID), anyCollection(), any(), any()))
				.thenReturn(automovelReservado);
	}

	@Test
	void aprovaQuandoAutomovelEstaLivre() {
		prepararAprovacao(false);

		PedidoResponse resposta = pedidoService.aprovar(AGENTE_ID, PEDIDO_ID);

		assertThat(resposta.status()).isEqualTo("APROVADO");
		assertThat(resposta.avaliador()).isEqualTo("Locadora Minas Ltda");
	}

	@Test
	void recusaAprovacaoQuandoOutroPedidoJaReservouOAutomovel() {
		prepararAprovacao(true);

		assertThatThrownBy(() -> pedidoService.aprovar(AGENTE_ID, PEDIDO_ID)).isInstanceOf(ConflitoException.class);
	}

	@Test
	void bloqueiaPedidoEAutomovelAntesDeVerificarAsReservas() {
		prepararAprovacao(false);

		pedidoService.aprovar(AGENTE_ID, PEDIDO_ID);

		InOrder ordem = inOrder(automovelRepository, pedidoRepository);
		ordem.verify(pedidoRepository).bloquearParaAlteracao(PEDIDO_ID);
		ordem.verify(automovelRepository).bloquearParaReserva(AUTOMOVEL_ID);
		ordem.verify(pedidoRepository).existsOutraReservaNoPeriodo(eq(AUTOMOVEL_ID), eq(PEDIDO_ID), anyCollection(), any(), any());
	}

	@Test
	void cancelarBloqueiaOPedido() {
		pedido.setStatus(StatusPedido.APROVADO);

		PedidoResponse resposta = pedidoService.cancelar(CLIENTE_ID, PEDIDO_ID);

		assertThat(resposta.status()).isEqualTo("CANCELADO");
		verify(pedidoRepository, never()).findById(any());
	}

	@Test
	void executarContratoBloqueiaOPedido() {
		pedido.setStatus(StatusPedido.APROVADO);
		when(agenteRepository.findById(AGENTE_ID)).thenReturn(Optional.of(agente));

		PedidoResponse resposta = pedidoService.executarContrato(AGENTE_ID, PEDIDO_ID,
				new ContratoRequest(TipoProprietario.CLIENTE, null));

		assertThat(resposta.status()).isEqualTo("CONTRATADO");
		verify(pedidoRepository, never()).findById(any());
	}

	@Test
	void naoExecutaContratoDePedidoCanceladoPorOutraTransacao() {
		// Status lido após o bloqueio: o cancelamento concorrente já foi gravado
		pedido.setStatus(StatusPedido.CANCELADO);
		when(agenteRepository.findById(AGENTE_ID)).thenReturn(Optional.of(agente));

		assertThatThrownBy(() -> pedidoService.executarContrato(AGENTE_ID, PEDIDO_ID,
				new ContratoRequest(TipoProprietario.CLIENTE, null))).isInstanceOf(IllegalStateException.class);
		verify(contratoRepository, never()).save(any());
	}

}
