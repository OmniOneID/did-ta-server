import React from 'react';
import { Button, Typography, Box } from '@mui/material';

const Step4Confirm: React.FC = () => {
  const handleConfirm = () => {
    console.log('Confirmed');
  };

  return (
    <Box>
      <Typography variant="subtitle1" gutterBottom>
        Please confirm and finish the registration.
      </Typography>
      <Button variant="contained" color="primary" onClick={handleConfirm}>
        Confirm
      </Button>
    </Box>
  );
};

export default Step4Confirm;
