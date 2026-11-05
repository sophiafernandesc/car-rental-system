import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import Alerta from '../components/Alerta.jsx'
import Campo from '../components/Campo.jsx'
import Marca from '../components/Marca.jsx'
import { authService } from '../services/authService'

const TIPOS = { EMPRESA: 'Empresa', BANCO: 'Banco' }
const TITULO_DADOS = { EMPRESA: 'Dados da empresa', BANCO: 'Dados do banco' }

// US02
export default function CadastroAgente() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ login: '', senha: '', razaoSocial: '', cnpj: '', tipo: 'EMPRESA' })
  const [erro, setErro] = useState(null)
  const [erros, setErros] = useState({})
  const [enviando, setEnviando] = useState(false)

  function alterar(evento) {
    setForm({ ...form, [evento.target.name]: evento.target.value })
  }

  async function cadastrar(evento) {
    evento.preventDefault()
    setErro(null)
    setErros({})
    setEnviando(true)
    try {
      await authService.cadastrarAgente(form)
      navigate('/login', { state: { mensagem: 'Cadastro de agente realizado! Faça login para continuar.' } })
    } catch (e) {
      setErro(e.message)
      setErros(e.erros ?? {})
      setEnviando(false)
    }
  }

  return (
    <div className="auth auth-unica">
      <section className="auth-card auth-card-largo glass" aria-labelledby="titulo-cadastro">
        <Marca />
        <h2 id="titulo-cadastro" style={{ marginTop: 20 }}>Criar conta de agente</h2>
        <p>Para empresas e bancos que avaliam pedidos e executam contratos de aluguel.</p>

        <form className="form" onSubmit={cadastrar} noValidate>
          <Alerta>{erro}</Alerta>

          <div className="form-secao">
            <h3 className="secao-titulo">Tipo de agente</h3>
            <div className="acoes" role="group" aria-label="Tipo de agente">
              {Object.entries(TIPOS).map(([valor, rotulo]) => (
                <button
                  key={valor}
                  type="button"
                  className="btn btn-vidro"
                  aria-pressed={form.tipo === valor}
                  onClick={() => setForm({ ...form, tipo: valor })}
                >
                  {rotulo}
                </button>
              ))}
            </div>
          </div>

          <div className="form-secao">
            <h3 className="secao-titulo">Acesso</h3>
            <div className="form-grid">
              <Campo id="login" label="Login" value={form.login} onChange={alterar} erro={erros.login} autoComplete="username" required />
              <Campo id="senha" label="Senha" type="password" value={form.senha} onChange={alterar} erro={erros.senha} ajuda="Mínimo de 6 caracteres" autoComplete="new-password" required />
            </div>
          </div>

          <div className="form-secao">
            <h3 className="secao-titulo">{TITULO_DADOS[form.tipo]}</h3>
            <div className="form-grid">
              <Campo id="razaoSocial" label="Razão social" value={form.razaoSocial} onChange={alterar} erro={erros.razaoSocial} autoComplete="organization" required />
              <Campo id="cnpj" label="CNPJ" value={form.cnpj} onChange={alterar} erro={erros.cnpj} placeholder="00.000.000/0000-00" inputMode="numeric" required />
            </div>
          </div>

          <button type="submit" className="btn btn-primario btn-bloco" disabled={enviando}>
            {enviando ? 'Cadastrando…' : 'Criar conta de agente'}
          </button>
        </form>

        <p className="auth-rodape">
          Já tem conta? <Link to="/login">Entrar</Link>
        </p>
      </section>
    </div>
  )
}
