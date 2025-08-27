import * as React from 'react';
import { IconButton, Menu, MenuItem, Avatar } from '@mui/material';
import LogoutIcon from '@mui/icons-material/Logout';

interface AdminAvatarMenuProps {
  onLogout: () => void;
}
const AdminAvatarMenu: React.FC<AdminAvatarMenuProps> = ({ onLogout }) => {
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);

  const handleMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    handleClose();
    onLogout();
  };

  return (
    <>
      <IconButton onClick={handleMenu} size="large" sx={{ ml: 2 }}>
        <Avatar sx={{ bgcolor: '#e50914' }} />
      </IconButton>
      <Menu
        anchorEl={anchorEl}
        open={open}
        onClose={handleClose}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
        transformOrigin={{ vertical: 'top', horizontal: 'right' }}
      >
        <MenuItem onClick={handleLogout} sx={{ color: '#e50914', fontWeight: 700 }}>
          <LogoutIcon sx={{ mr: 1 }} /> Log Out
        </MenuItem>
      </Menu>
    </>
  );
};

export default AdminAvatarMenu;
