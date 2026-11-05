import { useEffect, useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import Alerta from '../../components/Alerta.jsx'
import Modal from '../../components/Modal.jsx'
import PedidoCard from '../../components/PedidoCard.jsx'
import PedidoDetalhe from '../../components/PedidoDetalhe.jsx'
import StatusBadge from '../../components/StatusBadge.jsx'
import { pedidoService } from '../../services/pedidoService'
import { formatarData } from '../../utils/formatadores'

const FILTROS = [
  { id: 'todos', rotulo: 'Todos', status: null },
  { id: 'abertos', rotulo: 'Em aberto', status: ['EM_ANALISE', 'APROVADO'] },
  { id: 'contratados', rotulo: 'Contratados', status: ['CONTRATADO'] },
  { id: 'encerrados', rotulo: 'Encerrados', status: ['REPROVADO', 'CANCELADO'] },
]

// US10, com acesso a US11 (modificar) e US12 (cancelar)
export default function MeusPedidos() {
  const location = useLocation()
  const [pedidos, setPedidos] = useState(null)
  const [filtro, setFiltro] = useState('todos')
  const [selecionado, setSelecionado] = useState(null)
  const [erro, setErro] = useState(null)
  const [mensagem, setMensagem] = useState(location.state?.mensagem ?? null)

  useEffect(() => {
    pedidoService.listarMeus().then(setPedidos).catch((e) => setErro(e.message))
  }, [])

  const filtroAtual = FILTROS.find((f) => f.id === filtro)
  const pertenceAo = (f, pedido) => !f.status || f.status.includes(pedido.status)
  const visiveis = pedidos?.filter((p) => pertenceAo(filtroAtual, p)) ?? []

  function aoCancelar(atualizado) {
    setPedidos(pedidos.map((p) => (p.id === atualizado.id ? atualizado : p)))
    setSelecionado(null)
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

      {pedidos?.length > 0 && (
        <div className="abas glass" role="tablist" aria-label="Filtrar pedidos">
          {FILTROS.map((f) => (
            <button
              key={f.id}
              type="button"
              role="tab"
              className="aba"
              aria-selected={filtro === f.id}
              onClick={() => setFiltro(f.id)}
            >
              {f.rotulo} <span className="aba-contador">{pedidos.filter((p) => pertenceAo(f, p)).length}</span>
            </button>
          ))}
        </div>
      )}

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

      {pedidos?.length > 0 && visiveis.length === 0 && (
        <div className="vazio glass">
          <p>Nenhum pedido neste filtro.</p>
        </div>
      )}

      {visiveis.length > 0 && (
        <ul className="lista">
          {visiveis.map((pedido) => (
            <PedidoCard
              key={pedido.id}
              titulo={`${pedido.automovel.marca} ${pedido.automovel.modelo}`}
              lateral={<StatusBadge status={pedido.status} />}
              onAbrir={() => setSelecionado(pedido)}
            >
              <span>
                Período: <strong>{formatarData(pedido.dataInicio)} a {formatarData(pedido.dataFim)}</strong>
              </span>
              <span>
                Placa: <strong>{pedido.automovel.placa}</strong>
              </span>
              <span>Pedido #{pedido.id}</span>
            </PedidoCard>
          ))}
        </ul>
      )}

      {selecionado && (
        <Modal titulo={`Pedido #${selecionado.id}`} onFechar={() => setSelecionado(null)}>
          <PedidoDetalhe pedido={selecionado} />
          <AcoesPedido pedido={selecionado} onCancelado={aoCancelar} />
        </Modal>
      )}
    </>
  )
}

function AcoesPedido({ pedido, onCancelado }) {
  const [confirmando, setConfirmando] = useState(false)
  const [cancelando, setCancelando] = useState(false)
  const [erro, setErro] = useState(null)

  const podeModificar = pedido.status === 'EM_ANALISE'
  const podeCancelar = pedido.status === 'EM_ANALISE' || pedido.status === 'APROVADO'
  if (!podeCancelar) return null

  async function cancelar() {
    setErro(null)
    setCancelando(true)
    try {
      onCancelado(await pedidoService.cancelar(pedido.id))
    } catch (e) {
      setErro(e.message)
      setCancelando(false)
      setConfirmando(false)
    }
  }

  return (
    <footer className="modal-acoes">
      <Alerta>{erro}</Alerta>
      {confirmando ? (
        <div className="acoes">
          <span className="modal-pergunta">Cancelar este pedido?</span>
          <button type="button" className="btn btn-perigo" onClick={cancelar} disabled={cancelando}>
            {cancelando ? 'Cancelando…' : 'Sim, cancelar'}
          </button>
          <button type="button" className="btn btn-vidro" onClick={() => setConfirmando(false)}>
            Voltar
          </button>
        </div>
      ) : (
        <div className="acoes">
          {podeModificar && (
            <Link to={`/pedidos/${pedido.id}/editar`} className="btn btn-vidro">
              Modificar pedido
            </Link>
          )}
          <button type="button" className="btn btn-perigo" onClick={() => setConfirmando(true)}>
            Cancelar pedido
          </button>
        </div>
      )}
    </footer>
  )
}
