import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext.jsx'
import Marca from './Marca.jsx'

const LINKS = {
  CLIENTE: [
    { to: '/pedidos', rotulo: 'Meus pedidos', end: true },
    { to: '/pedidos/novo', rotulo: 'Novo pedido' },
    { to: '/perfil', rotulo: 'Meu perfil' },
  ],
  AGENTE: [{ to: '/agente/pedidos', rotulo: 'Avaliação de pedidos' }],
}

const ROTULO_PERFIL = {
  CLIENTE: 'Cliente',
  AGENTE: 'Agente',
  EMPRESA: 'Agente · Empresa',
  BANCO: 'Agente · Banco',
}

export default function Layout() {
  const { sessao, logout } = useAuth()

  return (
    <div className="app">
      <header className="topo glass">
        <Marca />
        <nav className="topo-nav" aria-label="Navegação principal">
          {LINKS[sessao.perfil].map((link) => (
            <NavLink key={link.to} to={link.to} end={link.end} className="nav-link">
              {link.rotulo}
            </NavLink>
          ))}
        </nav>
        <div className="topo-usuario">
          <div className="usuario-info">
            <span className="usuario-nome">{sessao.nome}</span>
            <span className="usuario-perfil">{ROTULO_PERFIL[sessao.tipoAgente ?? sessao.perfil]}</span>
          </div>
          <button type="button" className="btn btn-vidro btn-sm" onClick={logout}>
            Sair
          </button>
        </div>
      </header>
      <main className="conteudo">
        <Outlet />
      </main>
    </div>
  )
}
