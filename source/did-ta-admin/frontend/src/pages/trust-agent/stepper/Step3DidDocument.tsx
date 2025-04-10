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

interface Props {
  step: number;
  onRegister: (step: number, validate: () => boolean, afterValidate?: () => Promise<void>) => void;
}

const Step3DIDDocument: React.FC<Props> = ({ step, onRegister }) => {
  const [didDocument, setDidDocument] = useState<string>('');
  const [isDidGenerated, setIsDidGenerated] = useState<boolean>(false);
  const [isBlockchainRegistered, setIsBlockchainRegistered] = useState<boolean>(false);

  const handleGenerateDid = () => {
    const exampleDid = JSON.stringify(
      {
        id: 'did:omni:tas:1234',
        verificationMethod: [],
        authentication: [],
      },
      null,
      2
    );
    setDidDocument(exampleDid);
    setIsDidGenerated(true);
  };

  const handleRegisterBlockchain = () => {
    setIsBlockchainRegistered(true);
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
              <TextField
                fullWidth
                multiline
                minRows={6}
                margin="normal"
                value={didDocument}
                label="DID Document JSON"
                slotProps={{ input: { readOnly: true } }}
              />
              <Typography variant="body2" color="success.main">
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
            <Button variant="contained" onClick={handleRegisterBlockchain} sx={{ mt: 1 }}>
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
