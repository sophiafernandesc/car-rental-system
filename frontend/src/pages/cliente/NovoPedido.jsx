import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Alerta from '../../components/Alerta.jsx'
import Campo from '../../components/Campo.jsx'
import { automovelService } from '../../services/automovelService'
import { pedidoService } from '../../services/pedidoService'
import { hojeIso } from '../../utils/formatadores'

// US09
export default function NovoPedido() {
  const navigate = useNavigate()
  const [automoveis, setAutomoveis] = useState(null)
  const [automovelId, setAutomovelId] = useState(null)
  const [periodo, setPeriodo] = useState({ dataInicio: '', dataFim: '' })
  const [erro, setErro] = useState(null)
  const [erros, setErros] = useState({})
  const [enviando, setEnviando] = useState(false)

  useEffect(() => {
    automovelService.listar().then(setAutomoveis).catch((e) => setErro(e.message))
  }, [])

  function alterarPeriodo(evento) {
    setPeriodo({ ...periodo, [evento.target.name]: evento.target.value })
  }

  async function enviar(evento) {
    evento.preventDefault()
    if (!automovelId) {
      setErro('Selecione um automóvel.')
      return
    }
    setErro(null)
    setErros({})
    setEnviando(true)
    try {
      const pedido = await pedidoService.criar({ automovelId, ...periodo })
      navigate('/pedidos', { state: { mensagem: `Pedido #${pedido.id} enviado para análise.` } })
    } catch (e) {
      setErro(e.message)
      setErros(e.erros ?? {})
      setEnviando(false)
    }
  }

  return (
    <>
      <header className="pagina-cabecalho">
        <div>
          <h1>Novo pedido de aluguel</h1>
          <p>Escolha o automóvel e o período. Seu pedido será analisado por um agente.</p>
        </div>
      </header>

      <form className="novo-pedido" onSubmit={enviar}>
        <Alerta>{erro}</Alerta>

        <section className="painel glass" aria-labelledby="titulo-automovel">
          <h2 id="titulo-automovel" className="secao-titulo">1. Automóvel</h2>
          {automoveis === null && !erro && <p className="carregando">Carregando automóveis…</p>}
          <div className="grid-carros">
            {automoveis?.map((carro) => (
              <button
                key={carro.id}
                type="button"
                className="carro-opcao glass"
                aria-pressed={automovelId === carro.id}
                onClick={() => setAutomovelId(carro.id)}
              >
                <div className="carro-marca">{carro.marca}</div>
                <div className="carro-modelo">{carro.modelo}</div>
                <div className="carro-detalhes">
                  {carro.ano} · Placa {carro.placa}
                </div>
              </button>
            ))}
          </div>
        </section>

        <section className="painel glass" aria-labelledby="titulo-periodo">
          <h2 id="titulo-periodo" className="secao-titulo">2. Período</h2>
          <div className="form-grid">
            <Campo
              id="dataInicio"
              label="Data de início"
              type="date"
              min={hojeIso()}
              value={periodo.dataInicio}
              onChange={alterarPeriodo}
              erro={erros.dataInicio}
              required
            />
            <Campo
              id="dataFim"
              label="Data de devolução"
              type="date"
              min={periodo.dataInicio || hojeIso()}
              value={periodo.dataFim}
              onChange={alterarPeriodo}
              erro={erros.dataFim}
              required
            />
          </div>
        </section>

        <div className="acoes">
          <button type="submit" className="btn btn-primario" disabled={enviando}>
            {enviando ? 'Enviando…' : 'Enviar pedido'}
          </button>
          <button type="button" className="btn btn-vidro" onClick={() => navigate('/pedidos')}>
            Cancelar
          </button>
        </div>
      </form>
    </>
  )
}
