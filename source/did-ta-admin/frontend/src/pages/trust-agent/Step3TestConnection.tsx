import React, { useState } from 'react';
import { Button, Typography } from '@mui/material';

const Step3TestConnection: React.FC = () => {
  const [isServerValid, setIsServerValid] = useState(false);

  const handleTest = () => {
    setIsServerValid(true);
  };

  return (
    <>
      <Typography>Click the button below to test server connection:</Typography>
      <Button variant="outlined" onClick={handleTest} sx={{ mt: 2 }}>
        Test Connection
      </Button>
      {isServerValid && (
        <Typography sx={{ mt: 2 }} color="success.main">
          Connection successful!
        </Typography>
      )}
    </>
  );
};

export default Step3TestConnection;
