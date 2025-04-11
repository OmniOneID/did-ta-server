import React, { useEffect, useMemo, useState } from 'react';
import {
  Box,
  Button,
  TextField,
  Typography,
  Card,
  CardContent,
  styled,
} from '@mui/material';
import { generateTaDidDocument, registerTaDidDocument } from '../../../apis/ta-api';
import { useDialogs } from '@toolpad/core';
import CustomDialog from '../../../components/dialog/CustomDialog';
import { formatErrorMessage } from '../../../utils/error-handler';

interface Props {
  step: number;
  onRegister: (step: number, validate: () => boolean, afterValidate?: () => Promise<void>) => void;
  setIsLoading: (loading: boolean) => void;
}

const Step3DIDDocument: React.FC<Props> = ({ step, onRegister, setIsLoading }) => {
  const [didDocument, setDidDocument] = useState<string>('');
  const [isDidGenerated, setIsDidGenerated] = useState<boolean>(false);
  const [isBlockchainRegistered, setIsBlockchainRegistered] = useState<boolean>(false);
  const dialogs = useDialogs();
  
  const handleGenerateDid = async () => {
    setIsLoading(true);
    setIsDidGenerated(false);
    setDidDocument('');

    await generateTaDidDocument()
      .then((response) => {
        setDidDocument(JSON.stringify(response.data, null, 2));
        setIsDidGenerated(true);
        setIsLoading(false);
      }).catch((error) => {
        setIsLoading(false);
        dialogs.open(CustomDialog, {
          title: 'Notification',
          message: formatErrorMessage(error, `Failed to generate DID Document`),
          isModal: true,
        });
        throw error;
      });
  };

  const handleRegisterBlockchain = async () => {
    setIsLoading(true);

    const requestBody = {
      didDocument: didDocument
    };

    await registerTaDidDocument(requestBody).then((response) => {
      setIsBlockchainRegistered(true);
      setIsLoading(false);
    }).catch((error) => {
        setIsLoading(false);
        setIsBlockchainRegistered(false);
        dialogs.open(CustomDialog, {
            title: 'Notification',
            message: formatErrorMessage(error, `Failed to register TA DID Document`),
            isModal: true,
        });
        throw error;
    });
  };

  const validate = () => {
    return isBlockchainRegistered;
  };

  const afterValidate = async () => {
    console.log('Step3 afterValidate: Password ready for next step');
  };

  useEffect(() => {
    onRegister(step, validate, afterValidate);
  }, [isBlockchainRegistered]);

  const StyledDescription = useMemo(() => styled(Box)(({ theme }) => ({
        maxWidth: 600, 
        marginTop: theme.spacing(1),
        padding: theme.spacing(0),
    })), []);

  return (
    <Box>
      <Typography variant="h6" gutterBottom>
        Step 3 – Register DID Document
      </Typography>
      <StyledDescription>
        <Typography variant="body1">
          In this step, you will create the Trust Agent’s DID Document and proceed with blockchain registration.
        </Typography>
        <Typography variant="body1" sx={{ mt: 1 }}>
          <strong>Note:</strong> Once the DID Document is registered on the blockchain, it cannot be updated or registered again.
        </Typography>
      </StyledDescription>

      {/* Step 1. Generate DID Document */}
      <Card variant="outlined" sx={{ mb: 4, mt: 1 }}>
        <CardContent>
          <Typography variant="subtitle1" gutterBottom>
            Step 1. Generate DID Document
          </Typography>
          <Button variant="contained" onClick={handleGenerateDid} sx={{ mt: 1 }}>
            Generate
          </Button>

          {isDidGenerated && (
            <>
            <Box
              sx={{
                maxHeight: 300,
                overflow: 'auto',
                backgroundColor: '#f5f5f5',
                border: '1px solid #ccc',
                borderRadius: 1,
                padding: 2,
                fontFamily: 'monospace',
                whiteSpace: 'pre-wrap',
                marginTop: 2,
                fontSize: 14,
              }}
            >
              {didDocument}
            </Box>
              <Typography variant="body2" color="success.main" sx={{ mt: 1 }}>
                ✅ DID Document has been successfully created.
              </Typography>
            </>
          )}
        </CardContent>
      </Card>

      {/* Step 2. Register to Blockchain */}
      {isDidGenerated && (
        <Card variant="outlined">
          <CardContent>
            <Typography variant="subtitle1" gutterBottom>
              Step 2. Register to Blockchain
            </Typography>
            <Button 
              variant="contained" 
              onClick={handleRegisterBlockchain} 
              sx={{ mt: 1 }}
              disabled={isBlockchainRegistered}
            >
              Register
            </Button>
            {isBlockchainRegistered && (
              <Typography variant="body2" color="success.main" sx={{ mt: 1 }}>
                ✅ Successfully registered on the blockchain.
              </Typography>
            )}
          </CardContent>
        </Card>
      )}
    </Box>
  );
};

export default Step3DIDDocument;
