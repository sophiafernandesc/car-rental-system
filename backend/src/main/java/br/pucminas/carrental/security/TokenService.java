package br.pucminas.carrental.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import br.pucminas.carrental.model.Usuario;

@Component
public class TokenService {

	public static final String CLAIM_PERFIL = "perfil";

	private final JwtEncoder jwtEncoder;
	private final long expiracaoHoras;

	public TokenService(JwtEncoder jwtEncoder, @Value("${jwt.expiracao-horas}") long expiracaoHoras) {
		this.jwtEncoder = jwtEncoder;
		this.expiracaoHoras = expiracaoHoras;
	}

	// subject é o id do usuario
	public String gerarToken(Usuario usuario) {
		Instant agora = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("carrental")
				.issuedAt(agora)
				.expiresAt(agora.plus(expiracaoHoras, ChronoUnit.HOURS))
				.subject(usuario.getId().toString())
				.claim("login", usuario.getLogin())
				.claim(CLAIM_PERFIL, usuario.getPerfil().name())
				.build();

		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}

}
