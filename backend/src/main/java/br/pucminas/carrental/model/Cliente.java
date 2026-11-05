package br.pucminas.carrental.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Cliente extends Usuario {

	public static final int MAX_RENDIMENTOS = 3;

	@Column(nullable = false, unique = true)
	private String rg;

	@Column(nullable = false, unique = true, length = 11)
	private String cpf;

	@Column(nullable = false)
	private String nome;

	@Column(nullable = false)
	private String endereco;

	@Column(nullable = false)
	private String profissao;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "cliente_id", nullable = false)
	private List<Rendimento> rendimentos = new ArrayList<>();

	public Cliente() {
		super(Perfil.CLIENTE);
	}

	public void adicionarRendimento(Rendimento rendimento) {
		if (rendimentos.size() >= MAX_RENDIMENTOS) {
			throw new IllegalStateException("O cliente pode ter no máximo " + MAX_RENDIMENTOS + " rendimentos.");
		}
		rendimentos.add(rendimento);
	}

	public BigDecimal getRendaTotal() {
		return rendimentos.stream()
				.map(Rendimento::getValor)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

}
