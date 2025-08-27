import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LoginTabs from '../pages/LoginTabs';
import Register from '../pages/Register';
import AdminLogin from '../pages/AdminLogin';
import AdminSignup from '../pages/AdminSignup';
import Books from '../pages/Books';
import BookDetails from '../pages/BookDetails';
import Profile from '../pages/Profile';
import Favorites from '../pages/Favorites';
import RequireAuth from '../components/RequireAuth';
import AdminPanel from '../pages/AdminPanel';

const AppRoutes: React.FC = () => (
  <Router>
    <Routes>
  <Route path="/login" element={<LoginTabs />} />
  <Route path="/register" element={<Register />} />
  <Route path="/admin/login" element={<AdminLogin />} />
  <Route path="/admin/signup" element={<AdminSignup />} />
      <Route path="/books" element={<RequireAuth><Books /></RequireAuth>} />
      <Route path="/books/:id" element={<RequireAuth><BookDetails /></RequireAuth>} />
      <Route path="/profile" element={<RequireAuth><Profile /></RequireAuth>} />
      <Route path="/favorites" element={<RequireAuth><Favorites /></RequireAuth>} />
      <Route path="/admin" element={<RequireAuth roles={["ADMIN"]}><AdminPanel /></RequireAuth>} />
      <Route path="*" element={<Navigate to="/books" replace />} />
    </Routes>
  </Router>
);

export default AppRoutes;
