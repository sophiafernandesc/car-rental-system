package br.pucminas.carrental.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.pucminas.carrental.dto.AtualizarClienteRequest;
import br.pucminas.carrental.dto.ClienteResponse;
import br.pucminas.carrental.dto.RendimentoRequest;
import br.pucminas.carrental.dto.RendimentoResponse;
import br.pucminas.carrental.exception.ConflitoException;
import br.pucminas.carrental.exception.RecursoNaoEncontradoException;
import br.pucminas.carrental.model.Cliente;
import br.pucminas.carrental.model.Rendimento;
import br.pucminas.carrental.model.StatusPedido;
import br.pucminas.carrental.repository.ClienteRepository;
import br.pucminas.carrental.repository.PedidoAluguelRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {

	// Pedidos que impedem a exclusão da conta (US08)
	private static final List<StatusPedido> STATUS_IMPEDEM_EXCLUSAO =
			List.of(StatusPedido.APROVADO, StatusPedido.CONTRATADO);

	private final ClienteRepository clienteRepository;
	private final PedidoAluguelRepository pedidoRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional(readOnly = true)
	public ClienteResponse buscar(Long clienteId) {
		return toResponse(buscarCliente(clienteId));
	}

	@Transactional
	public ClienteResponse atualizar(Long clienteId, AtualizarClienteRequest request) {
		Cliente cliente = buscarCliente(clienteId);
		cliente.setNome(request.nome());
		cliente.setEndereco(request.endereco());
		cliente.setProfissao(request.profissao());
		if (request.novaSenha() != null) {
			cliente.setSenha(passwordEncoder.encode(request.novaSenha()));
		}
		return toResponse(cliente);
	}

	@Transactional
	public void excluir(Long clienteId) {
		Cliente cliente = buscarCliente(clienteId);
		if (pedidoRepository.existsByClienteIdAndStatusIn(clienteId, STATUS_IMPEDEM_EXCLUSAO)) {
			throw new ConflitoException("Não é possível excluir a conta com pedidos aprovados ou contratados.");
		}
		pedidoRepository.deleteByClienteId(clienteId);
		clienteRepository.delete(cliente);
	}

	@Transactional
	public ClienteResponse adicionarRendimento(Long clienteId, RendimentoRequest request) {
		Cliente cliente = buscarCliente(clienteId);
		cliente.adicionarRendimento(new Rendimento(request.entidadeEmpregadora(), request.valor()));
		// Garante que o novo rendimento receba um id antes de montar a resposta
		clienteRepository.flush();
		return toResponse(cliente);
	}

	@Transactional
	public ClienteResponse atualizarRendimento(Long clienteId, Long rendimentoId, RendimentoRequest request) {
		Cliente cliente = buscarCliente(clienteId);
		Rendimento rendimento = buscarRendimento(cliente, rendimentoId);
		rendimento.setEntidadeEmpregadora(request.entidadeEmpregadora());
		rendimento.setValor(request.valor());
		return toResponse(cliente);
	}

	@Transactional
	public ClienteResponse removerRendimento(Long clienteId, Long rendimentoId) {
		Cliente cliente = buscarCliente(clienteId);
		cliente.getRendimentos().remove(buscarRendimento(cliente, rendimentoId));
		return toResponse(cliente);
	}

	private Cliente buscarCliente(Long clienteId) {
		return clienteRepository.findById(clienteId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));
	}

	private Rendimento buscarRendimento(Cliente cliente, Long rendimentoId) {
		return cliente.getRendimentos().stream()
				.filter(r -> r.getId().equals(rendimentoId))
				.findFirst()
				.orElseThrow(() -> new RecursoNaoEncontradoException("Rendimento não encontrado."));
	}

	private ClienteResponse toResponse(Cliente cliente) {
		List<RendimentoResponse> rendimentos = cliente.getRendimentos().stream()
				.map(r -> new RendimentoResponse(r.getId(), r.getEntidadeEmpregadora(), r.getValor()))
				.toList();

		return new ClienteResponse(
				cliente.getId(),
				cliente.getLogin(),
				cliente.getRg(),
				cliente.getCpf(),
				cliente.getNome(),
				cliente.getEndereco(),
				cliente.getProfissao(),
				rendimentos,
				cliente.getRendaTotal());
	}

}
