import { api } from './api'

export const pedidoService = {
  // Cliente
  criar: (dados) => api.post('/pedidos', dados),
  listarMeus: () => api.get('/pedidos'),

  // Agente
  listarPorStatus: (status) => api.get(`/agente/pedidos?status=${status}`),
  aprovar: (id) => api.post(`/agente/pedidos/${id}/aprovar`),
  reprovar: (id) => api.post(`/agente/pedidos/${id}/reprovar`),
}
