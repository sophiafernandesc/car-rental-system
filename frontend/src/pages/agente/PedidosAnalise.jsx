import { useEffect, useState } from 'react'
import Alerta from '../../components/Alerta.jsx'
import StatusBadge from '../../components/StatusBadge.jsx'
import { pedidoService } from '../../services/pedidoService'
import { PROPRIETARIOS, formatarData, formatarDataHora, formatarMoeda } from '../../utils/formatadores'

const ABAS = [
  { status: 'EM_ANALISE', rotulo: 'Em análise' },
  { status: 'APROVADO', rotulo: 'Aprovados' },
  { status: 'CONTRATADO', rotulo: 'Contratados' },
  { status: 'REPROVADO', rotulo: 'Reprovados' },
]

// US13, US16 e US17
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

  // Executa uma ação sobre o pedido; em caso de sucesso ele sai da aba atual
  async function executar(pedido, acao, textoSucesso) {
    setErro(null)
    setMensagem(null)
    setProcessandoId(pedido.id)
    try {
      await acao()
      setResultado({ status, pedidos: pedidos.filter((p) => p.id !== pedido.id) })
      setMensagem(textoSucesso)
    } catch (e) {
      setErro(e.message)
    } finally {
      setProcessandoId(null)
    }
  }

  function avaliar(pedido, aprovar) {
    return executar(
      pedido,
      () => (aprovar ? pedidoService.aprovar(pedido.id) : pedidoService.reprovar(pedido.id)),
      `Pedido #${pedido.id} de ${pedido.clienteNome} ${aprovar ? 'aprovado' : 'reprovado'}.`,
    )
  }

  function executarContrato(pedido, tipoProprietario) {
    return executar(
      pedido,
      () => pedidoService.executarContrato(pedido.id, tipoProprietario),
      `Contrato do pedido #${pedido.id} executado. Proprietário do automóvel: ${PROPRIETARIOS[tipoProprietario]}.`,
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
                  {pedido.proprietario && (
                    <span>
                      Proprietário: <strong>{PROPRIETARIOS[pedido.proprietario]}</strong>
                    </span>
                  )}
                </div>
              </div>

              <div className="pedido-lateral">
                <div className="pedido-renda">
                  <span>Renda declarada</span>
                  <strong>{formatarMoeda(pedido.clienteRendaTotal)}</strong>
                </div>
                {pedido.status === 'EM_ANALISE' && (
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
                )}
                {pedido.status === 'APROVADO' && (
                  <ExecutarContrato
                    processando={processandoId === pedido.id}
                    onConfirmar={(tipo) => executarContrato(pedido, tipo)}
                  />
                )}
                {pedido.status !== 'EM_ANALISE' && pedido.status !== 'APROVADO' && <StatusBadge status={pedido.status} />}
              </div>
            </li>
          ))}
        </ul>
      )}
    </>
  )
}

// US17: o tipo de contrato define o proprietário do automóvel
function ExecutarContrato({ processando, onConfirmar }) {
  const [aberto, setAberto] = useState(false)
  const [tipo, setTipo] = useState(null)

  if (!aberto) {
    return (
      <button type="button" className="btn btn-primario btn-sm" onClick={() => setAberto(true)}>
        Executar contrato
      </button>
    )
  }

  return (
    <div className="contrato-opcoes" role="group" aria-label="Proprietário do automóvel">
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
      <div className="acoes">
        <button type="button" className="btn btn-primario btn-sm" onClick={() => onConfirmar(tipo)} disabled={!tipo || processando}>
          {processando ? 'Executando…' : 'Confirmar contrato'}
        </button>
        <button type="button" className="btn btn-vidro btn-sm" onClick={() => setAberto(false)}>
          Voltar
        </button>
      </div>
    </div>
  )
}
