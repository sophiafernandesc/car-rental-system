import { api } from './api'

export const clienteService = {
  buscar: () => api.get('/clientes/me'),
  atualizar: (dados) => api.put('/clientes/me', dados),
  excluir: () => api.delete('/clientes/me'),
  adicionarRendimento: (dados) => api.post('/clientes/me/rendimentos', dados),
  atualizarRendimento: (id, dados) => api.put(`/clientes/me/rendimentos/${id}`, dados),
  removerRendimento: (id) => api.delete(`/clientes/me/rendimentos/${id}`),
}
