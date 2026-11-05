package br.pucminas.carrental.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.pucminas.carrental.dto.CadastroAgenteRequest;
import br.pucminas.carrental.dto.CadastroClienteRequest;
import br.pucminas.carrental.dto.LoginRequest;
import br.pucminas.carrental.dto.LoginResponse;
import br.pucminas.carrental.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	// US01
	@PostMapping("/cadastro")
	@ResponseStatus(HttpStatus.CREATED)
	public Map<String, Long> cadastrar(@Valid @RequestBody CadastroClienteRequest request) {
		return Map.of("id", authService.cadastrarCliente(request));
	}

	// US02
	@PostMapping("/cadastro/agente")
	@ResponseStatus(HttpStatus.CREATED)
	public Map<String, Long> cadastrarAgente(@Valid @RequestBody CadastroAgenteRequest request) {
		return Map.of("id", authService.cadastrarAgente(request));
	}

	// US03
	@PostMapping("/login")
	public LoginResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

}
