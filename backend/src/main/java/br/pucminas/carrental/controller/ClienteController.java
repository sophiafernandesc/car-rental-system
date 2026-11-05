package br.pucminas.carrental.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.pucminas.carrental.dto.AtualizarClienteRequest;
import br.pucminas.carrental.dto.ClienteResponse;
import br.pucminas.carrental.dto.RendimentoRequest;
import br.pucminas.carrental.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// Operações sobre o cliente autenticado (id obtido do token)
@RestController
@RequestMapping("/clientes/me")
@RequiredArgsConstructor
public class ClienteController {

	private final ClienteService clienteService;

	// US05
	@GetMapping
	public ClienteResponse buscar(@AuthenticationPrincipal Jwt jwt) {
		return clienteService.buscar(clienteId(jwt));
	}

	// US06
	@PutMapping
	public ClienteResponse atualizar(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody AtualizarClienteRequest request) {
		return clienteService.atualizar(clienteId(jwt), request);
	}

	// US08
	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(@AuthenticationPrincipal Jwt jwt) {
		clienteService.excluir(clienteId(jwt));
	}

	// US07
	@PostMapping("/rendimentos")
	@ResponseStatus(HttpStatus.CREATED)
	public ClienteResponse adicionarRendimento(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody RendimentoRequest request) {
		return clienteService.adicionarRendimento(clienteId(jwt), request);
	}

	// US07
	@PutMapping("/rendimentos/{rendimentoId}")
	public ClienteResponse atualizarRendimento(@AuthenticationPrincipal Jwt jwt, @PathVariable Long rendimentoId,
			@Valid @RequestBody RendimentoRequest request) {
		return clienteService.atualizarRendimento(clienteId(jwt), rendimentoId, request);
	}

	// US07
	@DeleteMapping("/rendimentos/{rendimentoId}")
	public ClienteResponse removerRendimento(@AuthenticationPrincipal Jwt jwt, @PathVariable Long rendimentoId) {
		return clienteService.removerRendimento(clienteId(jwt), rendimentoId);
	}

	private Long clienteId(Jwt jwt) {
		return Long.valueOf(jwt.getSubject());
	}

}
