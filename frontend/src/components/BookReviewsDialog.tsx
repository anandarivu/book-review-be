import * as React from 'react';
import { Dialog, DialogTitle, DialogContent, IconButton, Typography, CircularProgress, Paper, Box, Rating } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';

interface Review {
  id: string;
  title: string;
  reviewText: string;
  rating: number;
  userId: string;
  createdAt?: string;
}

interface BookReviewsDialogProps {
  open: boolean;
  onClose: () => void;
  bookId: string;
  getReviewsForBook: (bookId: string) => Promise<{ data: { content: Review[] } }>;
}

const BookReviewsDialog: React.FC<BookReviewsDialogProps> = ({ open, onClose, bookId, getReviewsForBook }) => {
  const [reviews, setReviews] = React.useState<Review[]>([]);
  const [loading, setLoading] = React.useState(false);

  React.useEffect(() => {
    if (open) {
      setLoading(true);
      getReviewsForBook(bookId)
        .then(res => setReviews(res.data.content || res.data))
        .finally(() => setLoading(false));
    }
  }, [open, bookId, getReviewsForBook]);

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <Box display="flex" alignItems="center" justifyContent="space-between" px={2} pt={2}>
        <DialogTitle sx={{ p: 0 }}>Book Reviews</DialogTitle>
        <IconButton onClick={onClose} size="large" color="error">
          <CloseIcon />
        </IconButton>
      </Box>
      <DialogContent>
        {loading ? (
          <CircularProgress color="error" />
        ) : reviews.length === 0 ? (
          <Typography color="textSecondary">No reviews found.</Typography>
        ) : (
          <Box display="flex" flexDirection="column" gap={2}>
            {reviews.map(review => (
              <Paper key={review.id} sx={{ bgcolor: '#222', p: 2, border: '1px solid #e50914' }}>
                <Box display="flex" alignItems="center" gap={2} mb={1}>
                  <Rating value={review.rating} readOnly precision={0.1} size="small" sx={{ color: '#e50914' }} />
                  <Typography color="error" fontWeight={700}>
                    by {review.userId}
                  </Typography>
                  {review.createdAt && (
                    <Typography color="gray" fontSize={13}>
                      on {new Date(review.createdAt).toLocaleDateString()}
                    </Typography>
                  )}
                </Box>
                <Typography fontWeight={700} color="white" mb={0.5}>
                  Title: {review.title}
                </Typography>
                <Typography color="white">
                  Description: {review.reviewText}
                </Typography>
              </Paper>
            ))}
          </Box>
        )}
      </DialogContent>
    </Dialog>
  );
};

export default BookReviewsDialog;
