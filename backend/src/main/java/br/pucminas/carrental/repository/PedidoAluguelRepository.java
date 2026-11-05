package br.pucminas.carrental.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.pucminas.carrental.model.PedidoAluguel;
import br.pucminas.carrental.model.StatusPedido;

public interface PedidoAluguelRepository extends JpaRepository<PedidoAluguel, Long> {

	List<PedidoAluguel> findByClienteIdOrderByDataCriacaoDesc(Long clienteId);

	List<PedidoAluguel> findByStatusOrderByDataCriacaoAsc(StatusPedido status);

	boolean existsByClienteIdAndStatusIn(Long clienteId, Collection<StatusPedido> status);

	void deleteByClienteId(Long clienteId);

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

}
