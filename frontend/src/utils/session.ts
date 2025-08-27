// Utility to handle JWT/session expiry
export function handleJwtExpired(navigate: (path: string) => void) {
  localStorage.clear();
  navigate('/login');
}
