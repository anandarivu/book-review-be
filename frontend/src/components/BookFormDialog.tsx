import React from 'react';
import { Dialog, DialogTitle, DialogContent, TextField, Button, Box } from '@mui/material';
import SaveIcon from '@mui/icons-material/Save';

interface BookFormDialogProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (e: React.FormEvent) => void;
  isEdit?: boolean;
  bookData: any;
  setBookData: (data: any) => void;
}

const inputSx = { mb: 2, color: 'white', 'input::placeholder': { color: 'white', opacity: 1 } };

const BookFormDialog: React.FC<BookFormDialogProps> = ({ open, onClose, onSubmit, isEdit, bookData, setBookData }) => (
  <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
    <form onSubmit={onSubmit}>
      <DialogTitle>{isEdit ? 'Edit Book' : 'Add Book'}</DialogTitle>
      <DialogContent>
        <TextField
          label="Title"
          variant="filled"
          value={bookData.title}
          onChange={e => setBookData({ ...bookData, title: e.target.value })}
          fullWidth
          InputProps={{ style: { backgroundColor: 'black', color: 'white' }, placeholder: ' ' }}
          InputLabelProps={{ style: { color: '#e50914' } }}
          sx={inputSx}
        />
        <TextField
          label="Author"
          variant="filled"
          value={bookData.author}
          onChange={e => setBookData({ ...bookData, author: e.target.value })}
          fullWidth
          InputProps={{ style: { backgroundColor: 'black', color: 'white' }, placeholder: ' ' }}
          InputLabelProps={{ style: { color: '#e50914' } }}
          sx={inputSx}
        />
        <TextField
          label="Description"
          variant="filled"
          multiline
          minRows={2}
          value={bookData.description}
          onChange={e => setBookData({ ...bookData, description: e.target.value })}
          fullWidth
          InputProps={{ style: { backgroundColor: 'black', color: 'white' }, placeholder: ' ' }}
          InputLabelProps={{ style: { color: '#e50914' } }}
          sx={inputSx}
        />
        <TextField
          label="Cover Image URL"
          variant="filled"
          value={bookData.coverImageUrl}
          onChange={e => setBookData({ ...bookData, coverImageUrl: e.target.value })}
          fullWidth
          InputProps={{ style: { backgroundColor: 'black', color: 'white' }, placeholder: ' ' }}
          InputLabelProps={{ style: { color: '#e50914' } }}
          sx={inputSx}
        />
        <TextField
          label="Genres (comma separated)"
          variant="filled"
          value={Array.isArray(bookData.genres) ? bookData.genres.join(', ') : bookData.genres}
          onChange={e => setBookData({ ...bookData, genres: Array.isArray(bookData.genres) ? e.target.value.split(',').map(g => g.trim()) : e.target.value })}
          fullWidth
          InputProps={{ style: { backgroundColor: 'black', color: 'white' }, placeholder: ' ' }}
          InputLabelProps={{ style: { color: '#e50914' } }}
          sx={inputSx}
        />
        <TextField
          label="Published Year"
          type="number"
          variant="filled"
          value={bookData.publishedYear}
          onChange={e => setBookData({ ...bookData, publishedYear: e.target.value })}
          fullWidth
          InputProps={{ style: { backgroundColor: 'black', color: 'white' }, placeholder: ' ' }}
          InputLabelProps={{ style: { color: '#e50914' } }}
          sx={inputSx}
        />
        <Box display="flex" justifyContent="flex-end" gap={2} mt={2}>
          <Button
            variant="contained"
            sx={{ bgcolor: 'white', color: 'black', fontWeight: 700, boxShadow: 'none' }}
            onClick={onClose}
          >
            Cancel
          </Button>
          <Button
            type="submit"
            variant="contained"
            sx={{ bgcolor: '#e50914', color: 'white', fontWeight: 700, boxShadow: 'none', border: '1px solid #e50914', '&:hover': { bgcolor: '#b0060f' } }}
            startIcon={<SaveIcon />}
          >
            Save
          </Button>
        </Box>
      </DialogContent>
    </form>
  </Dialog>
);

export default BookFormDialog;
