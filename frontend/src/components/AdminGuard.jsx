import { Navigate } from 'react-router-dom'
import { isAuthenticated } from '../api/admin'

export default function AdminGuard({ children }) {
  return isAuthenticated() ? children : <Navigate to="/admin" replace />
}
