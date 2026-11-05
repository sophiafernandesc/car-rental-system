package br.pucminas.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.pucminas.carrental.model.Automovel;

public interface AutomovelRepository extends JpaRepository<Automovel, Long> {
}
