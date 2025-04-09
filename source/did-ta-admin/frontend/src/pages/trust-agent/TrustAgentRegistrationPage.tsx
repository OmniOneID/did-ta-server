import React, { useMemo, useState } from 'react';
import { Stepper, Step, StepLabel, Button, Box, Typography, styled } from '@mui/material';
import Step2Url from './Step2Url';
import Step3TestConnection from './Step3TestConnection';
import Step4Confirm from './Step4Confirm';
import Step0TaPassword from './stepper/Step0TaPassword';

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
  const [activeStep, setActiveStep] = useState<number>(0);
  const [validateFns, setValidateFns] = useState<Record<number, () => boolean>>({});

  const onValidateFn = (step: number, fn: () => boolean) => {
    setValidateFns((prev) => ({ ...prev, [step]: fn }));
  };
  
  const handleNext = async () => {
    const validate = validateFns[activeStep];
    if (validate && !validate()) return;

    setActiveStep((prev) => prev + 1);
  };

  const handleBack = () => setActiveStep((prev) => prev - 1);

  const getStepContent = (step: number) => {
    switch (step) {
      case 0: return <Step0TaPassword step={0} onValidate={onValidateFn}  />;
      case 1: return <Step2Url />;
      case 2: return <Step3TestConnection />;
      case 3: return <Step4Confirm />;
      default: return 'Unknown step';
    }
  };

  return (
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
  );
};

export default ServerRegistrationStepper;
