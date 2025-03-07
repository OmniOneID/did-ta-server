import { Box, Button, Stack, Typography } from '@mui/material';
import { useDialogs } from '@toolpad/core/useDialogs';
import { useState } from 'react';
import { Navigate, useNavigate } from 'react-router';
import CustomDialog from '../../components/dialog/CustomDialog';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import { useServerStatus } from '../../context/ServerStatusContext';
import { postData } from '../../utils/api';
import CustomConfirmDialog from '../../components/dialog/CustomConfirmDialog';

const TrustAgentRegisterPage = () => {
  const navigate = useNavigate();
  const { setServerStatus, setTaInfo, serverStatus } = useServerStatus();
  const [isError, setIsError] = useState<boolean>(false);
  const dialogs = useDialogs();
  const [isLoading, setIsLoading] = useState(false);

  const API_BASE_URL = "/tas/admin/v1";

  const handleSimpleRegistration = async () => {
    const result = await dialogs.open(CustomConfirmDialog, {
      title: 'Confirmation',
      message: 'Are you sure you want to register Trust Agent?',
      isModal: true,
    });

    if (result) {
      setIsLoading(true);
      setIsError(false);

      try {
        const { data } = await postData(API_BASE_URL, 'ta/register-simple', null);
        setServerStatus(data.status);
        setTaInfo(data);
  
        if (data.status === 'COMPLETED') {
  
          await dialogs.open(CustomDialog, {
            title: 'Notification',
            message: `Registration completed successfully.`,
            isModal: true,
          },{
            onClose: async (result) =>  navigate('/ta-management'),
          });
        }
      } catch (err: any) {
        setIsLoading(false);
        await dialogs.open(CustomDialog, {
          title: 'Notification',
          message: `Failed to register TA: ${err.message}`,
          isModal: true,
        });
  
        setIsError(true);
      } finally {
        setIsLoading(false);
      }
    }
  };

  if (serverStatus === 'COMPLETED') {
    return <Navigate to="/ta-management" replace />;
  }

  return (
    <>
        <FullscreenLoader open={isLoading} />
        <Box sx={{ p: 3 }}>
          <Typography variant="h4">Trust Agent Quick Registration</Typography>

          <Box sx={{ maxWidth: 500, margin: 'auto', p: 3, marginTop: 3, border: '1px solid #ccc', borderRadius: 2 }}>
            <Typography variant="body1">
              This is a <strong>temporary registration page</strong> for the Trust Agent.
            </Typography>
            <Typography variant="body1" sx={{ mt: 1 }}>
              A more detailed registration page will be updated in the second phase of development, scheduled for April.
            </Typography>
          </Box>

          <Box sx={{ maxWidth: 800, margin: 'auto', mt: 2, p: 3, border: '1px solid #ccc', borderRadius: 2 }}>
            <Stack direction="row" spacing={2}>
              <Button variant="contained" color="primary" onClick={handleSimpleRegistration}>
                Quick Register
              </Button>

              {isError && (
                <Button variant="contained" color="error" onClick={handleSimpleRegistration}>
                  Retry
                </Button>
              )}
            </Stack>
          </Box>
        </Box>
        
    </>

  );
};

export default TrustAgentRegisterPage;
