import { useParams } from 'react-router-dom'

export function CustomerMenuPage() {
  const { qrCodeId } = useParams()

  return (
    <section className="page-section">
      <p className="eyebrow">Customer</p>
      <h1>Menu</h1>
      <p>QR code reference: {qrCodeId}</p>
    </section>
  )
}
