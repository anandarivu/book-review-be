import { create } from 'zustand';
import { jwtDecode } from 'jwt-decode';

interface AuthState {
  token: string | null;
  role: string | null;
  setAuth: (token: string, role?: string) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  token: localStorage.getItem('token'),
  role: localStorage.getItem('role'),
  setAuth: (token, role) => {
    let decodedRole = role;
    if (!decodedRole) {
      try {
        const decoded: any = jwtDecode(token);
        decodedRole = decoded.role || decoded.roles || decoded['authorities'] || undefined;
        if (Array.isArray(decodedRole)) decodedRole = decodedRole[0];
      } catch {}
    }
    localStorage.setItem('token', token);
    localStorage.setItem('role', decodedRole || '');
    set({ token, role: decodedRole });
  },
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    set({ token: null, role: null });
  },
}));
