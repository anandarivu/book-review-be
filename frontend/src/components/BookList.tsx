import React from 'react';
import { List, ListItem, Box, IconButton, Typography } from '@mui/material';
import Rating from '@mui/material/Rating';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import BookReviewsDialog from '../components/BookReviewsDialog';
import { getReviewsForBook } from '../api/reviews';

interface BookListProps {
  books: any[];
  onEdit: (book: any) => void;
  onDelete: (id: string) => void;
  onOpenReviews: (id: string) => void;
  selectedBookId: string | null;
  reviewsDialogOpen: boolean;
  setReviewsDialogOpen: (open: boolean) => void;
}

const BookList: React.FC<BookListProps> = ({ books, onEdit, onDelete, selectedBookId, reviewsDialogOpen, setReviewsDialogOpen }) => (
  <>
    <List>
      {books.map(book => (
        <ListItem key={book.id} sx={{ py: 2 }}>
          <Box display="flex" alignItems="center" width="100%" gap={3}>
            {/* Book cover image on the left */}
            {book.coverImageUrl && (
              <Box minWidth={60} maxWidth={60} display="flex" alignItems="center" justifyContent="center">
                <img src={book.coverImageUrl} alt={book.title} style={{ width: 60, height: 90, objectFit: 'cover', borderRadius: 4, boxShadow: '0 2px 8px rgba(0,0,0,0.15)' }} />
              </Box>
            )}
            {/* Book info and actions on the right */}
            <Box display="flex" flex={1} alignItems="center" justifyContent="space-between" gap={2}>
              <Box>
                <Typography variant="h6" color="error" fontWeight={700} sx={{ wordBreak: 'break-word' }}>
                  {book.title}
                </Typography>
                <Typography variant="body2" color="white" sx={{ mb: 1 }}>
                  by {book.author}
                </Typography>
                <Box display="flex" alignItems="center" gap={1}>
                  <Rating value={book.averageRating || 0} precision={0.1} readOnly size="small" sx={{ mr: 1 }} />
                  <Typography color="primary" fontWeight={700}>
                    {book.averageRating ? book.averageRating.toFixed(1) : 'N/A'}
                  </Typography>
                </Box>
              </Box>
              <Box display="flex" alignItems="center" gap={1}>
                <IconButton color="success" onClick={() => onEdit(book)}>
                  <EditIcon />
                </IconButton>
                <IconButton color="error" onClick={() => onDelete(book.id)}>
                  <DeleteIcon />
                </IconButton>
              </Box>
            </Box>
          </Box>
        </ListItem>
      ))}
    </List>
    <BookReviewsDialog
      open={reviewsDialogOpen}
      onClose={() => setReviewsDialogOpen(false)}
      bookId={selectedBookId || ''}
      getReviewsForBook={getReviewsForBook}
    />
  </>
);

export default BookList;
