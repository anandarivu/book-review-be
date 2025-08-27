
import React, { useState } from 'react';
import api from '../api/axios';
import { useNavigate } from 'react-router-dom';
import { Box, Button, TextField, Typography, Paper, Alert } from '@mui/material';

const Register: React.FC = () => {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/auth/signup', { userId: username, email, password });
      navigate('/login');
    } catch (err: unknown) {
      setError('Registration failed');
    }
  };

  return (
    <Box minHeight="100vh" display="flex" alignItems="center" justifyContent="center" sx={{ backgroundColor: 'black' }}>
      <Paper elevation={8} sx={{ p: 5, bgcolor: '#181818', border: '2px solid #e50914', maxWidth: 400, width: '100%' }}>
        <Typography variant="h4" fontWeight={900} align="center" color="error" gutterBottom>
          Create Account
        </Typography>
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 18 }}>
          <TextField
            label="Username"
            variant="filled"
            value={username}
            onChange={e => setUsername(e.target.value)}
            fullWidth
            InputProps={{ style: { backgroundColor: 'black', color: 'white' } }}
            InputLabelProps={{ style: { color: '#e50914' } }}
            sx={{ mb: 2 }}
          />
          <TextField
            label="Email"
            type="email"
            variant="filled"
            value={email}
            onChange={e => setEmail(e.target.value)}
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
            Register
          </Button>
        </form>
        <Box mt={2} textAlign="center">
          <Typography variant="body2" color="gray">
            Already have an account?
            <Button variant="text" color="error" sx={{ ml: 1 }} onClick={() => navigate('/login')}>
              Login
            </Button>
          </Typography>
        </Box>
      </Paper>
    </Box>
  );
};

export default Register;
