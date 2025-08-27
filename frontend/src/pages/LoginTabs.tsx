import React, { useState } from 'react';
import { Box, Tabs, Tab, Paper } from '@mui/material';
import UserLogin from './UserLogin';
import AdminLogin from './AdminLogin';

const LoginTabs: React.FC = () => {
  const [tab, setTab] = useState(0);

  return (
    <Box minHeight="100vh" display="flex" alignItems="center" justifyContent="center" sx={{ backgroundColor: 'black' }}>
      <Paper elevation={8} sx={{ p: 5, bgcolor: '#181818', border: '2px solid #e50914', maxWidth: 400, width: '100%' }}>
        <Tabs
          value={tab}
          onChange={(_, newValue) => setTab(newValue)}
          centered
          textColor="primary"
          indicatorColor="primary"
          sx={{ mb: 3 }}
        >
          <Tab label="User Login" sx={{ color: tab === 0 ? '#e50914' : 'white', fontWeight: 700 }} />
          <Tab label="Admin Login" sx={{ color: tab === 1 ? '#e50914' : 'white', fontWeight: 700 }} />
        </Tabs>
        {tab === 0 ? <UserLogin /> : <AdminLogin />}
      </Paper>
    </Box>
  );
};

export default LoginTabs;
