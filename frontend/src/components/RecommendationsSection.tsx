import React, { useState } from 'react';
import { Box, Typography, Grid, Pagination } from '@mui/material';
import StarBorderIcon from '@mui/icons-material/StarBorder';
import BookCard from '../components/BookCard';

const RecommendationsSection: React.FC<{
  recommendations: any[];
  favorites: any[];
  itemsPerPage: number;
  recPage: number;
  setRecPage: (page: number) => void;
  handleAddFavorite: (bookId: string) => void;
  handleRemoveFavorite: (bookId: string) => void;
}> = ({ recommendations, favorites, itemsPerPage, recPage, setRecPage, handleAddFavorite, handleRemoveFavorite }) => {
  return (
    <>
      <Box mt={8} mb={4}>
        <Typography variant="h4" fontWeight={900} color="error" align="left" gutterBottom>
          Recommendations
        </Typography>
      </Box>
      {recommendations.length === 0 ? (
        <Box display="flex" flexDirection="column" alignItems="center" justifyContent="center" minHeight="300px" border="2px dashed #e50914" borderRadius={4} bgcolor="#181818" sx={{ boxShadow: 8 }}>
          <StarBorderIcon sx={{ fontSize: 60, color: 'white', mb: 2 }} />
          <Typography variant="h6" color="error" fontWeight={700} gutterBottom>
            No recommendations yet
          </Typography>
          <Typography variant="body2" color="gray" align="center">
            Book recommendations will appear here based on your activity.
          </Typography>
        </Box>
      ) : (
        <>
          <Grid container spacing={4}>
            {recommendations.slice((recPage - 1) * itemsPerPage, recPage * itemsPerPage).map(book => (
              <Grid item key={book.id} xs={12} sm={6} md={4} lg={3}>
                <BookCard
                  book={book}
                  isFavorite={favorites.some(fav => fav.id === book.id)}
                  onFavorite={() => {
                    if (favorites.some(fav => fav.id === book.id)) {
                      handleRemoveFavorite(book.id);
                    } else {
                      handleAddFavorite(book.id);
                    }
                  }}
                  showFavorite={true}
                  showViewDetails={true}
                />
              </Grid>
            ))}
          </Grid>
          {recommendations.length > itemsPerPage && (
            <Box display="flex" justifyContent="center" mt={3}>
              <Pagination
                count={Math.ceil(recommendations.length / itemsPerPage)}
                page={recPage}
                onChange={(_event: React.ChangeEvent<unknown>, value: number) => setRecPage(value)}
                color="primary"
              />
            </Box>
          )}
        </>
      )}
    </>
  );
};

export default RecommendationsSection;
