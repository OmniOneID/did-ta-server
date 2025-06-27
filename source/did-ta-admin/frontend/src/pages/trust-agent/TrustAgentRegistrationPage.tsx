import { Box, Button, Step, StepLabel, Stepper, Typography, styled } from '@mui/material';
import React, { useState } from 'react';
import { TasStatus } from '../../apis/constants/TasStatus';
import { TaInfoResDto } from '../../apis/models/TaInfoResDto';
import { getTaInfo } from '../../apis/ta-api';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import Step1TaPassword from './stepper/Step1TaPassword';
import Step2TaInfo from './stepper/Step2TaInfo';
import Step3DIDDocument from './stepper/Step3DidDocument';
import Step4CertificateVC from './stepper/Step4CertificateVc';
import StepComplete from './stepper/StepComplete';
import { useServerStatus } from '../../context/ServerStatusContext';
import { Navigate } from 'react-router';

const steps = ['Enter TA Password', 'Enter TA Info', 'Register DID Document', 'Issue Certificate VC'];

const StyledContainer = styled(Box)(({ theme }) => ({
  width: 800,
  margin: 'auto',
  marginTop: theme.spacing(1),
  padding: theme.spacing(3),
  border: 'none',
  borderRadius: theme.shape.borderRadius,
  backgroundColor: '#ffffff',
  boxShadow: '0px 4px 8px 0px #0000001A',
}));

const StyledTitle = styled(Typography)({
  textAlign: 'left',
  fontSize: '24px',
  fontWeight: 700,
});

const StyledStepperWrapper = styled(Box)({
  width: '100%',
  maxWidth: 800,
  marginLeft: 'auto',
  marginRight: 'auto',
  marginTop: 10,
});

const StyledStepper = styled(Stepper)({
  width: '100%',
});

const StyledStep = styled(Step)({});

const StyledStepLabel = styled(StepLabel)({});

const StyledContentWrapper = styled(Box)(({ theme }) => ({
  marginTop: theme.spacing(4),
}));

const StyledActionWrapper = styled(Box)({
  display: 'flex',
  justifyContent: 'center',
  marginTop: '24px',
  gap: "12px",
});

const TrustAgentRegistrationPage: React.FC = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [activeStep, setActiveStep] = useState<number>(0);
  const [validateFns, setValidateFns] = useState<Record<number, () => boolean>>({});
  const [afterValidateFns, setAfterValidateFns] = useState<Record<number, () => Promise<void>>>({});
  const { serverStatus } = useServerStatus();
  
  const registerStepFns = (step: number, validate: () => boolean, afterValidate?: () => Promise<void>) => {
    setValidateFns(prev => ({ ...prev, [step]: validate }));
    if (afterValidate) {
      setAfterValidateFns(prev => ({ ...prev, [step]: afterValidate }));
    }
  };
  
  const handleNext = async () => {
    const validate = validateFns[activeStep];
    const afterValidate = afterValidateFns[activeStep];

    if (validate && !validate()) return;

    try {
      if (afterValidate) {
        await afterValidate();
      }
  
      const { data } = await getTaInfo();
  
      setIsLoading(true);
      const nextStep = getNextStepByTaStatus(data);
      setActiveStep(nextStep);
      setIsLoading(false);
  
    } catch (error) {
      console.error('Step transition failed:', error);
      setIsLoading(false);
    }
  };

  const getNextStepByTaStatus = (taInfo: TaInfoResDto): number => {
    if (activeStep === 0) {
      if (!taInfo.name) {
        return 1;
      }

      switch (taInfo.status) {
        case TasStatus.DID_DOCUMENT_REQUIRED:
          return 2; 
        case TasStatus.CERTIFICATE_VC_REQUIRED:
          return 3;
        case TasStatus.COMPLETED:
          return 4;
        default:
          return activeStep + 1;
      }
    } else if (activeStep === 1) {
      switch (taInfo.status) {
        case TasStatus.DID_DOCUMENT_REQUIRED:
          return 2; 
        case TasStatus.CERTIFICATE_VC_REQUIRED:
          return 3;
        case TasStatus.COMPLETED:
          return 4;
        default:
          return activeStep + 1;
      } 
    } 

    return activeStep + 1;
  };

  const handleBack = () => setActiveStep((prev) => prev - 1);

  const getStepContent = (step: number) => {
    switch (step) {
      case 0: return <Step1TaPassword step={0} onRegister={registerStepFns} setIsLoading={setIsLoading}/>;
      case 1: return <Step2TaInfo step={1} onRegister={registerStepFns} setIsLoading={setIsLoading} />;
      case 2: return <Step3DIDDocument step={2} onRegister={registerStepFns} setIsLoading={setIsLoading} />;
      case 3: return <Step4CertificateVC step={3} onRegister={registerStepFns} setIsLoading={setIsLoading} />;
      case 4: return <StepComplete />;
      default: return 'Unknown step';
    }
  };

  if (serverStatus === 'COMPLETED') {
    return <Navigate to="/ta-management" replace />;
  }

  return (
    <>
      <FullscreenLoader open={isLoading} />
      <StyledContainer>
        <StyledTitle>TA Registration</StyledTitle>
  
        <StyledStepperWrapper>
          {activeStep < steps.length && (
            <StyledStepper activeStep={activeStep}>
              {steps.map((label) => (
                <StyledStep key={label}>
                  <StyledStepLabel>{label}</StyledStepLabel>
                </StyledStep>
              ))}
            </StyledStepper>
          )}
  
          <StyledContentWrapper>
            {getStepContent(activeStep)}
  
            {activeStep < steps.length && (
              <StyledActionWrapper>
                <Button variant='outlined' disabled={activeStep === 0} onClick={handleBack}>
                  Back
                </Button>
                <Button variant="contained" onClick={handleNext}>
                  {activeStep === steps.length - 1 ? 'Finish' : 'Next'}
                </Button>
              </StyledActionWrapper>
            )}
          </StyledContentWrapper>
        </StyledStepperWrapper>
      </StyledContainer>
    </>
  );
  
  
};

export default TrustAgentRegistrationPage;
