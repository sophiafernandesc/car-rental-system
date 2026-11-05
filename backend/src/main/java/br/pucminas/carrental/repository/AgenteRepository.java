package br.pucminas.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.pucminas.carrental.model.Agente;

public interface AgenteRepository extends JpaRepository<Agente, Long> {
}
