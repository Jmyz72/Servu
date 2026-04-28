import { Link, Outlet } from 'react-router-dom'

export function AppLayout() {
  return (
    <div className="app-shell">
      <header className="top-bar">
        <Link to="/" className="brand">
          Servu
        </Link>
        <nav aria-label="Primary navigation">
          <Link to="/customer/demo/menu">Customer</Link>
          <Link to="/vendor/orders">Vendor</Link>
          <Link to="/admin">Admin</Link>
          <Link to="/login">Login</Link>
        </nav>
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  )
}
