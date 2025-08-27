import React, { useState } from 'react';
import { Button, TextField, Typography, Alert } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';


const AdminSignup: React.FC = () => {
  const [userId, setUserId] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/auth/admin/signup', { userId, email, password });
      setSuccess('Admin account created successfully!');
      setError('');
      setTimeout(() => navigate('/admin/login'), 1500);
    } catch (err: unknown) {
      setError('Admin registration failed');
      setSuccess('');
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 18 }}>
      <Typography variant="h5" fontWeight={900} align="center" color="error" gutterBottom>
        Admin Signup
      </Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}
      <TextField
        label="Admin Username"
        variant="filled"
        value={userId}
        onChange={e => setUserId(e.target.value)}
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
        Signup
      </Button>
    </form>
  );
};

export default AdminSignup;
