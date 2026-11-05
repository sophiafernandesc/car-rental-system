import { api } from './api'

export const pedidoService = {
  // Cliente
  criar: (dados) => api.post('/pedidos', dados),
  listarMeus: () => api.get('/pedidos'),
  buscar: (id) => api.get(`/pedidos/${id}`),
  modificar: (id, dados) => api.put(`/pedidos/${id}`, dados),
  cancelar: (id) => api.post(`/pedidos/${id}/cancelar`),

  // Agente
  listarPorStatus: (status) => api.get(`/agente/pedidos?status=${status}`),
  aprovar: (id) => api.post(`/agente/pedidos/${id}/aprovar`),
  reprovar: (id) => api.post(`/agente/pedidos/${id}/reprovar`),
  executarContrato: (id, tipoProprietario, credito) =>
    api.post(`/agente/pedidos/${id}/contrato`, { tipoProprietario, credito }),
}
