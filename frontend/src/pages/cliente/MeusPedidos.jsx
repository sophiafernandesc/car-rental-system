import { useEffect, useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import Alerta from '../../components/Alerta.jsx'
import StatusBadge from '../../components/StatusBadge.jsx'
import { pedidoService } from '../../services/pedidoService'
import { formatarData, formatarDataHora } from '../../utils/formatadores'

// US10
export default function MeusPedidos() {
  const location = useLocation()
  const [pedidos, setPedidos] = useState(null)
  const [erro, setErro] = useState(null)

  useEffect(() => {
    pedidoService.listarMeus().then(setPedidos).catch((e) => setErro(e.message))
  }, [])

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

      <Alerta tipo="sucesso" className="pagina-alerta">{location.state?.mensagem}</Alerta>
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
                </div>
              </div>
              <div className="pedido-lateral">
                <StatusBadge status={pedido.status} />
              </div>
            </li>
          ))}
        </ul>
      )}
    </>
  )
}
