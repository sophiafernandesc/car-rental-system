-- Seed do MVP: agentes (US02) e automóveis (US19) são inseridos diretamente no banco.
-- Executado a cada inicialização; ON CONFLICT torna o script idempotente.
-- Senha dos agentes: agente123

-- Agente do tipo Empresa
WITH u AS (
	INSERT INTO usuario (login, senha, perfil)
	VALUES ('locadora', '$2y$10$RAJGB275DIjYK7468JAuyuAo6YQVKPuxlWskXgA.xhZYKYCgl2R6C', 'AGENTE')
	ON CONFLICT (login) DO NOTHING
	RETURNING id
), a AS (
	INSERT INTO agente (id, cnpj, razao_social)
	SELECT id, '11222333000181', 'Locadora Minas Ltda' FROM u
	RETURNING id
)
INSERT INTO empresa (id) SELECT id FROM a;

-- Agente do tipo Banco
WITH u AS (
	INSERT INTO usuario (login, senha, perfil)
	VALUES ('banco', '$2y$10$RAJGB275DIjYK7468JAuyuAo6YQVKPuxlWskXgA.xhZYKYCgl2R6C', 'AGENTE')
	ON CONFLICT (login) DO NOTHING
	RETURNING id
), a AS (
	INSERT INTO agente (id, cnpj, razao_social)
	SELECT id, '33000167000101', 'Banco Horizonte S.A.' FROM u
	RETURNING id
)
INSERT INTO banco (id) SELECT id FROM a;

-- Automóveis
INSERT INTO automovel (matricula, ano, marca, modelo, placa) VALUES
	('00123456789', 2024, 'Volkswagen', 'Polo', 'QWE1A23'),
	('00234567891', 2023, 'Chevrolet', 'Onix', 'RTY2B34'),
	('00345678912', 2024, 'Fiat', 'Pulse', 'UIO3C45'),
	('00456789123', 2022, 'Toyota', 'Corolla', 'PAS4D56'),
	('00567891234', 2025, 'Jeep', 'Compass', 'DFG5E67')
ON CONFLICT (placa) DO NOTHING;
