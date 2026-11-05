package br.pucminas.carrental.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.pucminas.carrental.dto.AutomovelResponse;
import br.pucminas.carrental.service.AutomovelService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/automoveis")
@RequiredArgsConstructor
public class AutomovelController {

	private final AutomovelService automovelService;

	@GetMapping
	public List<AutomovelResponse> listar() {
		return automovelService.listar();
	}

}
