package br.pucminas.carrental.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public abstract class Agente extends Usuario {

	@Column(nullable = false, unique = true, length = 14)
	private String cnpj;

	@Column(nullable = false)
	private String razaoSocial;

	protected Agente() {
		super(Perfil.AGENTE);
	}

}
