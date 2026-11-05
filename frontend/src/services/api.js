const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

export const SESSAO_STORAGE_KEY = 'locaro.sessao'
export const EVENTO_SESSAO_EXPIRADA = 'locaro:sessao-expirada'

export class ApiError extends Error {
  constructor(status, mensagem, erros = {}) {
    super(mensagem)
    this.status = status
    this.erros = erros
  }
}

function tokenAtual() {
  try {
    return JSON.parse(localStorage.getItem(SESSAO_STORAGE_KEY))?.token ?? null
  } catch {
    return null
  }
}

async function request(method, path, body) {
  const token = tokenAtual()
  const headers = {}
  if (body !== undefined) headers['Content-Type'] = 'application/json'
  if (token) headers.Authorization = `Bearer ${token}`

  let resposta
  try {
    resposta = await fetch(API_URL + path, {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    })
  } catch {
    throw new ApiError(0, 'Não foi possível conectar ao servidor. Verifique se a API está em execução.')
  }

  if (resposta.status === 204) return null

  const dados = await resposta.json().catch(() => null)

  if (!resposta.ok) {
    // Token expirado ou inválido: encerra a sessão (tratado no AuthContext)
    if (resposta.status === 401 && token) {
      window.dispatchEvent(new Event(EVENTO_SESSAO_EXPIRADA))
    }
    throw new ApiError(resposta.status, dados?.detail ?? 'Ocorreu um erro inesperado.', dados?.erros ?? {})
  }

  return dados
}

export const api = {
  get: (path) => request('GET', path),
  post: (path, body) => request('POST', path, body),
  put: (path, body) => request('PUT', path, body),
  delete: (path) => request('DELETE', path),
}
