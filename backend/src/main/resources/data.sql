-- Carga inicial: agentes de demonstração e frota de automóveis.
-- Executado a cada inicialização e idempotente.
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

-- Frota inicial: só é inserida com a tabela vazia, para não desfazer o que os agentes cadastram, editam ou removem
INSERT INTO automovel (matricula, ano, marca, modelo, placa)
SELECT * FROM (VALUES
	('00123456789', 2024, 'Volkswagen', 'Polo', 'QWE1A23'),
	('00234567891', 2023, 'Chevrolet', 'Onix', 'RTY2B34'),
	('00345678912', 2024, 'Fiat', 'Pulse', 'UIO3C45'),
	('00456789123', 2022, 'Toyota', 'Corolla', 'PAS4D56'),
	('00567891234', 2025, 'Jeep', 'Compass', 'DFG5E67'),
	('00678912345', 2024, 'Fiat', 'Mobi', 'GHJ6F78'),
	('00789123456', 2024, 'Renault', 'Kwid', 'KLZ7G89'),
	('00891234567', 2023, 'Volkswagen', 'Gol', 'XCV8H90'),
	('00912345678', 2024, 'Hyundai', 'HB20', 'BNM9J01'),
	('01023456789', 2023, 'Fiat', 'Argo', 'QAZ1K12'),
	('01134567891', 2024, 'Chevrolet', 'Onix Plus', 'WSX2L23'),
	('01245678912', 2025, 'Volkswagen', 'Virtus', 'EDC3M34'),
	('01356789123', 2024, 'Volkswagen', 'T-Cross', 'RFV4N45'),
	('01467891234', 2025, 'Hyundai', 'Creta', 'TGB5P56'),
	('01578912345', 2023, 'Jeep', 'Renegade', 'YHN6Q67')
) AS frota (matricula, ano, marca, modelo, placa)
WHERE NOT EXISTS (SELECT 1 FROM automovel);

-- Contratos executados antes de o proprietário ser registrado: só o tipo CLIENTE tem dono comprovado (o cliente do pedido).
-- Nos tipos EMPRESA e BANCO não há registro de qual agente executou, então o proprietário fica pendente (NULL)
UPDATE contrato c SET proprietario_id = p.cliente_id
FROM pedido_aluguel p
WHERE c.pedido_id = p.id AND c.tipo_proprietario = 'CLIENTE' AND c.proprietario_id IS NULL;
