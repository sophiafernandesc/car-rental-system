import { formatarMoeda } from '../utils/formatadores'

const percentual = new Intl.NumberFormat('pt-BR', { maximumFractionDigits: 2 })

export default function CreditoResumo({ credito }) {
  if (!credito) return null

  return (
    <span>
      Crédito: <strong>{formatarMoeda(credito.valor)} em {credito.prazoMeses}x</strong> ·{' '}
      {percentual.format(credito.taxaJuros)}% a.m. · {credito.banco}
    </span>
  )
}
