import { useEffect, useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import Alerta from '../../components/Alerta.jsx'
import CreditoResumo from '../../components/CreditoResumo.jsx'
import StatusBadge from '../../components/StatusBadge.jsx'
import { pedidoService } from '../../services/pedidoService'
import { PROPRIETARIOS, formatarData, formatarDataHora } from '../../utils/formatadores'

// US10, com acesso a US11 (modificar) e US12 (cancelar)
export default function MeusPedidos() {
  const location = useLocation()
  const [pedidos, setPedidos] = useState(null)
  const [erro, setErro] = useState(null)
  const [mensagem, setMensagem] = useState(location.state?.mensagem ?? null)

  useEffect(() => {
    pedidoService.listarMeus().then(setPedidos).catch((e) => setErro(e.message))
  }, [])

  function atualizarPedido(atualizado) {
    setPedidos(pedidos.map((p) => (p.id === atualizado.id ? atualizado : p)))
    setMensagem(`Pedido #${atualizado.id} cancelado.`)
  }

  return (
    <>
      <header className="pagina-cabecalho">
        <div>
          <h1>Meus pedidos</h1>
          <p>Acompanhe o status das suas solicitações de aluguel.</p>
        </div>
        <Link to="/pedidos/novo" className="btn btn-primario">
          + Novo pedido
        </Link>
      </header>

      <Alerta tipo="sucesso" className="pagina-alerta">{mensagem}</Alerta>
      <Alerta className="pagina-alerta">{erro}</Alerta>

      {pedidos === null && !erro && <p className="carregando">Carregando pedidos…</p>}

      {pedidos?.length === 0 && (
        <div className="vazio glass">
          <p>Você ainda não fez nenhum pedido de aluguel.</p>
          <Link to="/pedidos/novo" className="btn btn-primario">
            Fazer meu primeiro pedido
          </Link>
        </div>
      )}

      {pedidos?.length > 0 && (
        <ul className="lista" style={{ listStyle: 'none', padding: 0, margin: 0 }}>
          {pedidos.map((pedido) => (
            <li key={pedido.id} className="pedido-card glass">
              <div>
                <div className="pedido-carro">
                  {pedido.automovel.marca} {pedido.automovel.modelo}
                </div>
                <div className="pedido-meta">
                  <span>
                    Período: <strong>{formatarData(pedido.dataInicio)} a {formatarData(pedido.dataFim)}</strong>
                  </span>
                  <span>
                    Placa: <strong>{pedido.automovel.placa}</strong>
                  </span>
                  <span>Pedido #{pedido.id} · criado em {formatarDataHora(pedido.dataCriacao)}</span>
                  {pedido.avaliador && <span>Avaliado por {pedido.avaliador}</span>}
                  {pedido.proprietario && (
                    <span>
                      Proprietário: <strong>{PROPRIETARIOS[pedido.proprietario]}</strong>
                    </span>
                  )}
                  <CreditoResumo credito={pedido.credito} />
                </div>
              </div>
              <div className="pedido-lateral">
                <StatusBadge status={pedido.status} />
                <AcoesPedido pedido={pedido} onCancelado={atualizarPedido} onErro={setErro} />
              </div>
            </li>
          ))}
        </ul>
      )}
    </>
  )
}

function AcoesPedido({ pedido, onCancelado, onErro }) {
  const [confirmando, setConfirmando] = useState(false)
  const [cancelando, setCancelando] = useState(false)

  const podeModificar = pedido.status === 'EM_ANALISE'
  const podeCancelar = pedido.status === 'EM_ANALISE' || pedido.status === 'APROVADO'
  if (!podeCancelar) return null

  async function cancelar() {
    onErro(null)
    setCancelando(true)
    try {
      onCancelado(await pedidoService.cancelar(pedido.id))
    } catch (e) {
      onErro(e.message)
      setCancelando(false)
      setConfirmando(false)
    }
  }

  if (confirmando) {
    return (
      <div className="acoes">
        <button type="button" className="btn btn-perigo btn-sm" onClick={cancelar} disabled={cancelando}>
          {cancelando ? 'Cancelando…' : 'Confirmar cancelamento'}
        </button>
        <button type="button" className="btn btn-vidro btn-sm" onClick={() => setConfirmando(false)}>
          Voltar
        </button>
      </div>
    )
  }

  return (
    <div className="acoes">
      {podeModificar && (
        <Link to={`/pedidos/${pedido.id}/editar`} className="btn btn-vidro btn-sm">
          Modificar
        </Link>
      )}
      <button type="button" className="btn btn-perigo btn-sm" onClick={() => setConfirmando(true)}>
        Cancelar pedido
      </button>
    </div>
  )
}
