// Card clicável que abre o detalhe do pedido
export default function PedidoCard({ titulo, lateral, onAbrir, children }) {
  return (
    <li>
      <button type="button" className="pedido-card glass" onClick={onAbrir}>
        <div>
          <div className="pedido-carro">{titulo}</div>
          <div className="pedido-meta">{children}</div>
        </div>
        <div className="pedido-lateral">
          {lateral}
          <span className="pedido-ver">Ver detalhes ›</span>
        </div>
      </button>
    </li>
  )
}
