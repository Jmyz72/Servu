import { apiBaseUrl } from '../api/client'

export function HomePage() {
  return (
    <section className="page-section">
      <p className="eyebrow">QR ordering platform</p>
      <h1>Servu</h1>
      <p>
        Basic environment is ready for customer ordering, vendor operations,
        and platform admin routes.
      </p>
      <div className="status-row">
        <span>Backend API</span>
        <code>{apiBaseUrl}</code>
      </div>
    </section>
  )
}
