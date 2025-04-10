import React, { useMemo, useState } from 'react';
import { Stepper, Step, StepLabel, Button, Box, Typography, styled } from '@mui/material';
import Step1TaPassword from './stepper/Step1TaPassword';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import Step2TaInfo from './stepper/Step2TaInfo';
import Step3DIDDocument from './stepper/Step3DidDocument';
import Step4CertificateVC from './stepper/Step4CertificateVc';

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

const ServerRegistrationStepper: React.FC = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [activeStep, setActiveStep] = useState<number>(0);
  const [validateFns, setValidateFns] = useState<Record<number, () => boolean>>({});
  const [afterValidateFns, setAfterValidateFns] = useState<Record<number, () => Promise<void>>>({});

  const onValidateFn = (step: number, fn: () => boolean) => {
    setValidateFns((prev) => ({ ...prev, [step]: fn }));
  };

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

    if (afterValidate) {
      setIsLoading(true);
      try {
        await afterValidate();
      } catch (error) {
        console.error('Post-validation error:', error);
        setIsLoading(false);
        return;
      }
      setIsLoading(false);
    }

    setActiveStep(prev => prev + 1);
  };

  // const handleNext = async () => {
  //   const validate = validateFns[activeStep];
  //   if (validate && !validate()) return;

  //   setActiveStep((prev) => prev + 1);
  // };

  const handleBack = () => setActiveStep((prev) => prev - 1);

  const getStepContent = (step: number) => {
    switch (step) {
      case 0: return <Step1TaPassword step={0} onRegister={registerStepFns} />;
      case 1: return <Step2TaInfo step={1} onRegister={registerStepFns} />;
      case 2: return <Step3DIDDocument step={2} onRegister={registerStepFns} />;
      case 3: return <Step4CertificateVC step={3} onRegister={registerStepFns} />;
      default: return 'Unknown step';
    }
  };

  return (
    <>
      <FullscreenLoader open={isLoading} />
      <StyledContainer>
        <StyledTitle>TA Registration</StyledTitle>
        <StyledStepperWrapper>
          <StyledStepper activeStep={activeStep}>
            {steps.map((label) => (
              <StyledStep key={label}>
                <StyledStepLabel>{label}</StyledStepLabel>
              </StyledStep>
            ))}
          </StyledStepper>

          <StyledContentWrapper>
            {getStepContent(activeStep)}
            <StyledActionWrapper>
              <Button variant='outlined' disabled={activeStep === 0} onClick={handleBack}>Back</Button>
              <Button variant="contained" onClick={handleNext}>
                {activeStep === steps.length - 1 ? 'Finish' : 'Next'}
              </Button>
            </StyledActionWrapper>
          </StyledContentWrapper>
        </StyledStepperWrapper>
      </StyledContainer>
    </>
  );
};

export default ServerRegistrationStepper;
