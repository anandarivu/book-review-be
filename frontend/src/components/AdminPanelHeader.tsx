import React from 'react';
import { Box } from '@mui/material';
import AdminAvatarMenu from './AdminAvatarMenu';

interface AdminPanelHeaderProps {
  onLogout: () => void;
}

const AdminPanelHeader: React.FC<AdminPanelHeaderProps> = ({ onLogout }) => (
  <Box display="flex" justifyContent="flex-end" alignItems="center" mb={1}>
    <AdminAvatarMenu onLogout={onLogout} />
  </Box>
);

export default AdminPanelHeader;
