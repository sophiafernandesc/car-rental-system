import { api } from './api'

export const automovelService = {
  listar: () => api.get('/automoveis'),
}
