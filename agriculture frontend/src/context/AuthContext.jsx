import { createContext, useContext, useState } from 'react';

const AuthContext = createContext(null);

function readStoredUser() {
  const token = localStorage.getItem('token');
  if (!token) return null;
  return {
    token,
    userId: localStorage.getItem('userId'),
    name: localStorage.getItem('name'),
    email: localStorage.getItem('email'),
    role: localStorage.getItem('role'),
  };
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readStoredUser);

  function login(authResponse) {
    localStorage.setItem('token', authResponse.token);
    localStorage.setItem('userId', authResponse.userId);
    localStorage.setItem('name', authResponse.name);
    localStorage.setItem('email', authResponse.email);
    localStorage.setItem('role', authResponse.role);
    setUser(readStoredUser());
  }

  function logout() {
    localStorage.clear();
    setUser(null);
  }

  return <AuthContext.Provider value={{ user, login, logout }}>{children}</AuthContext.Provider>;
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  return useContext(AuthContext);
}
