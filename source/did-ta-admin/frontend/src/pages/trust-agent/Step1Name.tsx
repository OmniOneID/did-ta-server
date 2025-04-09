import React, { useState } from 'react';
import { TextField, Typography } from '@mui/material';

const Step1Name: React.FC = () => {
  const [name, setName] = useState('');

  return (
    <>
      <Typography variant="subtitle1" gutterBottom>Enter your service name</Typography>
      <TextField
        fullWidth
        label="Service Name"
        value={name}
        onChange={(e) => setName(e.target.value)}
      />
    </>
  );
};

export default Step1Name;