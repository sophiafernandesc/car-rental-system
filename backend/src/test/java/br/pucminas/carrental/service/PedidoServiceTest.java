package br.pucminas.carrental.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
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

import br.pucminas.carrental.dto.PedidoResponse;
import br.pucminas.carrental.exception.ConflitoException;
import br.pucminas.carrental.model.Automovel;
import br.pucminas.carrental.model.Cliente;
import br.pucminas.carrental.model.Empresa;
import br.pucminas.carrental.model.PedidoAluguel;
import br.pucminas.carrental.model.StatusPedido;
import br.pucminas.carrental.repository.AgenteRepository;
import br.pucminas.carrental.repository.AutomovelRepository;
import br.pucminas.carrental.repository.ClienteRepository;
import br.pucminas.carrental.repository.ContratoRepository;
import br.pucminas.carrental.repository.PedidoAluguelRepository;

// US16: a aprovação reserva o automóvel, então confere a disponibilidade com o carro bloqueado
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

	private static final long PEDIDO_ID = 1L;
	private static final long AGENTE_ID = 2L;
	private static final long AUTOMOVEL_ID = 3L;

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

	@BeforeEach
	void setUp() {
		Automovel automovel = new Automovel();
		automovel.setId(AUTOMOVEL_ID);
		automovel.setMarca("Fiat");
		automovel.setModelo("Mobi");

		Cliente cliente = new Cliente();
		cliente.setNome("Maria Teste");

		pedido = new PedidoAluguel();
		pedido.setId(PEDIDO_ID);
		pedido.setStatus(StatusPedido.EM_ANALISE);
		pedido.setAutomovel(automovel);
		pedido.setCliente(cliente);
		pedido.setDataInicio(LocalDate.of(2027, 3, 1));
		pedido.setDataFim(LocalDate.of(2027, 3, 5));

		Empresa agente = new Empresa();
		agente.setRazaoSocial("Locadora Minas Ltda");

		when(pedidoRepository.findById(PEDIDO_ID)).thenReturn(Optional.of(pedido));
		when(agenteRepository.findById(AGENTE_ID)).thenReturn(Optional.of(agente));
		when(automovelRepository.bloquearParaReserva(AUTOMOVEL_ID)).thenReturn(Optional.of(automovel));
	}

	@Test
	void aprovaQuandoAutomovelEstaLivre() {
		when(pedidoRepository.existsOutraReservaNoPeriodo(eq(AUTOMOVEL_ID), eq(PEDIDO_ID), anyCollection(), any(), any()))
				.thenReturn(false);

		PedidoResponse resposta = pedidoService.aprovar(AGENTE_ID, PEDIDO_ID);

		assertThat(resposta.status()).isEqualTo("APROVADO");
		assertThat(resposta.avaliador()).isEqualTo("Locadora Minas Ltda");
	}

	@Test
	void recusaAprovacaoQuandoOutroPedidoJaReservouOAutomovel() {
		when(pedidoRepository.existsOutraReservaNoPeriodo(eq(AUTOMOVEL_ID), eq(PEDIDO_ID), anyCollection(), any(), any()))
				.thenReturn(true);

		assertThatThrownBy(() -> pedidoService.aprovar(AGENTE_ID, PEDIDO_ID)).isInstanceOf(ConflitoException.class);
	}

	@Test
	void bloqueiaOAutomovelAntesDeVerificarAsReservas() {
		when(pedidoRepository.existsOutraReservaNoPeriodo(eq(AUTOMOVEL_ID), eq(PEDIDO_ID), anyCollection(), any(), any()))
				.thenReturn(false);

		pedidoService.aprovar(AGENTE_ID, PEDIDO_ID);

		InOrder ordem = inOrder(automovelRepository, pedidoRepository);
		ordem.verify(automovelRepository).bloquearParaReserva(AUTOMOVEL_ID);
		ordem.verify(pedidoRepository).existsOutraReservaNoPeriodo(eq(AUTOMOVEL_ID), eq(PEDIDO_ID), anyCollection(), any(), any());
	}

}
