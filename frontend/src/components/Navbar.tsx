import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { AppBar, Toolbar, Typography, Avatar, IconButton, Menu, MenuItem, Box } from '@mui/material';

function getInitials(name: string) {
  if (!name) return '';
  const parts = name.split(' ');
  return parts.map(p => p[0]).join('').toUpperCase().slice(0, 2);
}

const Navbar: React.FC<{ username?: string }> = ({ username }) => {
  const navigate = useNavigate();
  const { logout } = useAuthStore();
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);

  const handleMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };
  const handleMenuClose = () => {
    setAnchorEl(null);
  };
  const handleProfile = () => {
    navigate('/profile');
    handleMenuClose();
  };
  const handleLogout = () => {
    logout();
    navigate('/login');
    handleMenuClose();
  };

  return (
    <AppBar position="static" sx={{ bgcolor: 'black', borderBottom: '2px solid #e50914' }} elevation={0}>
      <Toolbar sx={{ display: 'flex', justifyContent: 'space-between', px: 3 }}>
        <Typography variant="h5" fontWeight={900} color="error" sx={{ cursor: 'pointer' }} onClick={() => navigate('/books')}>
          BookFlix
        </Typography>
        <Box display="flex" alignItems="center" gap={2}>
          <IconButton onClick={handleMenuOpen} size="large" sx={{ p: 0 }}>
            <Avatar sx={{ bgcolor: '#e50914', color: 'white', fontWeight: 700 }}>
              {getInitials(username || '')}
            </Avatar>
          </IconButton>
          <Menu
            anchorEl={anchorEl}
            open={open}
            onClose={handleMenuClose}
            anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
            transformOrigin={{ vertical: 'top', horizontal: 'right' }}
            PaperProps={{ sx: { bgcolor: '#181818', color: 'white', minWidth: 140, border: '1px solid #e50914' } }}
          >
            <MenuItem onClick={handleProfile} sx={{ color: 'white', fontWeight: 500 }}>Profile</MenuItem>
            <MenuItem onClick={handleLogout} sx={{ color: 'white', fontWeight: 500 }}>Logout</MenuItem>
          </Menu>
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;
