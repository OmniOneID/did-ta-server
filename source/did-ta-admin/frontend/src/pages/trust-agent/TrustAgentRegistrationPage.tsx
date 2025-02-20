import { useState } from 'react';
import { postData } from '../../utils/api';
import { useServerStatus } from '../../context/ServerStatusContext';
import { useNavigate } from 'react-router';
import { Button, Typography } from '@mui/material';

const TrustAgentRegisterPage = () => {
  const navigate = useNavigate();
  const { setIsLoading, setServerStatus, setTaInfo } = useServerStatus();
  const [error, setError] = useState<string | null>(null);

  const handleSimpleRegistration = async () => {
    setIsLoading(true);
    setError(null);

    try {
      const { data } = await postData('ta/register-simple', null);
      setServerStatus(data.status);
      setTaInfo(data);

      if (data.status === 'COMPLETED') {
        navigate('/ta-management');
      }
    } catch (err: any) {
      console.error('Failed to register TA:', err);
      setError(err.message || 'Failed to register TA');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div>
      <Button variant="contained" color="primary" onClick={handleSimpleRegistration}>
        TA Simple Registration
      </Button>

      {error && (
        <div>
          <Typography sx={{ color: 'red' }}>{error}</Typography>
          <Button variant="contained" color="error" onClick={handleSimpleRegistration}>
            Retry
          </Button>
        </div>
      )}
    </div>
  );
};

export default TrustAgentRegisterPage;
