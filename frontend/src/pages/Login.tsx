
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import { useAuthStore } from '../store/authStore';
import { Box, Button, TextField, Typography, Paper, Alert } from '@mui/material';

const Login: React.FC = () => {
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
    <Box minHeight="100vh" display="flex" alignItems="center" justifyContent="center" sx={{ backgroundColor: 'black' }}>
      <Paper elevation={8} sx={{ p: 5, bgcolor: '#181818', border: '2px solid #e50914', maxWidth: 400, width: '100%' }}>
        <Typography variant="h4" fontWeight={900} align="center" color="error" gutterBottom>
          Sign In
        </Typography>
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 18 }}>
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
        </form>
        <Box mt={2} textAlign="center">
          <Typography variant="body2" color="gray">
            Don't have an account?
            <Button variant="text" color="error" sx={{ ml: 1 }} onClick={() => navigate('/register')}>
              Create Account
            </Button>
          </Typography>
        </Box>
      </Paper>
    </Box>
  );
};

export default Login;
