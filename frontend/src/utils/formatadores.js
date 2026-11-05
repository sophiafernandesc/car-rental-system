const moeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' })

export function formatarMoeda(valor) {
  return moeda.format(Number(valor ?? 0))
}

// "2026-12-01" -> "01/12/2026" (sem conversão de fuso horário)
export function formatarData(dataIso) {
  if (!dataIso) return ''
  const [ano, mes, dia] = dataIso.slice(0, 10).split('-')
  return `${dia}/${mes}/${ano}`
}

export function formatarDataHora(dataHoraIso) {
  if (!dataHoraIso) return ''
  return `${formatarData(dataHoraIso)} às ${dataHoraIso.slice(11, 16)}`
}

export function formatarCpf(cpf) {
  return cpf?.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4') ?? ''
}

// Data local no formato aceito por <input type="date">
export function hojeIso() {
  const hoje = new Date()
  const pad = (n) => String(n).padStart(2, '0')
  return `${hoje.getFullYear()}-${pad(hoje.getMonth() + 1)}-${pad(hoje.getDate())}`
}
