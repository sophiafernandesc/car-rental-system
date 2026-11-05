export default function Campo({ id, label, erro, ajuda, ...inputProps }) {
  const descricaoId = erro ? `${id}-erro` : ajuda ? `${id}-ajuda` : undefined

  return (
    <div className="campo">
      <label htmlFor={id}>{label}</label>
      <input id={id} name={id} aria-invalid={erro ? 'true' : undefined} aria-describedby={descricaoId} {...inputProps} />
      {erro ? (
        <span id={`${id}-erro`} className="campo-erro">{erro}</span>
      ) : (
        ajuda && <span id={`${id}-ajuda`} className="campo-ajuda">{ajuda}</span>
      )}
    </div>
  )
}
