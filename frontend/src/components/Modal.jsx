import { useEffect, useRef } from 'react'

// <dialog> nativo: fecha com Esc e mantém o foco do teclado dentro do painel
export default function Modal({ titulo, onFechar, children }) {
  const dialogRef = useRef(null)

  // Sem cleanup: ao desmontar, o <dialog> sai do DOM e deixa de ser modal.
  // Chamar close() aqui dispararia onClose no StrictMode e fecharia o modal recém-aberto.
  useEffect(() => {
    const dialog = dialogRef.current
    if (!dialog.open) dialog.showModal()
  }, [])

  function fecharAoClicarFora(evento) {
    if (evento.target === dialogRef.current) onFechar()
  }

  return (
    <dialog ref={dialogRef} className="modal" onClose={onFechar} onClick={fecharAoClicarFora} aria-labelledby="modal-titulo">
      <div className="modal-painel glass">
        <header className="modal-cabecalho">
          <h2 id="modal-titulo">{titulo}</h2>
          <button type="button" className="btn btn-vidro btn-sm" onClick={onFechar} aria-label="Fechar">
            ✕
          </button>
        </header>
        {children}
      </div>
    </dialog>
  )
}
