import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

// Wrap a set of routes with this to require a specific role.
// Usage: <Route element={<ProtectedRoute role="FARMER" />}> ...child routes... </Route>
export default function ProtectedRoute({ role }) {
  const { user } = useAuth();

  if (!user || user.role !== role) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
}
