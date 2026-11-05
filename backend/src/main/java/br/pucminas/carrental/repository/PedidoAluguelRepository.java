package br.pucminas.carrental.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.pucminas.carrental.model.PedidoAluguel;
import br.pucminas.carrental.model.StatusPedido;

public interface PedidoAluguelRepository extends JpaRepository<PedidoAluguel, Long> {

	@EntityGraph(attributePaths = { "automovel", "cliente", "avaliador" })
	List<PedidoAluguel> findByClienteIdOrderByDataCriacaoDesc(Long clienteId);

	@EntityGraph(attributePaths = { "automovel", "cliente", "avaliador" })
	List<PedidoAluguel> findByStatusOrderByDataCriacaoAsc(StatusPedido status);

	boolean existsByClienteIdAndStatusIn(Long clienteId, Collection<StatusPedido> status);

	void deleteByClienteId(Long clienteId);

	boolean existsByAutomovelId(Long automovelId);

	// Verifica se o automóvel já está reservado em um período que se sobrepõe ao informado
	@Query("""
			select count(p) > 0 from PedidoAluguel p
			where p.automovel.id = :automovelId
				and p.status in :status
				and p.dataInicio <= :dataFim
				and p.dataFim >= :dataInicio
			""")
	boolean existsReservaNoPeriodo(@Param("automovelId") Long automovelId,
			@Param("status") Collection<StatusPedido> status,
			@Param("dataInicio") LocalDate dataInicio,
			@Param("dataFim") LocalDate dataFim);

	// Mesma verificação, desconsiderando o próprio pedido (usada na aprovação)
	@Query("""
			select count(p) > 0 from PedidoAluguel p
			where p.automovel.id = :automovelId
				and p.id <> :pedidoId
				and p.status in :status
				and p.dataInicio <= :dataFim
				and p.dataFim >= :dataInicio
			""")
	boolean existsOutraReservaNoPeriodo(@Param("automovelId") Long automovelId,
			@Param("pedidoId") Long pedidoId,
			@Param("status") Collection<StatusPedido> status,
			@Param("dataInicio") LocalDate dataInicio,
			@Param("dataFim") LocalDate dataFim);

}
