import { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import Alerta from '../components/Alerta.jsx'
import Campo from '../components/Campo.jsx'
import Marca from '../components/Marca.jsx'
import { useAuth } from '../contexts/AuthContext.jsx'

// US03 o redirecionamento após o login é feito pela rota
export default function Login() {
  const { login } = useAuth()
  const location = useLocation()
  const [form, setForm] = useState({ login: '', senha: '' })
  const [erro, setErro] = useState(null)
  const [enviando, setEnviando] = useState(false)

  function alterar(evento) {
    setForm({ ...form, [evento.target.name]: evento.target.value })
  }

  async function entrar(evento) {
    evento.preventDefault()
    setErro(null)
    setEnviando(true)
    try {
      await login(form.login, form.senha)
    } catch (e) {
      setErro(e.message)
      setEnviando(false)
    }
  }

  return (
    <div className="auth">
      <section className="auth-hero">
        <Marca grande />
        <h1>Seu próximo carro, a um pedido de distância.</h1>
        <p>Escolha o automóvel, informe o período e acompanhe a análise do seu pedido em tempo real.</p>
      </section>

      <section className="auth-card glass" aria-labelledby="titulo-login">
        <h2 id="titulo-login">Entrar</h2>
        <p>Acesse com seu login e senha.</p>

        <form className="form" onSubmit={entrar}>
          <Alerta tipo="sucesso">{location.state?.mensagem}</Alerta>
          <Alerta>{erro}</Alerta>

          <Campo id="login" label="Login" value={form.login} onChange={alterar} autoComplete="username" required autoFocus />
          <Campo id="senha" label="Senha" type="password" value={form.senha} onChange={alterar} autoComplete="current-password" required />

          <button type="submit" className="btn btn-primario btn-bloco" disabled={enviando}>
            {enviando ? 'Entrando…' : 'Entrar'}
          </button>
        </form>

        <p className="auth-rodape">
          Ainda não tem conta? <Link to="/cadastro">Cadastre-se</Link>
        </p>
      </section>
    </div>
  )
}
