import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import Alerta from '../../components/Alerta.jsx'
import Campo from '../../components/Campo.jsx'
import { automovelService } from '../../services/automovelService'
import { pedidoService } from '../../services/pedidoService'
import { hojeIso } from '../../utils/formatadores'

// US09 (novo pedido) e US11 (modificar pedido em análise)
export default function PedidoForm() {
  const { id } = useParams()
  const edicao = Boolean(id)
  const navigate = useNavigate()
  const [automoveis, setAutomoveis] = useState(null)
  const [automovelId, setAutomovelId] = useState(null)
  const [periodo, setPeriodo] = useState({ dataInicio: '', dataFim: '' })
  const [bloqueado, setBloqueado] = useState(false)
  const [erro, setErro] = useState(null)
  const [erros, setErros] = useState({})
  const [enviando, setEnviando] = useState(false)

  useEffect(() => {
    Promise.all([automovelService.listar(), edicao ? pedidoService.buscar(id) : null])
      .then(([carros, pedido]) => {
        setAutomoveis(carros)
        if (pedido) {
          setAutomovelId(pedido.automovel.id)
          setPeriodo({ dataInicio: pedido.dataInicio, dataFim: pedido.dataFim })
          if (pedido.status !== 'EM_ANALISE') {
            setBloqueado(true)
            setErro('Este pedido não pode mais ser modificado: apenas pedidos em análise podem ser alterados.')
          }
        }
      })
      .catch((e) => setErro(e.message))
  }, [edicao, id])

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
      const dados = { automovelId, ...periodo }
      const pedido = edicao ? await pedidoService.modificar(id, dados) : await pedidoService.criar(dados)
      const mensagem = edicao ? `Pedido #${pedido.id} atualizado.` : `Pedido #${pedido.id} enviado para análise.`
      navigate('/pedidos', { state: { mensagem } })
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
          <h1>{edicao ? `Modificar pedido #${id}` : 'Novo pedido de aluguel'}</h1>
          <p>
            {edicao
              ? 'Altere o automóvel ou o período. O pedido continua em análise.'
              : 'Escolha o automóvel e o período. Seu pedido será analisado por um agente.'}
          </p>
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
                disabled={bloqueado}
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
              disabled={bloqueado}
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
              disabled={bloqueado}
              required
            />
          </div>
        </section>

        <div className="acoes">
          <button type="submit" className="btn btn-primario" disabled={enviando || bloqueado}>
            {enviando ? 'Enviando…' : edicao ? 'Salvar alterações' : 'Enviar pedido'}
          </button>
          <button type="button" className="btn btn-vidro" onClick={() => navigate('/pedidos')}>
            Voltar
          </button>
        </div>
      </form>
    </>
  )
}
