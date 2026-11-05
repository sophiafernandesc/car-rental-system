import { Navigate, Route, Routes } from 'react-router-dom'
import Layout from '../components/Layout.jsx'
import { useAuth } from '../contexts/AuthContext.jsx'
import PedidosAnalise from '../pages/agente/PedidosAnalise.jsx'
import Cadastro from '../pages/Cadastro.jsx'
import MeusPedidos from '../pages/cliente/MeusPedidos.jsx'
import PedidoForm from '../pages/cliente/PedidoForm.jsx'
import Perfil from '../pages/cliente/Perfil.jsx'
import Login from '../pages/Login.jsx'

function rotaInicial(sessao) {
  if (!sessao) return '/login'
  return sessao.perfil === 'AGENTE' ? '/agente/pedidos' : '/pedidos'
}

// Páginas internas: exigem login
function RotaProtegida({ perfil, children }) {
  const { sessao } = useAuth()
  if (!sessao) return <Navigate to="/login" replace />
  if (sessao.perfil !== perfil) return <Navigate to={rotaInicial(sessao)} replace />
  return children
}

// Login e cadastro: usuário autenticado é levado para a sua página inicial
function SomenteVisitante({ children }) {
  const { sessao } = useAuth()
  return sessao ? <Navigate to={rotaInicial(sessao)} replace /> : children
}

export default function AppRoutes() {
  const { sessao } = useAuth()

  return (
    <Routes>
      <Route path="/login" element={<SomenteVisitante><Login /></SomenteVisitante>} />
      <Route path="/cadastro" element={<SomenteVisitante><Cadastro /></SomenteVisitante>} />

      <Route element={<RotaProtegida perfil="CLIENTE"><Layout /></RotaProtegida>}>
        <Route path="/pedidos" element={<MeusPedidos />} />
        <Route path="/pedidos/novo" element={<PedidoForm />} />
        <Route path="/pedidos/:id/editar" element={<PedidoForm />} />
        <Route path="/perfil" element={<Perfil />} />
      </Route>

      <Route element={<RotaProtegida perfil="AGENTE"><Layout /></RotaProtegida>}>
        <Route path="/agente/pedidos" element={<PedidosAnalise />} />
      </Route>

      <Route path="*" element={<Navigate to={rotaInicial(sessao)} replace />} />
    </Routes>
  )
}
