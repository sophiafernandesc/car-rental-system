import { createContext, useContext, useEffect, useState } from 'react'
import { EVENTO_SESSAO_EXPIRADA, SESSAO_STORAGE_KEY } from '../services/api'
import { authService } from '../services/authService'

const AuthContext = createContext(null)

function sessaoSalva() {
  try {
    return JSON.parse(localStorage.getItem(SESSAO_STORAGE_KEY))
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  // sessao = { token, id, nome, perfil }
  const [sessao, setSessao] = useState(sessaoSalva)

  async function login(usuario, senha) {
    const novaSessao = await authService.login(usuario, senha)
    localStorage.setItem(SESSAO_STORAGE_KEY, JSON.stringify(novaSessao))
    setSessao(novaSessao)
    return novaSessao
  }

  // US04: com JWT, o logout descarta o token no cliente
  function logout() {
    localStorage.removeItem(SESSAO_STORAGE_KEY)
    setSessao(null)
  }

  useEffect(() => {
    window.addEventListener(EVENTO_SESSAO_EXPIRADA, logout)
    return () => window.removeEventListener(EVENTO_SESSAO_EXPIRADA, logout)
  }, [])

  return <AuthContext.Provider value={{ sessao, login, logout }}>{children}</AuthContext.Provider>
}

export function useAuth() {
  return useContext(AuthContext)
}
