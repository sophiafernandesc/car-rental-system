package br.pucminas.carrental.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class ClienteTest {

	@Test
	void rendaTotalSomaOsRendimentos() {
		Cliente cliente = new Cliente();
		cliente.adicionarRendimento(new Rendimento("Empresa X", new BigDecimal("5000.00")));
		cliente.adicionarRendimento(new Rendimento("Freelance", new BigDecimal("1500.50")));

		assertThat(cliente.getRendaTotal()).isEqualByComparingTo("6500.50");
	}

	@Test
	void rendaTotalSemRendimentosEhZero() {
		assertThat(new Cliente().getRendaTotal()).isEqualByComparingTo(BigDecimal.ZERO);
	}

	@Test
	void naoPermiteMaisDeTresRendimentos() {
		Cliente cliente = new Cliente();
		for (int i = 0; i < Cliente.MAX_RENDIMENTOS; i++) {
			cliente.adicionarRendimento(new Rendimento("Empregador " + i, BigDecimal.TEN));
		}

		assertThatThrownBy(() -> cliente.adicionarRendimento(new Rendimento("Extra", BigDecimal.ONE)))
				.isInstanceOf(IllegalStateException.class);
		assertThat(cliente.getRendimentos()).hasSize(Cliente.MAX_RENDIMENTOS);
	}

}
