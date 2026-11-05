import { api } from './api'

export const automovelService = {
  listar: () => api.get('/automoveis'),

  // Agente
  cadastrar: (dados) => api.post('/agente/automoveis', dados),
  atualizar: (id, dados) => api.put(`/agente/automoveis/${id}`, dados),
  remover: (id) => api.delete(`/agente/automoveis/${id}`),
}
