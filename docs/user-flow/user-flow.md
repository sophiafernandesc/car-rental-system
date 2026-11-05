# User Flow  Sistema de Aluguel de Carros

> **Legenda:** retângulos = telas · losangos = decisões · arredondados = ações/resultados

---

## 1. Acesso ao sistema (autenticação)

O sistema só pode ser utilizado após cadastro prévio e login.

```mermaid
flowchart TD
    A([Acessa o sistema]) --> B{Possui conta?}
    B -- Não --> C[Tela de Cadastro]
    C --> D{Tipo de usuário}
    D -- Cliente --> E["Dados pessoais<br/>RG, CPF, nome, endereço, profissão"]
    E --> F["Rendimentos<br/>entidade empregadora + valor (máx. 3)"]
    D -- Agente --> G["Dados do agente<br/>CNPJ, razão social, tipo Empresa/Banco"]
    F --> H([Conta criada])
    G --> H
    H --> I
    B -- Sim --> I[Tela de Login]
    I --> J{Credenciais válidas?}
    J -- Não --> K([Exibe erro]) --> I
    J -- Sim --> L{Perfil do usuário}
    L -- Cliente --> M[[Dashboard do Cliente]]
    L -- Agente --> N[[Dashboard do Agente]]
```

---

## 2. Fluxo do Cliente

O cliente pode **introduzir, modificar, consultar e cancelar** pedidos de aluguel, além de gerenciar seu perfil (CRUD de cliente).

```mermaid
flowchart TD
    A[[Dashboard do Cliente]] --> B[Meus Pedidos]
    A --> C[Novo Pedido]
    A --> D[Meu Perfil]
    A --> Z([Logout])

    %% Novo pedido
    C --> C1[Seleciona automóvel]
    C1 --> C2[Informa período do aluguel]
    C2 --> C3{Confirma pedido?}
    C3 -- Não --> A
    C3 -- Sim --> C4(["Pedido criado<br/>status: EM_ANALISE"])
    C4 --> B

    %% Consulta
    B --> B1["Lista de pedidos<br/>com status"]
    B1 --> B2[Detalhes do pedido]
    B2 --> B3{Status permite alteração?}
    B3 -- "Sim (EM_ANALISE)" --> B4[Modificar pedido]
    B4 --> B5([Pedido atualizado]) --> B2
    B3 -- "Sim (EM_ANALISE / APROVADO)" --> B6{Confirma cancelamento?}
    B6 -- Sim --> B7(["Pedido CANCELADO"]) --> B1
    B6 -- Não --> B2
    B3 -- "Não (CONTRATADO / REPROVADO / CANCELADO)" --> B8([Apenas visualização])

    %% Perfil
    D --> D1[Visualizar dados]
    D1 --> D2[Editar dados pessoais]
    D1 --> D3["Gerenciar rendimentos (máx. 3)"]
    D1 --> D4{Excluir conta?}
    D2 --> D5([Dados atualizados]) --> D1
    D3 --> D5
    D4 -- Sim --> D6{"Tem pedido APROVADO<br/>ou CONTRATADO?"}
    D6 -- Sim --> D7(["Exclusão bloqueada"]) --> D1
    D6 -- Não --> Z
    D4 -- Não --> D1
```

---

## 3. Fluxo do Agente (Empresa / Banco)

Os agentes podem **modificar e avaliar** pedidos. Pedidos com parecer financeiro positivo seguem para **execução do contrato**. Apenas **bancos** podem conceder **contrato de crédito**.

```mermaid
flowchart TD
    A[[Dashboard do Agente]] --> B[Pedidos para análise]
    A --> V[Automóveis]
    A --> Z([Logout])

    %% Avaliação
    B --> B1[Detalhes do pedido]
    B1 --> B2["Dados do cliente<br/>profissão, empregadores, rendimentos"]
    B2 --> B3{Ação}
    B3 -- Modificar --> B4[Editar pedido] --> B1
    B3 -- Avaliar --> B5{Parecer financeiro}
    B5 -- Negativo --> B6(["Pedido REPROVADO"]) --> B
    B5 -- Positivo --> B8{"Automóvel livre<br/>no período?"}
    B8 -- Sim --> B7(["Pedido APROVADO"])
    B8 -- Não --> B9(["Aprovação recusada:<br/>carro já reservado"]) --> B1

    %% Contrato
    B7 --> C1[Executar contrato]
    C1 --> C2{"Proprietário do automóvel"}
    C2 --> C3[Cliente do pedido]
    C2 --> C4["O próprio agente<br/>(Empresa ou Banco)"]
    C3 --> C6{Agente é Banco?}
    C4 --> C6
    C6 -- Sim --> C7{Conceder contrato de crédito?}
    C7 -- Sim --> C8["Informa valor, juros e prazo"] --> C9
    C7 -- Não --> C9
    C6 -- Não --> C9(["Pedido CONTRATADO"])
    C9 --> B

    %% Automóveis
    V --> V1["Lista de automóveis"]
    V1 --> V2["Cadastrar / editar automóvel<br/>matrícula, ano, marca, modelo, placa"]
    V2 --> V1
    V1 --> V3{"Remover: automóvel<br/>tem pedidos?"}
    V3 -- Sim --> V4(["Remoção bloqueada"]) --> V1
    V3 -- Não --> V5(["Automóvel removido"]) --> V1
```

---

## 4. Ciclo de vida do Pedido de Aluguel

Estados do pedido e quem dispara cada transição.

```mermaid
stateDiagram-v2
    [*] --> EM_ANALISE: Cliente cria pedido
    EM_ANALISE --> EM_ANALISE: Cliente/Agente modifica
    EM_ANALISE --> APROVADO: Agente dá parecer positivo
    EM_ANALISE --> REPROVADO: Agente dá parecer negativo
    EM_ANALISE --> CANCELADO: Cliente cancela
    APROVADO --> CONTRATADO: Agente executa contrato
    APROVADO --> CANCELADO: Cliente cancela
    REPROVADO --> [*]
    CANCELADO --> [*]
    CONTRATADO --> [*]
```

---

## Decisões adotadas

Pontos que o enunciado não define. Estão implementados conforme abaixo, mas **ainda não foram validados com o professor**.

- [x] **Cadastro de automóveis:** quem cadastra? (adotado: agentes)
- [x] **Cadastro de agentes:** autocadastro ou feito por um administrador? (adotado: autocadastro)
- [x] **Regras de alteração:** cliente pode modificar apenas pedidos `EM_ANALISE`? Pode cancelar após `APROVADO`? (adotado: modifica só em `EM_ANALISE`; cancela em `EM_ANALISE` ou `APROVADO`)
- [x] **Matrícula x placa:** interpretar "matrícula" como RENAVAM? (adotado: RENAVAM, 11 dígitos)
