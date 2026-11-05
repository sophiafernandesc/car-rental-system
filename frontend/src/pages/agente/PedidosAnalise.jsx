import { useEffect, useState } from 'react'
import Alerta from '../../components/Alerta.jsx'
import StatusBadge from '../../components/StatusBadge.jsx'
import { pedidoService } from '../../services/pedidoService'
import { formatarData, formatarDataHora, formatarMoeda } from '../../utils/formatadores'

const ABAS = [
  { status: 'EM_ANALISE', rotulo: 'Em análise' },
  { status: 'APROVADO', rotulo: 'Aprovados' },
  { status: 'REPROVADO', rotulo: 'Reprovados' },
]

// US13 e US16
export default function PedidosAnalise() {
  const [status, setStatus] = useState('EM_ANALISE')
  const [resultado, setResultado] = useState({ status: null, pedidos: [] })
  const [erro, setErro] = useState(null)
  const [mensagem, setMensagem] = useState(null)
  const [processandoId, setProcessandoId] = useState(null)

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

  async function avaliar(pedido, aprovar) {
    setErro(null)
    setMensagem(null)
    setProcessandoId(pedido.id)
    try {
      await (aprovar ? pedidoService.aprovar(pedido.id) : pedidoService.reprovar(pedido.id))
      setResultado({ status, pedidos: pedidos.filter((p) => p.id !== pedido.id) })
      setMensagem(`Pedido #${pedido.id} de ${pedido.clienteNome} ${aprovar ? 'aprovado' : 'reprovado'}.`)
    } catch (e) {
      setErro(e.message)
    } finally {
      setProcessandoId(null)
    }
  }

  return (
    <>
      <header className="pagina-cabecalho">
        <div>
          <h1>Avaliação de pedidos</h1>
          <p>Analise a situação financeira do cliente e emita seu parecer.</p>
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
        <ul className="lista" style={{ listStyle: 'none', padding: 0, margin: 0 }}>
          {pedidos.map((pedido) => (
            <li key={pedido.id} className="pedido-card glass">
              <div>
                <div className="pedido-carro">{pedido.clienteNome}</div>
                <div className="pedido-meta">
                  <span>
                    Automóvel: <strong>{pedido.automovel.marca} {pedido.automovel.modelo}</strong> ({pedido.automovel.placa})
                  </span>
                  <span>
                    Período: <strong>{formatarData(pedido.dataInicio)} a {formatarData(pedido.dataFim)}</strong>
                  </span>
                  <span>Pedido #{pedido.id} · criado em {formatarDataHora(pedido.dataCriacao)}</span>
                  {pedido.avaliador && <span>Avaliado por {pedido.avaliador}</span>}
                </div>
              </div>

              <div className="pedido-lateral">
                <div className="pedido-renda">
                  <span>Renda declarada</span>
                  <strong>{formatarMoeda(pedido.clienteRendaTotal)}</strong>
                </div>
                {pedido.status === 'EM_ANALISE' ? (
                  <div className="acoes">
                    <button
                      type="button"
                      className="btn btn-sucesso btn-sm"
                      onClick={() => avaliar(pedido, true)}
                      disabled={processandoId === pedido.id}
                    >
                      Aprovar
                    </button>
                    <button
                      type="button"
                      className="btn btn-perigo btn-sm"
                      onClick={() => avaliar(pedido, false)}
                      disabled={processandoId === pedido.id}
                    >
                      Reprovar
                    </button>
                  </div>
                ) : (
                  <StatusBadge status={pedido.status} />
                )}
              </div>
            </li>
          ))}
        </ul>
      )}
    </>
  )
}
