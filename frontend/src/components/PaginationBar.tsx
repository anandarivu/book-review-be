import React from 'react';
import { Box, Button, Typography } from '@mui/material';

interface PaginationBarProps {
  page: number;
  totalPages: number;
  onPrev: () => void;
  onNext: () => void;
}

const PaginationBar: React.FC<PaginationBarProps> = ({ page, totalPages, onPrev, onNext }) => (
  <Box display="flex" justifyContent="center" alignItems="center" mt={2}>
    <Button
      disabled={page === 0}
      onClick={onPrev}
      sx={{ mr: 2 }}
      variant="outlined"
      color="error"
    >
      Previous
    </Button>
    <Typography color="white" fontWeight={700} mx={2}>
      Page {page + 1} of {totalPages}
    </Typography>
    <Button
      disabled={page + 1 >= totalPages}
      onClick={onNext}
      sx={{ ml: 2 }}
      variant="outlined"
      color="error"
    >
      Next
    </Button>
  </Box>
);

export default PaginationBar;
