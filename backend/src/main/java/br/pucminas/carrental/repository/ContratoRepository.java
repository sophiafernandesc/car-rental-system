package br.pucminas.carrental.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.pucminas.carrental.model.Contrato;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {

	Optional<Contrato> findByPedidoId(Long pedidoId);

}
