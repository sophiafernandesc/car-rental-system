package br.pucminas.carrental.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.pucminas.carrental.model.PedidoAluguel;
import br.pucminas.carrental.model.StatusPedido;

public interface PedidoAluguelRepository extends JpaRepository<PedidoAluguel, Long> {

	List<PedidoAluguel> findByClienteIdOrderByDataCriacaoDesc(Long clienteId);

	List<PedidoAluguel> findByStatusOrderByDataCriacaoAsc(StatusPedido status);

}
