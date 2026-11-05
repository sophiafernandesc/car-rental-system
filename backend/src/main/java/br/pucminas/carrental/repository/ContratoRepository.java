package br.pucminas.carrental.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.pucminas.carrental.model.Contrato;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {

	@EntityGraph(attributePaths = { "credito", "credito.banco", "proprietario" })
	Optional<Contrato> findByPedidoId(Long pedidoId);

	@EntityGraph(attributePaths = { "credito", "credito.banco", "proprietario" })
	List<Contrato> findByPedidoIdIn(Collection<Long> pedidoIds);

}
