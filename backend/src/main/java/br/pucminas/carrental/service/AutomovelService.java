package br.pucminas.carrental.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.pucminas.carrental.dto.AutomovelResponse;
import br.pucminas.carrental.model.Automovel;
import br.pucminas.carrental.repository.AutomovelRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutomovelService {

	private final AutomovelRepository automovelRepository;

	@Transactional(readOnly = true)
	public List<AutomovelResponse> listar() {
		return automovelRepository.findAll(Sort.by("marca", "modelo")).stream()
				.map(AutomovelService::toResponse)
				.toList();
	}

	static AutomovelResponse toResponse(Automovel automovel) {
		return new AutomovelResponse(
				automovel.getId(),
				automovel.getMatricula(),
				automovel.getAno(),
				automovel.getMarca(),
				automovel.getModelo(),
				automovel.getPlaca());
	}

}
