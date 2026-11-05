package br.pucminas.carrental.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.pucminas.carrental.dto.PedidoRequest;
import br.pucminas.carrental.dto.PedidoResponse;
import br.pucminas.carrental.exception.ConflitoException;
import br.pucminas.carrental.exception.NegocioException;
import br.pucminas.carrental.exception.RecursoNaoEncontradoException;
import br.pucminas.carrental.model.Agente;
import br.pucminas.carrental.model.Automovel;
import br.pucminas.carrental.model.Cliente;
import br.pucminas.carrental.model.PedidoAluguel;
import br.pucminas.carrental.model.StatusPedido;
import br.pucminas.carrental.repository.AgenteRepository;
import br.pucminas.carrental.repository.AutomovelRepository;
import br.pucminas.carrental.repository.ClienteRepository;
import br.pucminas.carrental.repository.PedidoAluguelRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {

	// Pedidos que ocupam o automóvel no período solicitado
	private static final List<StatusPedido> STATUS_RESERVAM_AUTOMOVEL =
			List.of(StatusPedido.APROVADO, StatusPedido.CONTRATADO);

	private final PedidoAluguelRepository pedidoRepository;
	private final ClienteRepository clienteRepository;
	private final AutomovelRepository automovelRepository;
	private final AgenteRepository agenteRepository;

	// US09
	@Transactional
	public PedidoResponse criar(Long clienteId, PedidoRequest request) {
		if (!request.dataFim().isAfter(request.dataInicio())) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "A data de fim deve ser posterior à data de início.");
		}

		Cliente cliente = clienteRepository.findById(clienteId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));
		Automovel automovel = automovelRepository.findById(request.automovelId())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Automóvel não encontrado."));

		if (pedidoRepository.existsReservaNoPeriodo(automovel.getId(), STATUS_RESERVAM_AUTOMOVEL,
				request.dataInicio(), request.dataFim())) {
			throw new ConflitoException("Automóvel indisponível no período informado.");
		}

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

	private PedidoAluguel buscarPedido(Long pedidoId) {
		return pedidoRepository.findById(pedidoId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado."));
	}

	private Agente buscarAgente(Long agenteId) {
		return agenteRepository.findById(agenteId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Agente não encontrado."));
	}

	private PedidoResponse toResponse(PedidoAluguel pedido) {
		Cliente cliente = pedido.getCliente();
		Agente avaliador = pedido.getAvaliador();

		return new PedidoResponse(
				pedido.getId(),
				pedido.getStatus().name(),
				pedido.getDataInicio(),
				pedido.getDataFim(),
				pedido.getDataCriacao(),
				AutomovelService.toResponse(pedido.getAutomovel()),
				cliente.getNome(),
				cliente.getRendaTotal(),
				avaliador != null ? avaliador.getRazaoSocial() : null);
	}

}
