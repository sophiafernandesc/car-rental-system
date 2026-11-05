import CreditoResumo from './CreditoResumo.jsx'
import StatusBadge from './StatusBadge.jsx'
import { PROPRIETARIOS, contarDias, formatarData, formatarDataHora } from '../utils/formatadores'

function etapas(pedido) {
  const lista = [{ rotulo: 'Pedido criado', detalhe: formatarDataHora(pedido.dataCriacao) }]

  if (pedido.avaliador) {
    lista.push({
      rotulo: pedido.status === 'REPROVADO' ? 'Reprovado' : 'Aprovado',
      detalhe: `por ${pedido.avaliador}`,
    })
  }

  switch (pedido.status) {
    case 'EM_ANALISE':
      lista.push({ rotulo: 'Aguardando análise de um agente', pendente: true })
      break
    case 'APROVADO':
      lista.push({ rotulo: 'Aguardando execução do contrato', pendente: true })
      break
    case 'CONTRATADO':
      lista.push({
        rotulo: 'Contrato executado',
        detalhe: `${formatarData(pedido.dataContrato)} · proprietário: ${PROPRIETARIOS[pedido.proprietario]}`,
      })
      break
    case 'CANCELADO':
      lista.push({ rotulo: 'Pedido cancelado pelo cliente' })
      break
  }
  return lista
}

export default function PedidoDetalhe({ pedido }) {
  const { automovel } = pedido

  return (
    <div className="detalhe">
      <section className="detalhe-destaque">
        <div>
          <div className="carro-marca">{automovel.marca}</div>
          <div className="detalhe-carro">{automovel.modelo}</div>
          <div className="carro-detalhes">
            {automovel.ano} · Placa {automovel.placa} · Matrícula {automovel.matricula}
          </div>
        </div>
        <StatusBadge status={pedido.status} />
      </section>

      <section>
        <h3 className="secao-titulo">Período</h3>
        <p className="detalhe-periodo">
          {formatarData(pedido.dataInicio)} a {formatarData(pedido.dataFim)}
          <span> · {contarDias(pedido.dataInicio, pedido.dataFim)} dias</span>
        </p>
      </section>

      {pedido.credito && (
        <section>
          <h3 className="secao-titulo">Contrato de crédito</h3>
          <p className="detalhe-texto">
            <CreditoResumo credito={pedido.credito} />
          </p>
        </section>
      )}

      <section>
        <h3 className="secao-titulo">Andamento</h3>
        <ol className="linha-tempo">
          {etapas(pedido).map((etapa) => (
            <li key={etapa.rotulo} className={etapa.pendente ? 'pendente' : undefined}>
              <strong>{etapa.rotulo}</strong>
              {etapa.detalhe && <span>{etapa.detalhe}</span>}
            </li>
          ))}
        </ol>
      </section>
    </div>
  )
}
