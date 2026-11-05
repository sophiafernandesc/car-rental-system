# Histórias de Usuário

## Visão geral

| ID | História | Ator | Entrega | Escopo |
|---|---|---|---|---|
| US01 | Cadastrar-se como cliente | Visitante | Autenticação | MVP |
| US02 | Cadastrar-se como agente | Visitante | Autenticação | Seed |
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
| US19 | Cadastrar automóvel | Agente | Sistema completo | Seed |
| US20 | Gerenciar automóveis | Agente | Sistema completo | Futuro |

**Escopo:**
- **MVP** - implementado com back-end e tela.
- **MVP (simplificado)** - implementado apenas com os critérios marcados como MVP na história.
- **Pós-MVP** - implementado com back-end e tela após a entrega do MVP.
- **Seed** - dados inseridos diretamente no banco (`data.sql`), sem tela de cadastro.
- **Futuro** - mantido na modelagem (entidades existem no código), sem funcionalidade implementada nesta versão.

---

##  Autenticação

### US01 - Cadastrar-se como cliente
**Como** visitante, **quero** me cadastrar como cliente, **para** poder solicitar aluguéis de automóveis.

**Critérios de aceitação:**
- [ ] O formulário solicita: login, senha, RG, CPF, nome, endereço e profissão.
- [ ] Permite informar até **3** rendimentos (entidade empregadora + valor).
- [ ] CPF, RG e login devem ser únicos no sistema.
- [ ] CPF deve ter formato válido.
- [ ] A senha é armazenada criptografada (hash).
- [ ] Após o cadastro, o usuário é direcionado para a tela de login.

### US02 - Cadastrar-se como agente
**Como** visitante representando uma empresa ou banco, **quero** me cadastrar como agente, **para** poder avaliar pedidos de aluguel.

**Critérios de aceitação:**
- [ ] O formulário solicita: login, senha, CNPJ, razão social e tipo (Empresa ou Banco).
- [ ] CNPJ e login devem ser únicos no sistema.
- [ ] A senha é armazenada criptografada (hash).

>  **Pendente:** autocadastro ou cadastro feito por um administrador? 
>
> **MVP:** agentes (Empresa e Banco) são inseridos via seed.

### US03 - Fazer login
**Como** usuário cadastrado, **quero** entrar no sistema com login e senha, **para** acessar as funcionalidades do meu perfil.

**Critérios de aceitação:**
- [ ] Credenciais inválidas exibem mensagem de erro, sem indicar qual campo está incorreto.
- [ ] Cliente é redirecionado ao Dashboard do Cliente; agente, ao Dashboard do Agente.
- [ ] Nenhuma página interna (front-end) ou endpoint protegido (back-end) é acessível sem autenticação.
- [ ] Um usuário não acessa funcionalidades de outro perfil (ex.: cliente não avalia pedidos).

### US04 - Fazer logout
**Como** usuário autenticado, **quero** sair do sistema, **para** encerrar minha sessão com segurança.

**Critérios de aceitação:**
- [ ] A sessão/token é descartado no cliente.
- [ ] Após o logout, o usuário é redirecionado à tela de login e não acessa páginas internas.

---

## Cliente - Perfil (CRUD)

### US05 - Consultar meus dados
**Como** cliente, **quero** visualizar meus dados cadastrais, **para** conferir se estão corretos.

**Critérios de aceitação:**
- [ ] Exibe dados pessoais, profissão e rendimentos cadastrados.
- [ ] O cliente só visualiza os **próprios** dados.

### US06 - Atualizar meus dados pessoais
**Como** cliente, **quero** editar meus dados pessoais, **para** mantê-los atualizados.

**Critérios de aceitação:**
- [ ] Permite editar nome, endereço, profissão e senha.
- [ ] As mesmas validações do cadastro se aplicam (unicidade, formato de CPF).
- [ ] Exibe confirmação após salvar.

### US07 - Gerenciar meus rendimentos
**Como** cliente, **quero** adicionar, editar e remover meus rendimentos, **para** que os agentes avaliem corretamente minha capacidade financeira.

**Critérios de aceitação:**
- [ ] Cada rendimento possui entidade empregadora e valor (maior que zero).
- [ ] O sistema impede cadastrar mais de **3** rendimentos (validação no front-end **e** no back-end).

### US08 - Excluir minha conta
**Como** cliente, **quero** excluir minha conta, **para** deixar de utilizar o sistema.

**Critérios de aceitação:**
- [ ] O sistema solicita confirmação antes de excluir.
- [ ] Não é permitido excluir a conta com pedido `APROVADO` ou `CONTRATADO`.
- [ ] Após a exclusão, a sessão é encerrada.

---

## Cliente - Pedidos de Aluguel

### US09 - Criar pedido de aluguel
**Como** cliente, **quero** criar um pedido de aluguel, **para** solicitar um automóvel.

**Critérios de aceitação:**
- [ ] O cliente seleciona um automóvel disponível e informa o período (data de início e fim).
- [ ] A data de início não pode ser anterior à data atual, e a data de fim deve ser posterior à de início.
- [ ] O pedido é criado com status `EM_ANALISE` e registra a data de criação.

### US10 - Consultar meus pedidos e status
**Como** cliente, **quero** consultar meus pedidos e o status de cada um, **para** acompanhar o andamento das solicitações.

**Critérios de aceitação:**
- [ ] Lista apenas os pedidos do cliente autenticado.
- [ ] Exibe automóvel, período, status e data de criação.
- [ ] O detalhe do pedido exibe o parecer do agente, quando houver.

### US11 - Modificar pedido
**Como** cliente, **quero** modificar um pedido, **para** corrigir o automóvel ou o período solicitado.

**Critérios de aceitação:**
- [ ] Só é permitido modificar pedidos com status `EM_ANALISE`.
- [ ] As mesmas validações da criação se aplicam.

> **Decisão adotada:** apenas pedidos `EM_ANALISE` podem ser modificados (a confirmar com o professor).

### US12 - Cancelar pedido
**Como** cliente, **quero** cancelar um pedido, **para** desistir do aluguel.

**Critérios de aceitação:**
- [ ] Só é permitido cancelar pedidos com status `EM_ANALISE` ou `APROVADO`.
- [ ] O sistema solicita confirmação antes de cancelar.
- [ ] O pedido passa para o status `CANCELADO` (não é removido do banco).

> **Decisão adotada:** o cancelamento é permitido em `EM_ANALISE` e `APROVADO` (a confirmar com o professor).

---

## Agente (Empresa / Banco)

### US13 - Listar pedidos para análise
**Como** agente, **quero** ver os pedidos aguardando análise, **para** avaliá-los.

**Critérios de aceitação:**
- [ ] Lista os pedidos com status `EM_ANALISE`. *(MVP)*
- [ ] Permite visualizar também os pedidos já avaliados (filtro por status).

### US14 - Consultar dados financeiros do cliente
**Como** agente, **quero** visualizar os dados do cliente de um pedido, **para** fundamentar meu parecer financeiro.

**Critérios de aceitação:**
- [ ] Exibe profissão, entidades empregadoras e rendimentos do cliente.
- [ ] Exibe o total dos rendimentos.

### US15 - Modificar pedido
**Como** agente, **quero** modificar um pedido, **para** ajustar condições antes da avaliação.

**Critérios de aceitação:**
- [ ] Só é permitido modificar pedidos com status `EM_ANALISE`.
- [ ] As mesmas validações da criação se aplicam.

### US16 - Avaliar pedido
**Como** agente, **quero** emitir um parecer financeiro sobre um pedido, **para** aprová-lo ou reprová-lo.

**Critérios de aceitação:**
- [ ] O parecer é positivo (status `APROVADO`) ou negativo (status `REPROVADO`). *(MVP)*
- [ ] O agente pode registrar uma justificativa.
- [ ] O sistema registra qual agente avaliou o pedido. *(MVP)*
- [ ] Só é possível avaliar pedidos com status `EM_ANALISE`. *(MVP)*

### US17 - Executar contrato
**Como** agente, **quero** executar o contrato de um pedido aprovado, **para** formalizar o aluguel.

**Critérios de aceitação:**
- [ ] Só é possível para pedidos com status `APROVADO`.
- [ ] O agente define o proprietário do automóvel conforme o tipo de contrato: **Cliente**, **Empresa** ou **Banco**.
- [ ] O contrato registra a data de execução.
- [ ] O pedido passa para o status `CONTRATADO`.

---

## Banco

### US18 - Conceder contrato de crédito
**Como** banco agente, **quero** associar um contrato de crédito ao aluguel, **para** financiar o cliente.

**Critérios de aceitação:**
- [ ] Disponível apenas para agentes do tipo **Banco**.
- [ ] O contrato de crédito registra valor, taxa de juros e prazo.
- [ ] O contrato de crédito é opcional e fica associado ao contrato de aluguel.

---

## Automóveis

> **Pendente:** confirmar quem cadastra os automóveis (proposta atual: agentes) e o significado de "matrícula" (proposta: RENAVAM).
>
> **MVP:** automóveis são inseridos via seed.

### US19 - Cadastrar automóvel
**Como** agente, **quero** cadastrar automóveis, **para** que fiquem disponíveis para pedidos de aluguel.

**Critérios de aceitação:**
- [ ] O formulário solicita matrícula, ano, marca, modelo e placa.
- [ ] Matrícula e placa devem ser únicas.
- [ ] O ano não pode ser posterior ao ano atual + 1.

### US20 - Gerenciar automóveis
**Como** agente, **quero** consultar, editar e remover automóveis, **para** manter a frota atualizada.

**Critérios de aceitação:**
- [ ] Lista os automóveis cadastrados.
- [ ] Não é permitido remover automóvel vinculado a pedido `EM_ANALISE`, `APROVADO` ou `CONTRATADO`.
