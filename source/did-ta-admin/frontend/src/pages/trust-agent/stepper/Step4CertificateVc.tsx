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

const Step4CertificateVC: React.FC<Props> = ({ step, onRegister }) => {
  const [dn, setDn] = useState('');
  const [vcJson, setVcJson] = useState('');
  const [error, setError] = useState<string | undefined>(undefined);
  const [isCreated, setIsCreated] = useState(false);
  const [isBlockchainRegistered, setIsBlockchainRegistered] = useState(false);

  const validateDn = (value: string) => {
    if (!value.trim()) return 'DN is required.';
    if (value.length < 4 || value.length > 100) return 'DN must be between 4 and 100 characters.';
    return undefined;
  };

  const handleGenerateCertificate = () => {
    const validationMessage = validateDn(dn);
    if (validationMessage) {
      setError(validationMessage);
      setIsCreated(false);
      return;
    }

    setError(undefined);
    const json = JSON.stringify({
      type: 'CertificateVC',
      subject: {
        dn,
        issuedAt: new Date().toISOString(),
      },
    }, null, 2);

    setVcJson(json);
    setIsCreated(true);
  };

  const handleRegisterBlockchain = () => {
    setIsBlockchainRegistered(true);
  };

  const validate = () => {
    return isBlockchainRegistered;
  };

  const afterValidate = async () => {
    console.log('Step4 afterValidate: Password ready for next step');
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
        Step 4 – Issue Certificate VC
      </Typography>
      <StyledDescription>
        <Typography variant="body1">
          In this step, you will issue a Certificate VC for the Trust Agent.
          This credential is required for trusted communication between entities.
        </Typography>
        <Typography variant="body1" sx={{ mt: 1 }}>
          Unlike other entities, the TA issues its own Certificate VC. 
          This self-signed credential proves that the TA has been formally registered as an Entity within the OpenDID system.
        </Typography>
      </StyledDescription>

      {/* Generate Certificate VC */}
      <Card variant="outlined" sx={{ mb: 4 }}>
        <CardContent>
          <Typography variant="subtitle1" gutterBottom>
            Step 1. Generate Certificate VC
          </Typography>

          <TextField
            fullWidth
            label="DN"
            variant="outlined"
            value={dn}
            onChange={(e) => {
              setDn(e.target.value);
              setIsCreated(false);
              setIsBlockchainRegistered(false);
            }}
            error={!!error}
            helperText={error}
            sx={{ mb: 2 }}
          />

          <Button variant="contained" onClick={handleGenerateCertificate}>
            Generate
          </Button>

          {isCreated && (
            <>
              <TextField
                fullWidth
                multiline
                minRows={6}
                margin="normal"
                value={vcJson}
                label="Certificate VC JSON"
                InputProps={{ readOnly: true }}
              />
              <Typography variant="body2" color="success.main">
                ✅ Certificate VC has been successfully created.
              </Typography>
            </>
          )}
        </CardContent>
      </Card>

      {/* Register to Blockchain */}
      {isCreated && (
        <Card variant="outlined">
          <CardContent>
            <Typography variant="subtitle1" gutterBottom>
              Step 2. Register to Blockchain
            </Typography>
            <Button variant="contained" onClick={handleRegisterBlockchain}>
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

export default Step4CertificateVC;
