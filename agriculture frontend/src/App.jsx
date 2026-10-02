import { Routes, Route, Navigate } from 'react-router-dom';

import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ProtectedRoute from './components/ProtectedRoute';

import FarmerLayout from './layouts/FarmerLayout';
import FarmerHome from './pages/farmer/FarmerHome';
import RecommendPage from './pages/farmer/RecommendPage';
import SchedulePage from './pages/farmer/SchedulePage';
import ProfilePage from './pages/farmer/ProfilePage';
import WeatherPage from './pages/farmer/WeatherPage';
import HistoryPage from './pages/farmer/HistoryPage';

import AdminLayout from './layouts/AdminLayout';
import CropsPage from './pages/admin/CropsPage';
import FertilizersPage from './pages/admin/FertilizersPage';
import DiseasesPage from './pages/admin/DiseasesPage';
import FarmersPage from './pages/admin/FarmersPage';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      <Route element={<ProtectedRoute role="FARMER" />}>
        <Route path="/farmer" element={<FarmerLayout />}>
          <Route index element={<FarmerHome />} />
          <Route path="recommend" element={<RecommendPage />} />
          <Route path="schedule" element={<SchedulePage />} />
          <Route path="profile" element={<ProfilePage />} />
          <Route path="weather" element={<WeatherPage />} />
          <Route path="history" element={<HistoryPage />} />
        </Route>
      </Route>

      <Route element={<ProtectedRoute role="ADMIN" />}>
        <Route path="/admin" element={<AdminLayout />}>
          <Route index element={<Navigate to="crops" replace />} />
          <Route path="crops" element={<CropsPage />} />
          <Route path="fertilizers" element={<FertilizersPage />} />
          <Route path="diseases" element={<DiseasesPage />} />
          <Route path="farmers" element={<FarmersPage />} />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
