package br.pucminas.carrental.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.pucminas.carrental.dto.ContratoRequest;
import br.pucminas.carrental.dto.CreditoRequest;
import br.pucminas.carrental.dto.CreditoResponse;
import br.pucminas.carrental.dto.DadosFinanceirosResponse;
import br.pucminas.carrental.dto.PedidoDetalheResponse;
import br.pucminas.carrental.dto.PedidoRequest;
import br.pucminas.carrental.dto.PedidoResponse;
import br.pucminas.carrental.dto.RendimentoResponse;
import br.pucminas.carrental.exception.ConflitoException;
import br.pucminas.carrental.exception.NegocioException;
import br.pucminas.carrental.exception.RecursoNaoEncontradoException;
import br.pucminas.carrental.model.Agente;
import br.pucminas.carrental.model.Automovel;
import br.pucminas.carrental.model.Banco;
import br.pucminas.carrental.model.Cliente;
import br.pucminas.carrental.model.Contrato;
import br.pucminas.carrental.model.ContratoCredito;
import br.pucminas.carrental.model.PedidoAluguel;
import br.pucminas.carrental.model.StatusPedido;
import br.pucminas.carrental.model.Usuario;
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
		return toResponses(pedidoRepository.findByClienteIdOrderByDataCriacaoDesc(clienteId));
	}

	// US10
	@Transactional(readOnly = true)
	public PedidoResponse buscarDoCliente(Long clienteId, Long pedidoId) {
		return toResponse(exigirDoCliente(clienteId, buscarPedido(pedidoId)));
	}

	// US11
	@Transactional
	public PedidoResponse modificar(Long clienteId, Long pedidoId, PedidoRequest request) {
		PedidoAluguel pedido = exigirDoCliente(clienteId, bloquearPedido(pedidoId));
		aplicarModificacao(pedido, request);
		return toResponse(pedido);
	}

	// US15
	@Transactional
	public PedidoResponse modificarPeloAgente(Long pedidoId, PedidoRequest request) {
		PedidoAluguel pedido = bloquearPedido(pedidoId);
		aplicarModificacao(pedido, request);
		return toResponse(pedido);
	}

	private void aplicarModificacao(PedidoAluguel pedido, PedidoRequest request) {
		Automovel automovel = buscarAutomovel(request);
		// A regra de status (entidade) é verificada antes da disponibilidade;
		// se o automóvel estiver indisponível, a exceção desfaz a alteração (rollback)
		pedido.modificar(automovel, request.dataInicio(), request.dataFim());
		verificarDisponibilidade(automovel, request);
	}

	// US12
	@Transactional
	public PedidoResponse cancelar(Long clienteId, Long pedidoId) {
		PedidoAluguel pedido = exigirDoCliente(clienteId, bloquearPedido(pedidoId));
		pedido.cancelar();
		return toResponse(pedido);
	}

	// US13
	@Transactional(readOnly = true)
	public List<PedidoResponse> listarPorStatus(StatusPedido status) {
		return toResponses(pedidoRepository.findByStatusOrderByDataCriacaoAsc(status));
	}

	// US13 e US14
	@Transactional(readOnly = true)
	public PedidoDetalheResponse detalharParaAgente(Long pedidoId) {
		PedidoAluguel pedido = buscarPedido(pedidoId);
		Cliente cliente = pedido.getCliente();
		List<RendimentoResponse> rendimentos = cliente.getRendimentos().stream()
				.map(r -> new RendimentoResponse(r.getId(), r.getEntidadeEmpregadora(), r.getValor()))
				.toList();

		return new PedidoDetalheResponse(
				toResponse(pedido),
				new DadosFinanceirosResponse(cliente.getNome(), cliente.getProfissao(), rendimentos, cliente.getRendaTotal()));
	}

	// US16: o pedido aprovado reserva o automóvel, então a disponibilidade é conferida de novo
	@Transactional
	public PedidoResponse aprovar(Long agenteId, Long pedidoId) {
		PedidoAluguel pedido = bloquearPedido(pedidoId);
		pedido.aprovar(buscarAgente(agenteId));

		Long automovelId = pedido.getAutomovel().getId();
		automovelRepository.bloquearParaReserva(automovelId);
		if (pedidoRepository.existsOutraReservaNoPeriodo(automovelId, pedido.getId(), STATUS_RESERVAM_AUTOMOVEL,
				pedido.getDataInicio(), pedido.getDataFim())) {
			throw new ConflitoException("Automóvel já reservado por outro pedido neste período.");
		}
		return toResponse(pedido);
	}

	// US16
	@Transactional
	public PedidoResponse reprovar(Long agenteId, Long pedidoId) {
		PedidoAluguel pedido = bloquearPedido(pedidoId);
		pedido.reprovar(buscarAgente(agenteId));
		return toResponse(pedido);
	}

	// US17 e US18
	@Transactional
	public PedidoResponse executarContrato(Long agenteId, Long pedidoId, ContratoRequest request) {
		PedidoAluguel pedido = bloquearPedido(pedidoId);
		// unproxy: o agente pode já estar carregado como proxy (ex.: avaliador do pedido)
		Agente agente = (Agente) Hibernate.unproxy(buscarAgente(agenteId));
		Contrato contrato = new Contrato(pedido, request.tipoProprietario(), agente);

		CreditoRequest credito = request.credito();
		if (credito != null) {
			if (!(agente instanceof Banco banco)) {
				throw new NegocioException(HttpStatus.FORBIDDEN, "Apenas agentes do tipo Banco podem conceder crédito.");
			}
			contrato.setCredito(banco.concederCredito(credito.valor(), credito.taxaJuros(), credito.prazoMeses()));
		}

		contratoRepository.save(contrato);
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

	// Usado nas alterações: impede que, por exemplo, cancelar e executar contrato leiam o mesmo status ao mesmo tempo
	private PedidoAluguel bloquearPedido(Long pedidoId) {
		return pedidoRepository.bloquearParaAlteracao(pedidoId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado."));
	}

	private PedidoAluguel exigirDoCliente(Long clienteId, PedidoAluguel pedido) {
		if (!pedido.getCliente().getId().equals(clienteId)) {
			throw new RecursoNaoEncontradoException("Pedido não encontrado.");
		}
		return pedido;
	}

	private Agente buscarAgente(Long agenteId) {
		return agenteRepository.findById(agenteId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Agente não encontrado."));
	}

	// Busca os contratos de todos os pedidos contratados da lista em uma única consulta
	private List<PedidoResponse> toResponses(List<PedidoAluguel> pedidos) {
		List<Long> contratados = pedidos.stream()
				.filter(p -> p.getStatus() == StatusPedido.CONTRATADO)
				.map(PedidoAluguel::getId)
				.toList();
		Map<Long, Contrato> contratos = contratados.isEmpty()
				? Map.of()
				: contratoRepository.findByPedidoIdIn(contratados).stream()
						.collect(Collectors.toMap(c -> c.getPedido().getId(), Function.identity()));

		return pedidos.stream()
				.map(p -> toResponse(p, contratos.get(p.getId())))
				.toList();
	}

	private PedidoResponse toResponse(PedidoAluguel pedido) {
		Contrato contrato = pedido.getStatus() == StatusPedido.CONTRATADO
				? contratoRepository.findByPedidoId(pedido.getId()).orElse(null)
				: null;
		return toResponse(pedido, contrato);
	}

	private PedidoResponse toResponse(PedidoAluguel pedido, Contrato contrato) {
		Cliente cliente = pedido.getCliente();
		Agente avaliador = pedido.getAvaliador();
		ContratoCredito credito = contrato != null ? contrato.getCredito() : null;

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
				contrato != null ? contrato.getTipoProprietario().name() : null,
				contrato != null && contrato.getProprietario() != null
						? AuthService.nomeExibicao((Usuario) Hibernate.unproxy(contrato.getProprietario()))
						: null,
				contrato != null ? contrato.getDataExecucao() : null,
				credito != null
						? new CreditoResponse(credito.getValor(), credito.getTaxaJuros(), credito.getPrazoMeses(),
								credito.getBanco().getRazaoSocial())
						: null);
	}

}
