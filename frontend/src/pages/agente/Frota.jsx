import { useEffect, useState } from 'react'
import Alerta from '../../components/Alerta.jsx'
import Campo from '../../components/Campo.jsx'
import Modal from '../../components/Modal.jsx'
import { automovelService } from '../../services/automovelService'

const ANO_MAXIMO = new Date().getFullYear() + 1

// US19 e US20
export default function Frota() {
  const [automoveis, setAutomoveis] = useState(null)
  const [erro, setErro] = useState(null)
  const [mensagem, setMensagem] = useState(null)
  // null: modal fechado; {}: novo automóvel; automóvel: edição
  const [selecionado, setSelecionado] = useState(null)

  useEffect(() => {
    automovelService.listar().then(setAutomoveis).catch((e) => setErro(e.message))
  }, [])

  function abrir(automovel) {
    setMensagem(null)
    setSelecionado(automovel)
  }

  function ordenar(lista) {
    return [...lista].sort((a, b) => `${a.marca} ${a.modelo}`.localeCompare(`${b.marca} ${b.modelo}`))
  }

  function salvo(automovel, novo) {
    setAutomoveis(ordenar(novo ? [...automoveis, automovel] : automoveis.map((a) => (a.id === automovel.id ? automovel : a))))
    setSelecionado(null)
    setMensagem(`${automovel.marca} ${automovel.modelo} (${automovel.placa}) ${novo ? 'cadastrado' : 'atualizado'}.`)
  }

  function removido(automovel) {
    setAutomoveis(automoveis.filter((a) => a.id !== automovel.id))
    setSelecionado(null)
    setMensagem(`${automovel.marca} ${automovel.modelo} (${automovel.placa}) removido da frota.`)
  }

  return (
    <>
      <header className="pagina-cabecalho">
        <div>
          <h1>Frota</h1>
          <p>Cadastre e mantenha os automóveis disponíveis para aluguel.</p>
        </div>
        <button type="button" className="btn btn-primario" onClick={() => abrir({})}>
          + Novo automóvel
        </button>
      </header>

      <Alerta tipo="sucesso" className="pagina-alerta">{mensagem}</Alerta>
      <Alerta className="pagina-alerta">{erro}</Alerta>

      {!automoveis && !erro && <p className="carregando">Carregando automóveis…</p>}

      {automoveis?.length === 0 && (
        <div className="vazio glass">
          <p>Nenhum automóvel cadastrado.</p>
        </div>
      )}

      {automoveis?.length > 0 && (
        <ul className="lista">
          {automoveis.map((automovel) => (
            <li key={automovel.id}>
              <button type="button" className="pedido-card glass" onClick={() => abrir(automovel)}>
                <div>
                  <div className="pedido-carro">{automovel.marca} {automovel.modelo}</div>
                  <div className="pedido-meta">
                    <span>Ano: <strong>{automovel.ano}</strong></span>
                    <span>Placa: <strong>{automovel.placa}</strong></span>
                    <span>Matrícula: {automovel.matricula}</span>
                  </div>
                </div>
                <div className="pedido-lateral">
                  <span className="pedido-ver">Editar ›</span>
                </div>
              </button>
            </li>
          ))}
        </ul>
      )}

      {selecionado && (
        <Modal
          titulo={selecionado.id ? `${selecionado.marca} ${selecionado.modelo}` : 'Novo automóvel'}
          onFechar={() => setSelecionado(null)}
        >
          <AutomovelForm
            automovel={selecionado}
            onSalvo={salvo}
            onRemovido={removido}
            onCancelar={() => setSelecionado(null)}
          />
        </Modal>
      )}
    </>
  )
}

function AutomovelForm({ automovel, onSalvo, onRemovido, onCancelar }) {
  const editando = Boolean(automovel.id)
  const [form, setForm] = useState({
    matricula: automovel.matricula ?? '',
    ano: automovel.ano ?? '',
    marca: automovel.marca ?? '',
    modelo: automovel.modelo ?? '',
    placa: automovel.placa ?? '',
  })
  const [erro, setErro] = useState(null)
  const [erros, setErros] = useState({})
  const [processando, setProcessando] = useState(false)
  const [confirmandoRemocao, setConfirmandoRemocao] = useState(false)

  function alterar(evento) {
    setForm({ ...form, [evento.target.name]: evento.target.value })
  }

  async function executar(acao) {
    setErro(null)
    setErros({})
    setProcessando(true)
    try {
      await acao()
    } catch (e) {
      setErro(e.message)
      setErros(e.erros ?? {})
      setProcessando(false)
    }
  }

  function salvar(evento) {
    evento.preventDefault()
    const dados = { ...form, ano: Number(form.ano), placa: form.placa.toUpperCase() }
    executar(async () => {
      const salvo = editando
        ? await automovelService.atualizar(automovel.id, dados)
        : await automovelService.cadastrar(dados)
      onSalvo(salvo, !editando)
    })
  }

  function remover() {
    executar(async () => {
      await automovelService.remover(automovel.id)
      onRemovido(automovel)
    })
  }

  return (
    <form className="form" onSubmit={salvar} noValidate aria-label={editando ? 'Editar automóvel' : 'Cadastrar automóvel'}>
      <Alerta>{erro}</Alerta>
      <div className="form-grid">
        <Campo id="marca" label="Marca" value={form.marca} onChange={alterar} erro={erros.marca} required />
        <Campo id="modelo" label="Modelo" value={form.modelo} onChange={alterar} erro={erros.modelo} required />
        <Campo id="ano" label="Ano" type="number" min="1950" max={ANO_MAXIMO} value={form.ano} onChange={alterar} erro={erros.ano} required />
        <Campo id="placa" label="Placa" value={form.placa} onChange={alterar} erro={erros.placa} placeholder="ABC1D23" maxLength={7} required />
      </div>
      <Campo
        id="matricula"
        label="Matrícula (RENAVAM)"
        value={form.matricula}
        onChange={alterar}
        erro={erros.matricula}
        ajuda="11 dígitos"
        inputMode="numeric"
        maxLength={11}
        required
      />

      <footer className="modal-acoes">
        {confirmandoRemocao ? (
          <div className="acoes">
            <span className="modal-pergunta">Remover este automóvel da frota?</span>
            <button type="button" className="btn btn-perigo" onClick={remover} disabled={processando}>
              {processando ? 'Removendo…' : 'Sim, remover'}
            </button>
            <button type="button" className="btn btn-vidro" onClick={() => setConfirmandoRemocao(false)}>
              Voltar
            </button>
          </div>
        ) : (
          <div className="acoes">
            <button type="submit" className="btn btn-primario" disabled={processando}>
              {processando ? 'Salvando…' : editando ? 'Salvar alterações' : 'Cadastrar automóvel'}
            </button>
            <button type="button" className="btn btn-vidro" onClick={onCancelar}>
              Cancelar
            </button>
            {editando && (
              <button type="button" className="btn btn-perigo" onClick={() => setConfirmandoRemocao(true)}>
                Remover
              </button>
            )}
          </div>
        )}
      </footer>
    </form>
  )
}
