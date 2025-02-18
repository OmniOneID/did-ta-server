import React from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, DialogContentText } from '@mui/material';
import { DialogProps } from '@toolpad/core/useDialogs';

const CustomConfirmDialog: React.FC<DialogProps<{ message: string; title?: string; isModal?: boolean }, boolean>> = ({
  payload,
  open,
  onClose,
}) => {
  const handleClose = (event: unknown, reason?: string) => {
    if (payload?.isModal && reason === 'backdropClick') {
      return; 
    }
    onClose(false);
  };

  return (
    <Dialog open={open} onClose={handleClose} disableEscapeKeyDown={payload?.isModal ?? false}>
      {payload?.title && <DialogTitle>{payload.title}</DialogTitle>}
      <DialogContent>
        <DialogContentText>{payload?.message}</DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button variant="contained" onClick={() => onClose(false)} color="secondary">
          취소
        </Button>
        <Button variant="contained" onClick={() => onClose(true)} color="primary" autoFocus>
          확인
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default CustomConfirmDialog;
