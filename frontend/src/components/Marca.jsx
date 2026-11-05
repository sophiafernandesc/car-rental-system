export default function Marca({ grande = false }) {
  return (
    <span className={grande ? 'marca marca-grande' : 'marca'}>
      <svg viewBox="0 0 32 32" aria-hidden="true">
        <rect width="32" height="32" rx="9" fill="#3DDC84" />
        <path
          d="M7 19.5l1.6-4.6A3 3 0 0 1 11.4 13h9.2a3 3 0 0 1 2.8 1.9l1.6 4.6M6 19.5h20v4H6z"
          fill="none"
          stroke="#03130A"
          strokeWidth="2"
          strokeLinejoin="round"
        />
        <circle cx="10.5" cy="23.5" r="1.8" fill="#03130A" />
        <circle cx="21.5" cy="23.5" r="1.8" fill="#03130A" />
      </svg>
      Locaro
    </span>
  )
}
