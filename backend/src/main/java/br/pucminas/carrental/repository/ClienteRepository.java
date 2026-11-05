package br.pucminas.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.pucminas.carrental.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

	boolean existsByCpf(String cpf);

	boolean existsByRg(String rg);

}
