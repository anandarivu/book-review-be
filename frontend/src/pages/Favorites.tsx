import React, { useEffect, useState } from 'react';
import { getFavorites, removeFavorite } from '../api/favorites';
import { Box, Typography, Paper, List, ListItem, ListItemText, IconButton, CircularProgress } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';

const Favorites: React.FC = () => {
  const [favorites, setFavorites] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getFavorites().then(res => {
      setFavorites(res.data);
      setLoading(false);
    });
  }, []);

  const handleRemoveFavorite = async (bookId: number) => {
    await removeFavorite(bookId);
    getFavorites().then(res => setFavorites(res.data));
  };

  if (loading)
    return (
      <Box display="flex" alignItems="center" justifyContent="center" minHeight="60vh">
        <CircularProgress color="error" />
      </Box>
    );

  if (!favorites.length)
    return (
      <Box display="flex" alignItems="center" justifyContent="center" minHeight="40vh">
        <Typography color="gray">No favorites yet.</Typography>
      </Box>
    );

  return (
    <Box maxWidth="sm" mx="auto" py={8}>
      <Paper elevation={8} sx={{ bgcolor: '#181818', p: 4, border: '2px solid #e50914' }}>
        <Typography variant="h4" fontWeight={900} color="error" gutterBottom>
          My Favorites
        </Typography>
        <List>
          {favorites.map(book => (
            <ListItem key={book.id} secondaryAction={
              <IconButton edge="end" color="error" onClick={() => handleRemoveFavorite(book.id)}>
                <DeleteIcon />
              </IconButton>
            }>
              <ListItemText primary={book.title} primaryTypographyProps={{ color: 'white' }} />
            </ListItem>
          ))}
        </List>
      </Paper>
    </Box>
  );
};

export default Favorites;
// ...existing code...
