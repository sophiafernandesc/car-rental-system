package br.pucminas.carrental.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.pucminas.carrental.dto.CadastroClienteRequest;
import br.pucminas.carrental.dto.LoginRequest;
import br.pucminas.carrental.dto.LoginResponse;
import br.pucminas.carrental.exception.ConflitoException;
import br.pucminas.carrental.exception.CredenciaisInvalidasException;
import br.pucminas.carrental.model.Agente;
import br.pucminas.carrental.model.Cliente;
import br.pucminas.carrental.model.Rendimento;
import br.pucminas.carrental.model.Usuario;
import br.pucminas.carrental.repository.ClienteRepository;
import br.pucminas.carrental.repository.UsuarioRepository;
import br.pucminas.carrental.security.TokenService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UsuarioRepository usuarioRepository;
	private final ClienteRepository clienteRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;

	@Transactional
	public Long cadastrarCliente(CadastroClienteRequest request) {
		String cpf = somenteDigitos(request.cpf());

		if (usuarioRepository.existsByLogin(request.login())) {
			throw new ConflitoException("Login já cadastrado.");
		}
		if (clienteRepository.existsByCpf(cpf)) {
			throw new ConflitoException("CPF já cadastrado.");
		}
		if (clienteRepository.existsByRg(request.rg())) {
			throw new ConflitoException("RG já cadastrado.");
		}

		Cliente cliente = new Cliente();
		cliente.setLogin(request.login());
		cliente.setSenha(passwordEncoder.encode(request.senha()));
		cliente.setRg(request.rg());
		cliente.setCpf(cpf);
		cliente.setNome(request.nome());
		cliente.setEndereco(request.endereco());
		cliente.setProfissao(request.profissao());
		if (request.rendimentos() != null) {
			request.rendimentos().forEach(r ->
					cliente.adicionarRendimento(new Rendimento(r.entidadeEmpregadora(), r.valor())));
		}

		return clienteRepository.save(cliente).getId();
	}

	@Transactional(readOnly = true)
	public LoginResponse login(LoginRequest request) {
		Usuario usuario = usuarioRepository.findByLogin(request.login())
				.filter(u -> passwordEncoder.matches(request.senha(), u.getSenha()))
				.orElseThrow(CredenciaisInvalidasException::new);

		return new LoginResponse(
				tokenService.gerarToken(usuario),
				usuario.getId(),
				nomeExibicao(usuario),
				usuario.getPerfil().name());
	}

	private String nomeExibicao(Usuario usuario) {
		return switch (usuario) {
			case Cliente cliente -> cliente.getNome();
			case Agente agente -> agente.getRazaoSocial();
			default -> usuario.getLogin();
		};
	}

	private String somenteDigitos(String valor) {
		return valor.replaceAll("\\D", "");
	}

}
