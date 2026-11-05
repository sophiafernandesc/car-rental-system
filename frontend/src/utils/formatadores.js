const moeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' })

export function formatarMoeda(valor) {
  return moeda.format(Number(valor ?? 0))
}

export function formatarData(dataIso) {
  if (!dataIso) return ''
  const [ano, mes, dia] = dataIso.slice(0, 10).split('-')
  return `${dia}/${mes}/${ano}`
}

export function formatarDataHora(dataHoraIso) {
  if (!dataHoraIso) return ''
  return `${formatarData(dataHoraIso)} às ${dataHoraIso.slice(11, 16)}`
}

export const PROPRIETARIOS = {
  CLIENTE: 'Cliente',
  EMPRESA: 'Empresa',
  BANCO: 'Banco',
}

export function formatarCpf(cpf) {
  return cpf?.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4') ?? ''
}

export function contarDias(dataInicioIso, dataFimIso) {
  const umDia = 24 * 60 * 60 * 1000
  return Math.round((Date.parse(dataFimIso) - Date.parse(dataInicioIso)) / umDia)
}

export function hojeIso() {
  const hoje = new Date()
  const pad = (n) => String(n).padStart(2, '0')
  return `${hoje.getFullYear()}-${pad(hoje.getMonth() + 1)}-${pad(hoje.getDate())}`
}
