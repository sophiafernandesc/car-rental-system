import { useEffect, useState } from 'react'
import Alerta from '../../components/Alerta.jsx'
import Campo from '../../components/Campo.jsx'
import Modal from '../../components/Modal.jsx'
import PedidoCard from '../../components/PedidoCard.jsx'
import PedidoDetalhe from '../../components/PedidoDetalhe.jsx'
import StatusBadge from '../../components/StatusBadge.jsx'
import { useAuth } from '../../contexts/AuthContext.jsx'
import { pedidoService } from '../../services/pedidoService'
import { PROPRIETARIOS, formatarData, formatarMoeda } from '../../utils/formatadores'

const ABAS = [
  { status: 'EM_ANALISE', rotulo: 'Em análise' },
  { status: 'APROVADO', rotulo: 'Aprovados' },
  { status: 'CONTRATADO', rotulo: 'Contratados' },
  { status: 'REPROVADO', rotulo: 'Reprovados' },
]

// US13, US14, US16, US17 e US18
export default function PedidosAnalise() {
  const [status, setStatus] = useState('EM_ANALISE')
  const [resultado, setResultado] = useState({ status: null, pedidos: [] })
  const [erro, setErro] = useState(null)
  const [mensagem, setMensagem] = useState(null)
  const [selecionado, setSelecionado] = useState(null)
  const [erroAcao, setErroAcao] = useState(null)
  const [processando, setProcessando] = useState(false)

  useEffect(() => {
    let ativo = true
    pedidoService
      .listarPorStatus(status)
      .then((pedidos) => ativo && setResultado({ status, pedidos }))
      .catch((e) => ativo && setErro(e.message))
    return () => {
      ativo = false
    }
  }, [status])

  const carregando = resultado.status !== status && !erro
  const pedidos = resultado.status === status ? resultado.pedidos : []

  function trocarAba(novoStatus) {
    setErro(null)
    setMensagem(null)
    setStatus(novoStatus)
  }

  function abrir(pedido) {
    setErroAcao(null)
    setSelecionado(pedido)
  }

  // Em caso de sucesso, o pedido sai da aba atual e o detalhe é fechado
  async function executar(pedido, acao, textoSucesso) {
    setErroAcao(null)
    setProcessando(true)
    try {
      await acao()
      setResultado({ status, pedidos: pedidos.filter((p) => p.id !== pedido.id) })
      setSelecionado(null)
      setMensagem(textoSucesso)
    } catch (e) {
      setErroAcao(e.message)
    } finally {
      setProcessando(false)
    }
  }

  function avaliar(pedido, aprovar) {
    return executar(
      pedido,
      () => (aprovar ? pedidoService.aprovar(pedido.id) : pedidoService.reprovar(pedido.id)),
      `Pedido #${pedido.id} de ${pedido.clienteNome} ${aprovar ? 'aprovado' : 'reprovado'}.`,
    )
  }

  function executarContrato(pedido, tipoProprietario, credito) {
    return executar(
      pedido,
      () => pedidoService.executarContrato(pedido.id, tipoProprietario, credito),
      `Contrato do pedido #${pedido.id} executado${credito ? ' com crédito concedido' : ''}. ` +
        `Proprietário do automóvel: ${PROPRIETARIOS[tipoProprietario]}.`,
    )
  }

  return (
    <>
      <header className="pagina-cabecalho">
        <div>
          <h1>Avaliação de pedidos</h1>
          <p>Analise a situação financeira do cliente, emita seu parecer e execute os contratos aprovados.</p>
        </div>
      </header>

      <div className="abas glass" role="tablist" aria-label="Filtrar pedidos por status">
        {ABAS.map((aba) => (
          <button
            key={aba.status}
            type="button"
            role="tab"
            className="aba"
            aria-selected={status === aba.status}
            onClick={() => trocarAba(aba.status)}
          >
            {aba.rotulo}
          </button>
        ))}
      </div>

      <Alerta tipo="sucesso" className="pagina-alerta">{mensagem}</Alerta>
      <Alerta className="pagina-alerta">{erro}</Alerta>

      {carregando && <p className="carregando">Carregando pedidos…</p>}

      {!carregando && pedidos.length === 0 && (
        <div className="vazio glass">
          <p>Nenhum pedido nesta situação.</p>
        </div>
      )}

      {!carregando && pedidos.length > 0 && (
        <ul className="lista">
          {pedidos.map((pedido) => (
            <PedidoCard
              key={pedido.id}
              titulo={pedido.clienteNome}
              onAbrir={() => abrir(pedido)}
              lateral={
                <>
                  <div className="pedido-renda">
                    <span>Renda declarada</span>
                    <strong>{formatarMoeda(pedido.clienteRendaTotal)}</strong>
                  </div>
                  <StatusBadge status={pedido.status} />
                </>
              }
            >
              <span>
                Automóvel: <strong>{pedido.automovel.marca} {pedido.automovel.modelo}</strong>
              </span>
              <span>
                Período: <strong>{formatarData(pedido.dataInicio)} a {formatarData(pedido.dataFim)}</strong>
              </span>
              <span>Pedido #{pedido.id}</span>
            </PedidoCard>
          ))}
        </ul>
      )}

      {selecionado && (
        <Modal titulo={`Pedido #${selecionado.id} · ${selecionado.clienteNome}`} onFechar={() => setSelecionado(null)}>
          <DetalheAgente pedido={selecionado} />
          {(selecionado.status === 'EM_ANALISE' || selecionado.status === 'APROVADO') && (
            <footer className="modal-acoes">
              <Alerta>{erroAcao}</Alerta>
              {selecionado.status === 'EM_ANALISE' && (
                <div className="acoes">
                  <button type="button" className="btn btn-sucesso" onClick={() => avaliar(selecionado, true)} disabled={processando}>
                    Aprovar pedido
                  </button>
                  <button type="button" className="btn btn-perigo" onClick={() => avaliar(selecionado, false)} disabled={processando}>
                    Reprovar pedido
                  </button>
                </div>
              )}
              {selecionado.status === 'APROVADO' && (
                <ExecutarContrato
                  processando={processando}
                  onConfirmar={(tipo, credito) => executarContrato(selecionado, tipo, credito)}
                />
              )}
            </footer>
          )}
        </Modal>
      )}
    </>
  )
}

// US14: dados financeiros do cliente para fundamentar o parecer
function DetalheAgente({ pedido }) {
  const [detalhe, setDetalhe] = useState(null)
  const [erro, setErro] = useState(null)

  useEffect(() => {
    pedidoService.detalharParaAgente(pedido.id).then(setDetalhe).catch((e) => setErro(e.message))
  }, [pedido.id])

  const cliente = detalhe?.cliente

  return (
    <>
      <PedidoDetalhe pedido={detalhe?.pedido ?? pedido} />

      <section className="detalhe-financeiro">
        <h3 className="secao-titulo">Dados financeiros do cliente</h3>
        <Alerta>{erro}</Alerta>
        {!cliente && !erro && <p className="carregando">Carregando…</p>}
        {cliente && (
          <>
            <p className="detalhe-texto">
              <strong>{cliente.nome}</strong> · {cliente.profissao}
            </p>
            {cliente.rendimentos.length === 0 ? (
              <p className="carregando">Nenhum rendimento declarado.</p>
            ) : (
              <ul className="rendimentos-lista">
                {cliente.rendimentos.map((r) => (
                  <li key={r.id}>
                    <span>{r.entidadeEmpregadora}</span>
                    <strong>{formatarMoeda(r.valor)}</strong>
                  </li>
                ))}
              </ul>
            )}
            <div className="rendimento-total">
              <span>Renda total</span>
              <strong>{formatarMoeda(cliente.rendaTotal)}</strong>
            </div>
          </>
        )}
      </section>
    </>
  )
}

// US17: o tipo de contrato define o proprietário do automóvel
// US18: agentes do tipo Banco podem conceder um contrato de crédito junto
function ExecutarContrato({ processando, onConfirmar }) {
  const { sessao } = useAuth()
  const ehBanco = sessao.tipoAgente === 'BANCO'
  const [tipo, setTipo] = useState(null)
  const [comCredito, setComCredito] = useState(false)
  const [credito, setCredito] = useState({ valor: '', taxaJuros: '', prazoMeses: '' })

  function alterarCredito(evento) {
    setCredito({ ...credito, [evento.target.name]: evento.target.value })
  }

  function confirmar(evento) {
    evento.preventDefault()
    const dadosCredito = comCredito
      ? { valor: Number(credito.valor), taxaJuros: Number(credito.taxaJuros), prazoMeses: Number(credito.prazoMeses) }
      : null
    onConfirmar(tipo, dadosCredito)
  }

  return (
    <form className="contrato-opcoes" onSubmit={confirmar} aria-label="Executar contrato">
      <span className="contrato-rotulo">Proprietário do automóvel</span>
      <div className="acoes">
        {Object.entries(PROPRIETARIOS).map(([valor, rotulo]) => (
          <button
            key={valor}
            type="button"
            className="btn btn-vidro btn-sm"
            aria-pressed={tipo === valor}
            onClick={() => setTipo(valor)}
          >
            {rotulo}
          </button>
        ))}
      </div>

      {ehBanco && (
        <label className="checkbox">
          <input type="checkbox" checked={comCredito} onChange={(e) => setComCredito(e.target.checked)} />
          Conceder contrato de crédito
        </label>
      )}
      {ehBanco && comCredito && (
        <div className="credito-campos">
          <Campo id="valor" label="Valor (R$)" type="number" min="0.01" step="0.01" value={credito.valor} onChange={alterarCredito} required />
          <Campo id="taxaJuros" label="Juros (% a.m.)" type="number" min="0" max="100" step="0.01" value={credito.taxaJuros} onChange={alterarCredito} required />
          <Campo id="prazoMeses" label="Prazo (meses)" type="number" min="1" max="120" step="1" value={credito.prazoMeses} onChange={alterarCredito} required />
        </div>
      )}

      <div className="acoes">
        <button type="submit" className="btn btn-primario" disabled={!tipo || processando}>
          {processando ? 'Executando…' : 'Executar contrato'}
        </button>
      </div>
    </form>
  )
}
