import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function AdminLayout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate('/');
  }

  return (
    <div className="app-layout">
      <aside className="sidebar">
        <div className="sidebar-brand">🌾 Smart <span>Agriculture</span></div>
        <div className="sidebar-role">Admin</div>

        <nav className="sidebar-nav">
          <NavLink to="/admin/crops">Crops</NavLink>
          <NavLink to="/admin/fertilizers">Fertilizers</NavLink>
          <NavLink to="/admin/diseases">Diseases</NavLink>
          <NavLink to="/admin/farmers">Farmers</NavLink>
        </nav>

        <div className="sidebar-footer">
          <div className="sidebar-user">{user?.name}</div>
          <button className="btn-outline btn-small btn-block" onClick={handleLogout}>Log out</button>
        </div>
      </aside>

      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}
