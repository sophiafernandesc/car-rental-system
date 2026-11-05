package br.pucminas.carrental.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.pucminas.carrental.model.Automovel;
import jakarta.persistence.LockModeType;

public interface AutomovelRepository extends JpaRepository<Automovel, Long> {

	boolean existsByMatricula(String matricula);

	boolean existsByPlaca(String placa);

	boolean existsByMatriculaAndIdNot(String matricula, Long id);

	boolean existsByPlacaAndIdNot(String placa, Long id);

	// Bloqueia o automóvel até o fim da transação: aprovações concorrentes do mesmo carro são executadas em sequência
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select a from Automovel a where a.id = :id")
	Optional<Automovel> bloquearParaReserva(@Param("id") Long id);

}
