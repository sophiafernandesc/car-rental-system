package br.pucminas.carrental.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.pucminas.carrental.dto.AutomovelRequest;
import br.pucminas.carrental.dto.AutomovelResponse;
import br.pucminas.carrental.service.AutomovelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// Gestão da frota pelo agente; a listagem fica em GET /automoveis
@RestController
@RequestMapping("/agente/automoveis")
@RequiredArgsConstructor
public class AgenteAutomovelController {

	private final AutomovelService automovelService;

	// US19
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public AutomovelResponse cadastrar(@Valid @RequestBody AutomovelRequest request) {
		return automovelService.cadastrar(request);
	}

	// US20
	@PutMapping("/{automovelId}")
	public AutomovelResponse atualizar(@PathVariable Long automovelId, @Valid @RequestBody AutomovelRequest request) {
		return automovelService.atualizar(automovelId, request);
	}

	// US20
	@DeleteMapping("/{automovelId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long automovelId) {
		automovelService.remover(automovelId);
	}

}
