# Documentação - Sistema de Aluguel de Carros

Artefatos de modelagem do projeto (Laboratório 02 - DIAW).

## Estrutura

```
docs/
├── user-flow/               # Fluxos de navegação (cliente e agente)
├── historias-usuario.md     # Histórias de usuário
└── diagramas/
    ├── casos-de-uso/v1, v2/ # Diagrama de Casos de Uso
    ├── classes/v1, v2/      # Diagrama de Classes
    └── pacotes/v1, v2/      # Diagrama de Pacotes (Visão Lógica)
```

## Versionamento dos diagramas

A avaliação considera a **evolução dos modelos**, então nenhuma versão é sobrescrita:

- Cada revisão vira uma nova pasta (`v1`, `v2`, ...) dentro do tipo de diagrama.
- Cada versão contém o fonte (`.puml`) e a imagem exportada (`.png`/`.svg`).
- Ao criar uma nova versão, registre abaixo o que mudou e o motivo (ex.: feedback em aula).

## Histórico de versões

| Diagrama | Versão | Data | Alterações |
|---|---|---|---|
| Casos de uso, Classes, Pacotes | v1 | 2026-11-04 | Versão inicial |
| Casos de uso | v2 | 2026-11-05 | US11, US12, US14, US15, US17 e US18 passam a implementadas. Nova legenda separa casos implementados e alimentados via seed (US02, US19/US20). "Conceder crédito" associado diretamente ao ator Banco. |
| Classes | v2 | 2026-11-05 | `PedidoAluguel` ganha `modificar()`, `cancelar()` e `contratar()`; `Contrato` ganha construtor que executa o contrato; `Banco` ganha `concederCredito()`. `Contrato`, `ContratoCredito` e `TipoProprietario` deixam de ser «futuro». Nota com as transições de status. |
| Pacotes | v2 | 2026-11-05 | Dependências ajustadas ao código: `service → security` (geração do token), `controller → model`, `dto → model`, `security → model`; removida `security → repository` (autenticação via JWT do Spring, sem filtro próprio). Front: novo pacote `utils` e dependências de `components` e `routes` explicitadas. |
