import React from 'react';
import { Box, TextField, Button, InputAdornment, IconButton } from '@mui/material';
import SaveIcon from '@mui/icons-material/Save';
import CloseIcon from '@mui/icons-material/Close';
import { Add } from '@mui/icons-material';

interface SearchBarProps {
  searchInput: string;
  onSearchInputChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  searchInputRef: React.RefObject<HTMLInputElement>;
  onAddBook: () => void;
}

const SearchBar: React.FC<SearchBarProps> = ({ searchInput, onSearchInputChange, searchInputRef, onAddBook }) => (
  <Box display="flex" alignItems="center" mb={3}>
    <TextField
      label="Search books..."
      variant="outlined"
      value={searchInput}
      inputRef={searchInputRef}
      onChange={onSearchInputChange}
      fullWidth
      sx={{
        mr: 2,
        bgcolor: '#181818',
        borderRadius: 3,
        boxShadow: 2,
        input: { color: 'white', fontSize: 18, padding: '12px 14px' },
        '& .MuiOutlinedInput-root': {
          borderRadius: 3,
          background: '#181818',
          boxShadow: '0 2px 8px rgba(0,0,0,0.10)',
          transition: 'box-shadow 0.2s',
          '&:hover': { boxShadow: '0 4px 16px rgba(229,9,20,0.10)' },
          '&.Mui-focused': {
            boxShadow: '0 0 0 2px #e50914',
            borderColor: '#e50914',
          },
        },
      }}
      InputProps={{
        endAdornment: searchInput ? (
          <InputAdornment position="end">
            <IconButton
              aria-label="clear search"
              onClick={() => {
                if (searchInputRef.current) searchInputRef.current.value = '';
                onSearchInputChange({ target: { value: '' } } as any);
              }}
              edge="end"
              size="small"
              sx={{ color: 'white' }}
            >
              <CloseIcon />
            </IconButton>
          </InputAdornment>
        ) : null,
        style: { color: 'white' },
      }}
      InputLabelProps={{ style: { color: '#e50914', fontWeight: 700, fontSize: 16 } }}
    />
    <Button variant="contained" color="error" startIcon={<Add />} sx={{ fontWeight: 700, height: 48, borderRadius: 3, boxShadow: 2, ml: 1 }} onClick={onAddBook}>
      Add
    </Button>
  </Box>
);

export default SearchBar;
