import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import Alerta from '../components/Alerta.jsx'
import Campo from '../components/Campo.jsx'
import Marca from '../components/Marca.jsx'
import { authService } from '../services/authService'

const MAX_RENDIMENTOS = 3

// US01
export default function Cadastro() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ login: '', senha: '', nome: '', cpf: '', rg: '', endereco: '', profissao: '' })
  const [rendimentos, setRendimentos] = useState([])
  const [erro, setErro] = useState(null)
  const [erros, setErros] = useState({})
  const [enviando, setEnviando] = useState(false)

  function alterar(evento) {
    setForm({ ...form, [evento.target.name]: evento.target.value })
  }

  function alterarRendimento(indice, campo, valor) {
    setRendimentos(rendimentos.map((r, i) => (i === indice ? { ...r, [campo]: valor } : r)))
  }

  async function cadastrar(evento) {
    evento.preventDefault()
    setErro(null)
    setErros({})
    setEnviando(true)
    try {
      await authService.cadastrar({
        ...form,
        rendimentos: rendimentos.map((r) => ({ entidadeEmpregadora: r.entidadeEmpregadora, valor: Number(r.valor) })),
      })
      navigate('/login', { state: { mensagem: 'Cadastro realizado! Faça login para continuar.' } })
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
        <h2 id="titulo-cadastro" style={{ marginTop: 20 }}>Criar conta de cliente</h2>
        <p>Preencha seus dados para solicitar aluguéis de automóveis.</p>

        <form className="form" onSubmit={cadastrar} noValidate>
          <Alerta>{erro}</Alerta>

          <div className="form-secao">
            <h3 className="secao-titulo">Acesso</h3>
            <div className="form-grid">
              <Campo id="login" label="Login" value={form.login} onChange={alterar} erro={erros.login} autoComplete="username" required />
              <Campo id="senha" label="Senha" type="password" value={form.senha} onChange={alterar} erro={erros.senha} ajuda="Mínimo de 6 caracteres" autoComplete="new-password" required />
            </div>
          </div>

          <div className="form-secao">
            <h3 className="secao-titulo">Dados pessoais</h3>
            <div className="form-grid">
              <Campo id="nome" label="Nome completo" value={form.nome} onChange={alterar} erro={erros.nome} autoComplete="name" required />
              <Campo id="cpf" label="CPF" value={form.cpf} onChange={alterar} erro={erros.cpf} placeholder="000.000.000-00" inputMode="numeric" required />
              <Campo id="rg" label="RG" value={form.rg} onChange={alterar} erro={erros.rg} required />
              <Campo id="profissao" label="Profissão" value={form.profissao} onChange={alterar} erro={erros.profissao} required />
            </div>
            <div style={{ marginTop: 16 }}>
              <Campo id="endereco" label="Endereço" value={form.endereco} onChange={alterar} erro={erros.endereco} autoComplete="street-address" required />
            </div>
          </div>

          <div className="form-secao">
            <h3 className="secao-titulo">Rendimentos (até {MAX_RENDIMENTOS})</h3>
            <div className="rendimentos">
              {rendimentos.map((r, i) => (
                <div key={i} className="rendimento-form">
                  <Campo
                    id={`rendimento-entidade-${i}`}
                    label="Entidade empregadora"
                    value={r.entidadeEmpregadora}
                    onChange={(e) => alterarRendimento(i, 'entidadeEmpregadora', e.target.value)}
                    erro={erros[`rendimentos[${i}].entidadeEmpregadora`]}
                  />
                  <Campo
                    id={`rendimento-valor-${i}`}
                    label="Valor (R$)"
                    type="number"
                    min="0.01"
                    step="0.01"
                    value={r.valor}
                    onChange={(e) => alterarRendimento(i, 'valor', e.target.value)}
                    erro={erros[`rendimentos[${i}].valor`]}
                  />
                  <div className="acoes">
                    <button type="button" className="btn btn-perigo btn-sm" onClick={() => setRendimentos(rendimentos.filter((_, j) => j !== i))}>
                      Remover
                    </button>
                  </div>
                </div>
              ))}
              {erros.rendimentos && <span className="campo-erro">{erros.rendimentos}</span>}
              {rendimentos.length < MAX_RENDIMENTOS && (
                <button
                  type="button"
                  className="btn btn-vidro"
                  onClick={() => setRendimentos([...rendimentos, { entidadeEmpregadora: '', valor: '' }])}
                >
                  + Adicionar rendimento
                </button>
              )}
            </div>
          </div>

          <button type="submit" className="btn btn-primario btn-bloco" disabled={enviando}>
            {enviando ? 'Cadastrando…' : 'Criar conta'}
          </button>
        </form>

        <p className="auth-rodape">
          Já tem conta? <Link to="/login">Entrar</Link>
        </p>
      </section>
    </div>
  )
}
