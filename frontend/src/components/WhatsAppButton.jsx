const NUMERO = '221763628768'

export default function WhatsAppButton() {
  const msg = encodeURIComponent('Bonjour Experts Voyages, je souhaite des informations sur les caravanes.')
  return (
    <a
      href={`https://wa.me/${NUMERO}?text=${msg}`}
      target="_blank"
      rel="noreferrer"
      aria-label="Contacter sur WhatsApp"
      className="ev-whatsapp position-fixed bottom-0 end-0 m-3 rounded-circle shadow d-flex align-items-center justify-content-center"
    >
      <i className="bi bi-whatsapp" />
    </a>
  )
}