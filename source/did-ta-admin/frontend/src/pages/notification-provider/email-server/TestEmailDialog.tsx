import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField, DialogContentText, Box } from "@mui/material";
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
    <Dialog open={open} onClose={onClose} fullWidth sx={{ maxWidth: 500, margin: "0 auto" }}>
      <Box sx={{ px: 2 }}>
        <DialogTitle sx={{ p: 0, pt: 2, fontWeight: 700 }}>Send Test Email</DialogTitle>
        <Box sx={{ height: "1px", backgroundColor: "var(--G40, #BFBFBF)", width: "100%", mt: 1 }} />
      </Box>

      <DialogContent sx={{ px: 2 }}>
        <DialogContentText sx={{ textAlign: "left" }}>
          Please enter the recipient's email address.
        </DialogContentText>
        <TextField
          fullWidth
          label="Recipient Email *"
          variant="outlined"
          margin="normal"
          value={email}
          onChange={handleChange}
          error={!!errors.email}
          helperText={errors.email}
        />
      </DialogContent>

      <DialogActions sx={{ px: 2, pt: 0, display: "flex", gap: 2, mt: 0 }}>
        <Button variant="outlined" onClick={onClose} color="primary" sx={{ flexGrow: 1, height: "48px" }}>
          Cancel
        </Button>
        <Button variant="contained" onClick={handleSend} color="primary" disabled={isButtonDisabled} sx={{ flexGrow: 1, height: "48px" }}>
          Send
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default TestEmailDialog;
