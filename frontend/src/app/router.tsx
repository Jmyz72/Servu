import { createBrowserRouter } from 'react-router-dom'
import { AdminDashboardPage } from '../pages/admin/AdminDashboardPage'
import { LoginPage } from '../pages/auth/LoginPage'
import { CustomerMenuPage } from '../pages/customer/CustomerMenuPage'
import { HomePage } from '../pages/HomePage'
import { VendorOrdersPage } from '../pages/vendor/VendorOrdersPage'
import { AppLayout } from './AppLayout'
import { ProtectedRoute } from './ProtectedRoute'

export const router = createBrowserRouter([
  {
    path: '/',
    element: <AppLayout />,
    children: [
      { index: true, element: <HomePage /> },
      { path: 'customer/:qrCodeId/menu', element: <CustomerMenuPage /> },
      { path: 'login', element: <LoginPage /> },
      {
        element: <ProtectedRoute />,
        children: [
          { path: 'vendor/orders', element: <VendorOrdersPage /> },
          { path: 'admin', element: <AdminDashboardPage /> },
        ],
      },
    ],
  },
])
