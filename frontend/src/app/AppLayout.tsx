import { Link, Outlet } from 'react-router-dom'
import { useAuth } from './useAuth'

export function AppLayout() {
  const { status, user, logout } = useAuth()

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
          {status === 'authenticated' ? (
            <button className="nav-button" type="button" onClick={() => void logout()}>
              Logout
            </button>
          ) : (
            <Link to="/login">Login</Link>
          )}
        </nav>
      </header>
      {user ? (
        <div className="session-strip">
          <span>{user.displayName}</span>
          <span>{user.role.replaceAll('_', ' ')}</span>
        </div>
      ) : null}
      <main>
        <Outlet />
      </main>
    </div>
  )
}
