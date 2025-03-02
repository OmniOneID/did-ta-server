import { Button, Stack } from '@mui/material';
import { useDialogs } from '@toolpad/core/useDialogs';
import { useState } from 'react';
import { Navigate, useNavigate } from 'react-router';
import CustomDialog from '../../components/dialog/CustomDialog';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import { useServerStatus } from '../../context/ServerStatusContext';
import { postData } from '../../utils/api';

const TrustAgentRegisterPage = () => {
  const navigate = useNavigate();
  const { setServerStatus, setTaInfo, serverStatus } = useServerStatus();
  const [isError, setIsError] = useState<boolean>(false);
  const dialogs = useDialogs();
  const [isLoading, setIsLoading] = useState(false);

  const API_BASE_URL = "/tas/admin/v1";

  const handleSimpleRegistration = async () => {
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
  };


  if (serverStatus === 'COMPLETED') {
    return <Navigate to="/ta-management" replace />;
  }

  return (
    <>
        <FullscreenLoader open={isLoading} />
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
    </>

  );
};

export default TrustAgentRegisterPage;
