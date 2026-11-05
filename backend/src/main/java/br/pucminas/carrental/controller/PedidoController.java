package br.pucminas.carrental.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.pucminas.carrental.dto.PedidoRequest;
import br.pucminas.carrental.dto.PedidoResponse;
import br.pucminas.carrental.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// Pedidos do cliente autenticado (id obtido do token)
@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

	private final PedidoService pedidoService;

	// US09
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PedidoResponse criar(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody PedidoRequest request) {
		return pedidoService.criar(clienteId(jwt), request);
	}

	// US10
	@GetMapping
	public List<PedidoResponse> listar(@AuthenticationPrincipal Jwt jwt) {
		return pedidoService.listarDoCliente(clienteId(jwt));
	}

	// US10
	@GetMapping("/{pedidoId}")
	public PedidoResponse buscar(@AuthenticationPrincipal Jwt jwt, @PathVariable Long pedidoId) {
		return pedidoService.buscarDoCliente(clienteId(jwt), pedidoId);
	}

	// US11
	@PutMapping("/{pedidoId}")
	public PedidoResponse modificar(@AuthenticationPrincipal Jwt jwt, @PathVariable Long pedidoId,
			@Valid @RequestBody PedidoRequest request) {
		return pedidoService.modificar(clienteId(jwt), pedidoId, request);
	}

	// US12
	@PostMapping("/{pedidoId}/cancelar")
	public PedidoResponse cancelar(@AuthenticationPrincipal Jwt jwt, @PathVariable Long pedidoId) {
		return pedidoService.cancelar(clienteId(jwt), pedidoId);
	}

	private Long clienteId(Jwt jwt) {
		return Long.valueOf(jwt.getSubject());
	}

}
