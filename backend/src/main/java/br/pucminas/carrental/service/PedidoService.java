package br.pucminas.carrental.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.pucminas.carrental.dto.ContratoRequest;
import br.pucminas.carrental.dto.PedidoRequest;
import br.pucminas.carrental.dto.PedidoResponse;
import br.pucminas.carrental.exception.ConflitoException;
import br.pucminas.carrental.exception.NegocioException;
import br.pucminas.carrental.exception.RecursoNaoEncontradoException;
import br.pucminas.carrental.model.Agente;
import br.pucminas.carrental.model.Automovel;
import br.pucminas.carrental.model.Cliente;
import br.pucminas.carrental.model.Contrato;
import br.pucminas.carrental.model.PedidoAluguel;
import br.pucminas.carrental.model.StatusPedido;
import br.pucminas.carrental.repository.AgenteRepository;
import br.pucminas.carrental.repository.AutomovelRepository;
import br.pucminas.carrental.repository.ClienteRepository;
import br.pucminas.carrental.repository.ContratoRepository;
import br.pucminas.carrental.repository.PedidoAluguelRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {

	private static final List<StatusPedido> STATUS_RESERVAM_AUTOMOVEL =
			List.of(StatusPedido.APROVADO, StatusPedido.CONTRATADO);

	private final PedidoAluguelRepository pedidoRepository;
	private final ClienteRepository clienteRepository;
	private final AutomovelRepository automovelRepository;
	private final AgenteRepository agenteRepository;
	private final ContratoRepository contratoRepository;

	// US09
	@Transactional
	public PedidoResponse criar(Long clienteId, PedidoRequest request) {
		Cliente cliente = clienteRepository.findById(clienteId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));
		Automovel automovel = buscarAutomovel(request);
		verificarDisponibilidade(automovel, request);

		PedidoAluguel pedido = new PedidoAluguel();
		pedido.setCliente(cliente);
		pedido.setAutomovel(automovel);
		pedido.setDataInicio(request.dataInicio());
		pedido.setDataFim(request.dataFim());

		return toResponse(pedidoRepository.save(pedido));
	}

	// US10
	@Transactional(readOnly = true)
	public List<PedidoResponse> listarDoCliente(Long clienteId) {
		return pedidoRepository.findByClienteIdOrderByDataCriacaoDesc(clienteId).stream()
				.map(this::toResponse)
				.toList();
	}

	// US10
	@Transactional(readOnly = true)
	public PedidoResponse buscarDoCliente(Long clienteId, Long pedidoId) {
		return toResponse(buscarPedidoDoCliente(clienteId, pedidoId));
	}

	// US11
	@Transactional
	public PedidoResponse modificar(Long clienteId, Long pedidoId, PedidoRequest request) {
		PedidoAluguel pedido = buscarPedidoDoCliente(clienteId, pedidoId);
		Automovel automovel = buscarAutomovel(request);
		// A regra de status (entidade) é verificada antes da disponibilidade;
		// se o automóvel estiver indisponível, a exceção desfaz a alteração (rollback)
		pedido.modificar(automovel, request.dataInicio(), request.dataFim());
		verificarDisponibilidade(automovel, request);
		return toResponse(pedido);
	}

	// US12
	@Transactional
	public PedidoResponse cancelar(Long clienteId, Long pedidoId) {
		PedidoAluguel pedido = buscarPedidoDoCliente(clienteId, pedidoId);
		pedido.cancelar();
		return toResponse(pedido);
	}

	// US13
	@Transactional(readOnly = true)
	public List<PedidoResponse> listarPorStatus(StatusPedido status) {
		return pedidoRepository.findByStatusOrderByDataCriacaoAsc(status).stream()
				.map(this::toResponse)
				.toList();
	}

	// US16
	@Transactional
	public PedidoResponse aprovar(Long agenteId, Long pedidoId) {
		PedidoAluguel pedido = buscarPedido(pedidoId);
		pedido.aprovar(buscarAgente(agenteId));
		return toResponse(pedido);
	}

	// US16
	@Transactional
	public PedidoResponse reprovar(Long agenteId, Long pedidoId) {
		PedidoAluguel pedido = buscarPedido(pedidoId);
		pedido.reprovar(buscarAgente(agenteId));
		return toResponse(pedido);
	}

	// US17
	@Transactional
	public PedidoResponse executarContrato(Long pedidoId, ContratoRequest request) {
		PedidoAluguel pedido = buscarPedido(pedidoId);
		contratoRepository.save(new Contrato(pedido, request.tipoProprietario()));
		return toResponse(pedido);
	}

	private Automovel buscarAutomovel(PedidoRequest request) {
		if (!request.dataFim().isAfter(request.dataInicio())) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "A data de fim deve ser posterior à data de início.");
		}
		return automovelRepository.findById(request.automovelId())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Automóvel não encontrado."));
	}

	private void verificarDisponibilidade(Automovel automovel, PedidoRequest request) {
		if (pedidoRepository.existsReservaNoPeriodo(automovel.getId(), STATUS_RESERVAM_AUTOMOVEL,
				request.dataInicio(), request.dataFim())) {
			throw new ConflitoException("Automóvel indisponível no período informado.");
		}
	}

	private PedidoAluguel buscarPedido(Long pedidoId) {
		return pedidoRepository.findById(pedidoId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado."));
	}

	private PedidoAluguel buscarPedidoDoCliente(Long clienteId, Long pedidoId) {
		PedidoAluguel pedido = buscarPedido(pedidoId);
		if (!pedido.getCliente().getId().equals(clienteId)) {
			throw new RecursoNaoEncontradoException("Pedido não encontrado.");
		}
		return pedido;
	}

	private Agente buscarAgente(Long agenteId) {
		return agenteRepository.findById(agenteId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Agente não encontrado."));
	}

	private PedidoResponse toResponse(PedidoAluguel pedido) {
		Cliente cliente = pedido.getCliente();
		Agente avaliador = pedido.getAvaliador();
		String proprietario = pedido.getStatus() == StatusPedido.CONTRATADO
				? contratoRepository.findByPedidoId(pedido.getId())
						.map(contrato -> contrato.getTipoProprietario().name())
						.orElse(null)
				: null;

		return new PedidoResponse(
				pedido.getId(),
				pedido.getStatus().name(),
				pedido.getDataInicio(),
				pedido.getDataFim(),
				pedido.getDataCriacao(),
				AutomovelService.toResponse(pedido.getAutomovel()),
				cliente.getNome(),
				cliente.getRendaTotal(),
				avaliador != null ? avaliador.getRazaoSocial() : null,
				proprietario);
	}

}
