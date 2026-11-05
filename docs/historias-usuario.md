# Histórias de Usuário

## Visão geral

| ID | História | Ator | Entrega | Escopo |
|---|---|---|---|---|
| US01 | Cadastrar-se como cliente | Visitante | Autenticação | MVP |
| US02 | Cadastrar-se como agente | Visitante | Autenticação | Pós-MVP |
| US03 | Fazer login | Usuário | Autenticação | MVP |
| US04 | Fazer logout | Usuário | Autenticação | MVP |
| US05 | Consultar meus dados | Cliente | CRUD Cliente | MVP |
| US06 | Atualizar meus dados pessoais | Cliente | CRUD Cliente | MVP |
| US07 | Gerenciar meus rendimentos | Cliente | CRUD Cliente | MVP |
| US08 | Excluir minha conta | Cliente | CRUD Cliente | MVP |
| US09 | Criar pedido de aluguel | Cliente | Protótipo | MVP |
| US10 | Consultar meus pedidos e status | Cliente | Protótipo | MVP |
| US11 | Modificar pedido | Cliente | Sistema completo | Pós-MVP |
| US12 | Cancelar pedido | Cliente | Sistema completo | Pós-MVP |
| US13 | Listar pedidos para análise | Agente | Sistema completo | MVP (simplificado) |
| US14 | Consultar dados financeiros do cliente | Agente | Sistema completo | Pós-MVP |
| US15 | Modificar pedido | Agente | Sistema completo | Pós-MVP |
| US16 | Avaliar pedido | Agente | Sistema completo | MVP (simplificado) |
| US17 | Executar contrato | Agente | Sistema completo | Pós-MVP |
| US18 | Conceder contrato de crédito | Banco | Sistema completo | Pós-MVP |
| US19 | Cadastrar automóvel | Agente | Sistema completo | Pós-MVP |
| US20 | Gerenciar automóveis | Agente | Sistema completo | Pós-MVP |

**Escopo:**
- **MVP** - implementado com back-end e tela.
- **MVP (simplificado)** - implementado apenas com os critérios marcados como MVP na história.
- **Pós-MVP** - implementado com back-end e tela após a entrega do MVP.

---

##  Autenticação

### US01 - Cadastrar-se como cliente
**Como** visitante, **quero** me cadastrar como cliente, **para** poder solicitar aluguéis de automóveis.

**Critérios de aceitação:**
- [x] O formulário solicita: login, senha, RG, CPF, nome, endereço e profissão.
- [x] Permite informar até **3** rendimentos (entidade empregadora + valor).
- [x] CPF, RG e login devem ser únicos no sistema.
- [x] CPF deve ter formato válido.
- [x] A senha é armazenada criptografada (hash).
- [x] Após o cadastro, o usuário é direcionado para a tela de login.

### US02 - Cadastrar-se como agente
**Como** visitante representando uma empresa ou banco, **quero** me cadastrar como agente, **para** poder avaliar pedidos de aluguel.

**Critérios de aceitação:**
- [x] O formulário solicita: login, senha, CNPJ, razão social e tipo (Empresa ou Banco).
- [x] CNPJ e login devem ser únicos no sistema.
- [x] A senha é armazenada criptografada (hash).

> **Decisão adotada:** autocadastro pela tela "Cadastre-se como agente". No MVP, os agentes eram inseridos via seed; o seed mantém dois agentes de demonstração.

### US03 - Fazer login
**Como** usuário cadastrado, **quero** entrar no sistema com login e senha, **para** acessar as funcionalidades do meu perfil.

**Critérios de aceitação:**
- [x] Credenciais inválidas exibem mensagem de erro, sem indicar qual campo está incorreto.
- [x] Cliente é redirecionado ao Dashboard do Cliente; agente, ao Dashboard do Agente.
- [x] Nenhuma página interna (front-end) ou endpoint protegido (back-end) é acessível sem autenticação.
- [x] Um usuário não acessa funcionalidades de outro perfil (ex.: cliente não avalia pedidos).

### US04 - Fazer logout
**Como** usuário autenticado, **quero** sair do sistema, **para** encerrar minha sessão com segurança.

**Critérios de aceitação:**
- [x] A sessão/token é descartado no cliente.
- [x] Após o logout, o usuário é redirecionado à tela de login e não acessa páginas internas.

---

## Cliente - Perfil (CRUD)

### US05 - Consultar meus dados
**Como** cliente, **quero** visualizar meus dados cadastrais, **para** conferir se estão corretos.

**Critérios de aceitação:**
- [x] Exibe dados pessoais, profissão e rendimentos cadastrados.
- [x] O cliente só visualiza os **próprios** dados.

### US06 - Atualizar meus dados pessoais
**Como** cliente, **quero** editar meus dados pessoais, **para** mantê-los atualizados.

**Critérios de aceitação:**
- [x] Permite editar nome, endereço, profissão e senha.
- [x] As mesmas validações do cadastro se aplicam (unicidade, formato de CPF).
- [x] Exibe confirmação após salvar.

> **Decisão adotada:** CPF, RG e login não são editáveis após o cadastro, então as validações de unicidade e formato ficam no cadastro (US01).

### US07 - Gerenciar meus rendimentos
**Como** cliente, **quero** adicionar, editar e remover meus rendimentos, **para** que os agentes avaliem corretamente minha capacidade financeira.

**Critérios de aceitação:**
- [x] Cada rendimento possui entidade empregadora e valor (maior que zero).
- [x] O sistema impede cadastrar mais de **3** rendimentos (validação no front-end **e** no back-end).

### US08 - Excluir minha conta
**Como** cliente, **quero** excluir minha conta, **para** deixar de utilizar o sistema.

**Critérios de aceitação:**
- [x] O sistema solicita confirmação antes de excluir.
- [x] Não é permitido excluir a conta com pedido `APROVADO` ou `CONTRATADO`.
- [x] Após a exclusão, a sessão é encerrada.

---

## Cliente - Pedidos de Aluguel

### US09 - Criar pedido de aluguel
**Como** cliente, **quero** criar um pedido de aluguel, **para** solicitar um automóvel.

**Critérios de aceitação:**
- [x] O cliente seleciona um automóvel disponível e informa o período (data de início e fim).
- [x] A data de início não pode ser anterior à data atual, e a data de fim deve ser posterior à de início.
- [x] O pedido é criado com status `EM_ANALISE` e registra a data de criação.

### US10 - Consultar meus pedidos e status
**Como** cliente, **quero** consultar meus pedidos e o status de cada um, **para** acompanhar o andamento das solicitações.

**Critérios de aceitação:**
- [x] Lista apenas os pedidos do cliente autenticado.
- [x] Exibe automóvel, período, status e data de criação.
- [x] O detalhe do pedido exibe o parecer do agente, quando houver.

### US11 - Modificar pedido
**Como** cliente, **quero** modificar um pedido, **para** corrigir o automóvel ou o período solicitado.

**Critérios de aceitação:**
- [x] Só é permitido modificar pedidos com status `EM_ANALISE`.
- [x] As mesmas validações da criação se aplicam.

> **Decisão adotada:** apenas pedidos `EM_ANALISE` podem ser modificados. Implementada; ainda não validada com o professor.

### US12 - Cancelar pedido
**Como** cliente, **quero** cancelar um pedido, **para** desistir do aluguel.

**Critérios de aceitação:**
- [x] Só é permitido cancelar pedidos com status `EM_ANALISE` ou `APROVADO`.
- [x] O sistema solicita confirmação antes de cancelar.
- [x] O pedido passa para o status `CANCELADO` (não é removido do banco).

> **Decisão adotada:** o cancelamento é permitido em `EM_ANALISE` e `APROVADO`. Implementada; ainda não validada com o professor.

---

## Agente (Empresa / Banco)

### US13 - Listar pedidos para análise
**Como** agente, **quero** ver os pedidos aguardando análise, **para** avaliá-los.

**Critérios de aceitação:**
- [x] Lista os pedidos com status `EM_ANALISE`. *(MVP)*
- [x] Permite visualizar também os pedidos já avaliados (filtro por status).

### US14 - Consultar dados financeiros do cliente
**Como** agente, **quero** visualizar os dados do cliente de um pedido, **para** fundamentar meu parecer financeiro.

**Critérios de aceitação:**
- [x] Exibe profissão, entidades empregadoras e rendimentos do cliente.
- [x] Exibe o total dos rendimentos.

### US15 - Modificar pedido
**Como** agente, **quero** modificar um pedido, **para** ajustar condições antes da avaliação.

**Critérios de aceitação:**
- [x] Só é permitido modificar pedidos com status `EM_ANALISE`.
- [x] As mesmas validações da criação se aplicam.

### US16 - Avaliar pedido
**Como** agente, **quero** emitir um parecer financeiro sobre um pedido, **para** aprová-lo ou reprová-lo.

**Critérios de aceitação:**
- [x] O parecer é positivo (status `APROVADO`) ou negativo (status `REPROVADO`). *(MVP)*
- [ ] O agente pode registrar uma justificativa.
- [x] O sistema registra qual agente avaliou o pedido. *(MVP)*
- [x] Só é possível avaliar pedidos com status `EM_ANALISE`. *(MVP)*
- [x] A aprovação só é aceita se o automóvel estiver livre no período, já que pedidos aprovados ou contratados reservam o carro.

> **Fora do escopo:** a justificativa do parecer não foi implementada nesta versão.

### US17 - Executar contrato
**Como** agente, **quero** executar o contrato de um pedido aprovado, **para** formalizar o aluguel.

**Critérios de aceitação:**
- [x] Só é possível para pedidos com status `APROVADO`.
- [x] O agente define o proprietário do automóvel conforme o tipo de contrato: **Cliente**, **Empresa** ou **Banco**.
- [x] O contrato registra o proprietário concreto: o cliente do pedido ou o próprio agente que executa o contrato.
- [x] O contrato registra a data de execução.
- [x] O pedido passa para o status `CONTRATADO`.

---

## Banco

### US18 - Conceder contrato de crédito
**Como** banco agente, **quero** associar um contrato de crédito ao aluguel, **para** financiar o cliente.

**Critérios de aceitação:**
- [x] Disponível apenas para agentes do tipo **Banco**.
- [x] O contrato de crédito registra valor, taxa de juros e prazo.
- [x] O contrato de crédito é opcional e fica associado ao contrato de aluguel.

---

## Automóveis

> **Decisão adotada:** os agentes cadastram os automóveis, e "matrícula" é o RENAVAM (11 dígitos). No MVP, a frota era inserida via seed; o seed agora só popula a frota inicial quando a tabela está vazia.

### US19 - Cadastrar automóvel
**Como** agente, **quero** cadastrar automóveis, **para** que fiquem disponíveis para pedidos de aluguel.

**Critérios de aceitação:**
- [x] O formulário solicita matrícula, ano, marca, modelo e placa.
- [x] Matrícula e placa devem ser únicas.
- [x] O ano não pode ser posterior ao ano atual + 1.

### US20 - Gerenciar automóveis
**Como** agente, **quero** consultar, editar e remover automóveis, **para** manter a frota atualizada.

**Critérios de aceitação:**
- [x] Lista os automóveis cadastrados.
- [x] Não é permitido remover automóvel vinculado a pedidos.

> **Decisão adotada:** a remoção é bloqueada para qualquer pedido, e não só para os ativos, para preservar o histórico de pedidos e contratos.
