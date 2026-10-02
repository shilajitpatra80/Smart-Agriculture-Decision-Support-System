import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function FarmerLayout() {
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
        <div className="sidebar-role">Farmer</div>

        <nav className="sidebar-nav">
          <NavLink to="/farmer" end>Today</NavLink>
          <NavLink to="/farmer/recommend">Recommend a crop</NavLink>
          <NavLink to="/farmer/schedule">Irrigation schedule</NavLink>
          <NavLink to="/farmer/profile">My profile</NavLink>
          <NavLink to="/farmer/weather">Weather</NavLink>
          <NavLink to="/farmer/history">History</NavLink>
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
