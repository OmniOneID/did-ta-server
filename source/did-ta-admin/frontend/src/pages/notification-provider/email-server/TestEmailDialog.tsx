import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from "@mui/material";
import React, { useEffect, useState } from "react";
import { emailRegex } from "../../../utils/regex";

interface TestEmailDialogProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (email: string) => void;
}

interface ErrorState {
    email?: string;
}

const TestEmailDialog: React.FC<TestEmailDialogProps> = ({ open, onClose, onSubmit }) => {
  const [email, setEmail] = useState("");
  const [errors, setErrors] = useState<ErrorState>({});
  const [isButtonDisabled, setIsButtonDisabled] = useState(true);

  const handleSend = () => {
    if (!validate()) return;
    onSubmit(email);
    onClose();
  };

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = event.target.value;
    setEmail(newValue);
  };

  const validate = () => {
    let tempErrors: ErrorState = {};

    if (!email.trim()) {
      tempErrors.email = "Please enter an email.";
    } else if (!emailRegex.test(email)) {
      tempErrors.email = "Please enter a valid email.";
    }

    setErrors(tempErrors);
    return Object.values(tempErrors).every((error) => !error);
  };

  useEffect(() => {
    setIsButtonDisabled(!email.trim());
  }, [email]);

  useEffect(() => {
    if (open) {
      setEmail(""); 
      setErrors({});
      setIsButtonDisabled(true);
    }
  }, [open]);

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Send Test Email</DialogTitle>
      <DialogContent>
        <TextField
            fullWidth
            label="Recipient Email"
            variant="outlined"
            margin="normal"
            value={email}
            onChange={handleChange}
            error={!!errors.email}
            helperText={errors.email}
            sx={{ minLength: 4, maxLength: 256 }}
        />
      </DialogContent>
      <DialogActions>
        <Button variant="contained" onClick={onClose} color="secondary">
            Cancel
        </Button>
        <Button variant="contained" onClick={handleSend} color="primary" disabled={isButtonDisabled}>
            Send
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default TestEmailDialog;
