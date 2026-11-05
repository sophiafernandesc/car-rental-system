package br.pucminas.carrental.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.pucminas.carrental.dto.ContratoRequest;
import br.pucminas.carrental.dto.PedidoResponse;
import br.pucminas.carrental.model.StatusPedido;
import br.pucminas.carrental.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// Avaliação de pedidos pelo agente autenticado (Empresa ou Banco)
@RestController
@RequestMapping("/agente/pedidos")
@RequiredArgsConstructor
public class AgentePedidoController {

	private final PedidoService pedidoService;

	// US13
	@GetMapping
	public List<PedidoResponse> listar(@RequestParam(defaultValue = "EM_ANALISE") StatusPedido status) {
		return pedidoService.listarPorStatus(status);
	}

	// US16
	@PostMapping("/{pedidoId}/aprovar")
	public PedidoResponse aprovar(@AuthenticationPrincipal Jwt jwt, @PathVariable Long pedidoId) {
		return pedidoService.aprovar(agenteId(jwt), pedidoId);
	}

	// US16
	@PostMapping("/{pedidoId}/reprovar")
	public PedidoResponse reprovar(@AuthenticationPrincipal Jwt jwt, @PathVariable Long pedidoId) {
		return pedidoService.reprovar(agenteId(jwt), pedidoId);
	}

	// US17
	@PostMapping("/{pedidoId}/contrato")
	@ResponseStatus(HttpStatus.CREATED)
	public PedidoResponse executarContrato(@AuthenticationPrincipal Jwt jwt, @PathVariable Long pedidoId,
			@Valid @RequestBody ContratoRequest request) {
		return pedidoService.executarContrato(agenteId(jwt), pedidoId, request);
	}

	private Long agenteId(Jwt jwt) {
		return Long.valueOf(jwt.getSubject());
	}

}
