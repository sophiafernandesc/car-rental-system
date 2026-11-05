export default function Alerta({ tipo = 'erro', className = '', children }) {
  if (!children) return null

  return (
    <div role={tipo === 'erro' ? 'alert' : 'status'} className={`alerta alerta-${tipo} ${className}`}>
      {children}
    </div>
  )
}
