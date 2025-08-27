import React, { useState } from 'react';
import { Button, TextField, Typography, Alert } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import { useAuthStore } from '../store/authStore';

const UserLogin: React.FC = () => {
  const [userId, setUserId] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { setAuth } = useAuthStore();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const res = await api.post('/auth/login', { userId, password });
      localStorage.setItem('token', res.data.token);
      setAuth(res.data.token, res.data.role);
      navigate('/books');
    } catch (err: unknown) {
      setError('Invalid credentials');
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 18 }}>
      <Typography variant="h5" fontWeight={900} align="center" color="error" gutterBottom>
        User Login
      </Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      <TextField
        label="Username"
        variant="filled"
        value={userId}
        onChange={e => setUserId(e.target.value)}
        fullWidth
        InputProps={{ style: { backgroundColor: 'black', color: 'white' } }}
        InputLabelProps={{ style: { color: '#e50914' } }}
        sx={{ mb: 2 }}
      />
      <TextField
        label="Password"
        type="password"
        variant="filled"
        value={password}
        onChange={e => setPassword(e.target.value)}
        fullWidth
        InputProps={{ style: { backgroundColor: 'black', color: 'white' } }}
        InputLabelProps={{ style: { color: '#e50914' } }}
        sx={{ mb: 2 }}
      />
      <Button type="submit" variant="contained" color="error" size="large" sx={{ fontWeight: 700, mb: 1 }}>
        Login
      </Button>
      <Button variant="text" color="error" sx={{ fontWeight: 700, mb: 1 }} onClick={() => navigate('/register')}>
        Create Account
      </Button>
    </form>
  );
};

export default UserLogin;
