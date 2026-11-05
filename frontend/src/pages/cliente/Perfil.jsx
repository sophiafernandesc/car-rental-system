import { useEffect, useState } from 'react'
import Alerta from '../../components/Alerta.jsx'
import Campo from '../../components/Campo.jsx'
import { useAuth } from '../../contexts/AuthContext.jsx'
import { clienteService } from '../../services/clienteService'
import { formatarCpf, formatarMoeda } from '../../utils/formatadores'

const MAX_RENDIMENTOS = 3

// US05 a US08
export default function Perfil() {
  const { logout } = useAuth()
  const [cliente, setCliente] = useState(null)
  const [erro, setErro] = useState(null)

  useEffect(() => {
    clienteService.buscar().then(setCliente).catch((e) => setErro(e.message))
  }, [])

  return (
    <>
      <header className="pagina-cabecalho">
        <div>
          <h1>Meu perfil</h1>
          <p>Mantenha seus dados e rendimentos atualizados para agilizar a análise dos pedidos.</p>
        </div>
      </header>

      <Alerta className="pagina-alerta">{erro}</Alerta>
      {!cliente && !erro && <p className="carregando">Carregando perfil…</p>}

      {cliente && (
        <>
          <div className="perfil-grid">
            <DadosPessoais cliente={cliente} onAtualizado={setCliente} />
            <Rendimentos cliente={cliente} onAtualizado={setCliente} />
          </div>
          <ExcluirConta onExcluida={logout} />
        </>
      )}
    </>
  )
}

// US05 e US06
function DadosPessoais({ cliente, onAtualizado }) {
  const [form, setForm] = useState({
    nome: cliente.nome,
    endereco: cliente.endereco,
    profissao: cliente.profissao,
    novaSenha: '',
  })
  const [erro, setErro] = useState(null)
  const [erros, setErros] = useState({})
  const [sucesso, setSucesso] = useState(null)
  const [salvando, setSalvando] = useState(false)

  function alterar(evento) {
    setForm({ ...form, [evento.target.name]: evento.target.value })
  }

  async function salvar(evento) {
    evento.preventDefault()
    setErro(null)
    setErros({})
    setSucesso(null)
    setSalvando(true)
    try {
      onAtualizado(await clienteService.atualizar({ ...form, novaSenha: form.novaSenha || null }))
      setForm({ ...form, novaSenha: '' })
      setSucesso('Dados atualizados com sucesso.')
    } catch (e) {
      setErro(e.message)
      setErros(e.erros ?? {})
    } finally {
      setSalvando(false)
    }
  }

  return (
    <section className="painel glass" aria-labelledby="titulo-dados">
      <h2 id="titulo-dados" className="secao-titulo">Dados pessoais</h2>
      <form className="form" onSubmit={salvar}>
        <Alerta tipo="sucesso">{sucesso}</Alerta>
        <Alerta>{erro}</Alerta>

        <div className="form-grid">
          <Campo id="login" label="Login" value={cliente.login} readOnly />
          <Campo id="cpf" label="CPF" value={formatarCpf(cliente.cpf)} readOnly />
          <Campo id="rg" label="RG" value={cliente.rg} readOnly />
        </div>
        <Campo id="nome" label="Nome completo" value={form.nome} onChange={alterar} erro={erros.nome} required />
        <Campo id="endereco" label="Endereço" value={form.endereco} onChange={alterar} erro={erros.endereco} required />
        <Campo id="profissao" label="Profissão" value={form.profissao} onChange={alterar} erro={erros.profissao} required />
        <Campo
          id="novaSenha"
          label="Nova senha"
          type="password"
          value={form.novaSenha}
          onChange={alterar}
          erro={erros.novaSenha}
          ajuda="Deixe em branco para manter a senha atual"
          autoComplete="new-password"
        />

        <div className="acoes">
          <button type="submit" className="btn btn-primario" disabled={salvando}>
            {salvando ? 'Salvando…' : 'Salvar alterações'}
          </button>
        </div>
      </form>
    </section>
  )
}

// US07
function Rendimentos({ cliente, onAtualizado }) {
  const [editandoId, setEditandoId] = useState(null)
  const [erro, setErro] = useState(null)

  async function executar(acao) {
    setErro(null)
    try {
      onAtualizado(await acao())
      return true
    } catch (e) {
      setErro(e.message)
      return false
    }
  }

  async function salvarEdicao(id, dados) {
    const ok = await executar(() => clienteService.atualizarRendimento(id, dados))
    if (ok) setEditandoId(null)
    return ok
  }

  const podeAdicionar = cliente.rendimentos.length < MAX_RENDIMENTOS && editandoId === null

  return (
    <section className="painel glass" aria-labelledby="titulo-rendimentos">
      <h2 id="titulo-rendimentos" className="secao-titulo">
        Rendimentos ({cliente.rendimentos.length}/{MAX_RENDIMENTOS})
      </h2>

      <div className="rendimentos">
        <Alerta>{erro}</Alerta>

        {cliente.rendimentos.length === 0 && <p className="carregando">Nenhum rendimento cadastrado.</p>}

        {cliente.rendimentos.map((r) =>
          editandoId === r.id ? (
            <RendimentoForm
              key={r.id}
              inicial={r}
              onSalvar={(dados) => salvarEdicao(r.id, dados)}
              onCancelar={() => setEditandoId(null)}
            />
          ) : (
            <div key={r.id} className="rendimento-linha">
              <div>
                <strong>{r.entidadeEmpregadora}</strong>
                <span>{formatarMoeda(r.valor)}</span>
              </div>
              <div className="acoes">
                <button type="button" className="btn btn-vidro btn-sm" onClick={() => setEditandoId(r.id)}>
                  Editar
                </button>
                <button
                  type="button"
                  className="btn btn-perigo btn-sm"
                  onClick={() => executar(() => clienteService.removerRendimento(r.id))}
                  aria-label={`Remover rendimento ${r.entidadeEmpregadora}`}
                >
                  Remover
                </button>
              </div>
            </div>
          ),
        )}

        {podeAdicionar && (
          <RendimentoForm rotuloSalvar="Adicionar" onSalvar={(dados) => executar(() => clienteService.adicionarRendimento(dados))} />
        )}

        <div className="rendimento-total">
          <span>Renda total</span>
          <strong>{formatarMoeda(cliente.rendaTotal)}</strong>
        </div>
      </div>
    </section>
  )
}

function RendimentoForm({ inicial, onSalvar, onCancelar, rotuloSalvar = 'Salvar' }) {
  const prefixo = inicial ? `rendimento-${inicial.id}` : 'rendimento-novo'
  const [entidade, setEntidade] = useState(inicial?.entidadeEmpregadora ?? '')
  const [valor, setValor] = useState(inicial?.valor ?? '')
  const [salvando, setSalvando] = useState(false)

  async function salvar(evento) {
    evento.preventDefault()
    setSalvando(true)
    const ok = await onSalvar({ entidadeEmpregadora: entidade, valor: Number(valor) })
    setSalvando(false)
    if (ok && !inicial) {
      setEntidade('')
      setValor('')
    }
  }

  return (
    <form className="rendimento-form" onSubmit={salvar}>
      <Campo id={`${prefixo}-entidade`} label="Entidade empregadora" value={entidade} onChange={(e) => setEntidade(e.target.value)} required />
      <Campo id={`${prefixo}-valor`} label="Valor (R$)" type="number" min="0.01" step="0.01" value={valor} onChange={(e) => setValor(e.target.value)} required />
      <div className="acoes">
        <button type="submit" className="btn btn-vidro btn-sm" disabled={salvando}>
          {rotuloSalvar}
        </button>
        {onCancelar && (
          <button type="button" className="btn btn-vidro btn-sm" onClick={onCancelar}>
            Cancelar
          </button>
        )}
      </div>
    </form>
  )
}

// US08
function ExcluirConta({ onExcluida }) {
  const [confirmando, setConfirmando] = useState(false)
  const [erro, setErro] = useState(null)
  const [excluindo, setExcluindo] = useState(false)

  async function excluir() {
    setErro(null)
    setExcluindo(true)
    try {
      await clienteService.excluir()
      onExcluida()
    } catch (e) {
      setErro(e.message)
      setExcluindo(false)
    }
  }

  return (
    <section className="painel glass zona-perigo" aria-labelledby="titulo-excluir">
      <h2 id="titulo-excluir" className="secao-titulo" style={{ color: 'var(--erro)' }}>Excluir conta</h2>
      <p>
        {confirmando
          ? 'Tem certeza? Seus dados e pedidos serão removidos permanentemente.'
          : 'Não é possível excluir a conta com pedidos aprovados ou contratados.'}
      </p>
      <Alerta className="pagina-alerta">{erro}</Alerta>
      {confirmando ? (
        <div className="acoes">
          <button type="button" className="btn btn-perigo" onClick={excluir} disabled={excluindo}>
            {excluindo ? 'Excluindo…' : 'Sim, excluir minha conta'}
          </button>
          <button type="button" className="btn btn-vidro" onClick={() => setConfirmando(false)}>
            Cancelar
          </button>
        </div>
      ) : (
        <button type="button" className="btn btn-perigo" onClick={() => setConfirmando(true)}>
          Excluir minha conta
        </button>
      )}
    </section>
  )
}
