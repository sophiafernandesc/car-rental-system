import { api } from './api'

export const authService = {
  login: (login, senha) => api.post('/auth/login', { login, senha }),
  cadastrar: (dados) => api.post('/auth/cadastro', dados),
}
