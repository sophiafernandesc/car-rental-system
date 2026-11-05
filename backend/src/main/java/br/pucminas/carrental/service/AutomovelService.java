package br.pucminas.carrental.service;

import java.time.Year;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.pucminas.carrental.dto.AutomovelRequest;
import br.pucminas.carrental.dto.AutomovelResponse;
import br.pucminas.carrental.exception.ConflitoException;
import br.pucminas.carrental.exception.NegocioException;
import br.pucminas.carrental.exception.RecursoNaoEncontradoException;
import br.pucminas.carrental.model.Automovel;
import br.pucminas.carrental.repository.AutomovelRepository;
import br.pucminas.carrental.repository.PedidoAluguelRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutomovelService {

	private final AutomovelRepository automovelRepository;
	private final PedidoAluguelRepository pedidoRepository;

	@Transactional(readOnly = true)
	public List<AutomovelResponse> listar() {
		return automovelRepository.findAll(Sort.by("marca", "modelo")).stream()
				.map(AutomovelService::toResponse)
				.toList();
	}

	// US19
	@Transactional
	public AutomovelResponse cadastrar(AutomovelRequest request) {
		String placa = request.placa().toUpperCase();
		if (automovelRepository.existsByMatricula(request.matricula())) {
			throw new ConflitoException("Matrícula já cadastrada.");
		}
		if (automovelRepository.existsByPlaca(placa)) {
			throw new ConflitoException("Placa já cadastrada.");
		}
		Automovel automovel = new Automovel();
		preencher(automovel, request, placa);
		return toResponse(automovelRepository.save(automovel));
	}

	// US20
	@Transactional
	public AutomovelResponse atualizar(Long automovelId, AutomovelRequest request) {
		Automovel automovel = buscarAutomovel(automovelId);
		String placa = request.placa().toUpperCase();
		if (automovelRepository.existsByMatriculaAndIdNot(request.matricula(), automovelId)) {
			throw new ConflitoException("Matrícula já cadastrada.");
		}
		if (automovelRepository.existsByPlacaAndIdNot(placa, automovelId)) {
			throw new ConflitoException("Placa já cadastrada.");
		}
		preencher(automovel, request, placa);
		return toResponse(automovel);
	}

	// US20: automóvel com pedidos é mantido para preservar o histórico
	@Transactional
	public void remover(Long automovelId) {
		Automovel automovel = buscarAutomovel(automovelId);
		if (pedidoRepository.existsByAutomovelId(automovelId)) {
			throw new ConflitoException("Não é possível remover um automóvel que possui pedidos.");
		}
		automovelRepository.delete(automovel);
	}

	private void preencher(Automovel automovel, AutomovelRequest request, String placa) {
		if (request.ano() > Year.now().getValue() + 1) {
			throw new NegocioException(HttpStatus.BAD_REQUEST, "O ano não pode ser posterior a " + (Year.now().getValue() + 1) + ".");
		}
		automovel.setMatricula(request.matricula());
		automovel.setAno(request.ano());
		automovel.setMarca(request.marca());
		automovel.setModelo(request.modelo());
		automovel.setPlaca(placa);
	}

	private Automovel buscarAutomovel(Long automovelId) {
		return automovelRepository.findById(automovelId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Automóvel não encontrado."));
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
