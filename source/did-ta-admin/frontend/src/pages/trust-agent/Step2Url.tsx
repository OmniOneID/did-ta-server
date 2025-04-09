import React, { useState } from 'react';
import { TextField } from '@mui/material';


const Step2Url: React.FC = () => {
  const [serverUrl, setServerUrl] = useState('');
  
  return (
    <TextField
      fullWidth
      label="Server URL"
      value={serverUrl}
      onChange={(e) => setServerUrl(e.target.value)}
    />
  );
};

export default Step2Url;
