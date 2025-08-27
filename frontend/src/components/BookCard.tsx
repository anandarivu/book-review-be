import React from 'react';
import { Card, CardContent, CardMedia, Box, Typography, Button, IconButton, Rating } from '@mui/material';
import './BookCard.css';
import { Link } from 'react-router-dom';
import FavoriteIcon from '@mui/icons-material/Favorite';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import DeleteIcon from '@mui/icons-material/Delete';

interface BookCardProps {
  book: any;
  isFavorite?: boolean;
  onFavorite?: (bookId: string, isFav: boolean) => void;
  onDelete?: (bookId: string) => void;
  showFavorite?: boolean;
  showDelete?: boolean;
  showViewDetails?: boolean;
}

const BookCard: React.FC<BookCardProps> = ({
  book,
  isFavorite = false,
  onFavorite,
  onDelete,
  showFavorite = false,
  showDelete = false,
  showViewDetails = true,
}) => (
  <Card
    className="book-card-hover"
    sx={{
      bgcolor: '#181818',
      border: '2px solid #e50914',
      borderRadius: 3,
      boxShadow: 8,
      width: 260,
      height: 420,
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'space-between',
    }}
  >
    {book.coverImageUrl && (
      <CardMedia
        component="img"
        image={book.coverImageUrl}
        alt={book.title}
        sx={{ height: 180, objectFit: 'cover', borderRadius: 2, mb: 2 }}
      />
    )}
    <CardContent sx={{ flexGrow: 1 }}>
      <Box display="flex" alignItems="center" gap={1}>
        <Typography variant="h6" color="error" fontWeight={700} gutterBottom sx={{ wordBreak: 'break-word' }}>
          {book.title}
        </Typography>
        {showDelete && (
          <IconButton edge="end" color="error" onClick={() => onDelete && onDelete(book.id)}>
            <DeleteIcon />
          </IconButton>
        )}
        {showFavorite && (
          <IconButton
            color={isFavorite ? 'error' : 'default'}
            onClick={() => onFavorite && onFavorite(book.id, isFavorite)}
            sx={{ ml: 1 }}
          >
            {isFavorite ? <FavoriteIcon /> : <FavoriteBorderIcon />}
          </IconButton>
        )}
      </Box>
      <Box display="flex" alignItems="center" mb={1}>
        <Rating value={book.averageRating || 0} precision={0.1} readOnly size="small" sx={{ mr: 1 }} />
        <Typography color="primary" fontWeight={700}>
          {book.averageRating ? book.averageRating.toFixed(1) : 'N/A'}
        </Typography>
        {book.reviewCount > 0 && (
          <Typography color="gray" fontWeight={500} sx={{ ml: 1, fontSize: 14 }}>
            ({book.reviewCount} reviews)
          </Typography>
        )}
      </Box>
      <Typography variant="body2" color="white" gutterBottom>
        by <span style={{ fontWeight: 500 }}>{book.author}</span> in <span style={{ fontWeight: 500 }}>{book.publishedYear}</span>
      </Typography>
      {book.genres && book.genres.length > 0 && (
        <Typography variant="caption" color="error" display="block" gutterBottom>
          Genres: {book.genres.join(', ')}
        </Typography>
      )}
    </CardContent>
    {showViewDetails && (
      <Box px={2} pb={2} sx={{ mt: 'auto' }}>
        <Button
          component={Link}
          to={`/books/${book.id}`}
          variant="contained"
          color="error"
          fullWidth
          sx={{ fontWeight: 700 }}
        >
          View Details
        </Button>
      </Box>
    )}
  </Card>
);

export default BookCard;
