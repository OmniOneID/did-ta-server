import SettingsIcon from '@mui/icons-material/Settings';
import { Box, Button, IconButton, Popover } from '@mui/material';
import { useDialogs } from '@toolpad/core';
import { AuthenticationContext, SessionContext } from '@toolpad/core/AppProvider';
import React, { useContext, useState } from 'react';
import { requestPasswordReset } from '../../apis/admin-api';
import PasswordChangeDialog from '../../pages/auth/PasswordChangeDialog';
import CustomConfirmDialog from '../dialog/CustomConfirmDialog';
import CustomDialog from '../dialog/CustomDialog';

const AccountMenu = () => {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const authentication = useContext(AuthenticationContext);
  const session = useContext(SessionContext);
  const [isPasswordDialogOpen, setIsPasswordDialogOpen] = useState(false);
  const dialogs = useDialogs();

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleSignOut = () => {
    authentication?.signOut();
    setAnchorEl(null);
  };

  const handleOpenPasswordDialog = () => {   
    setIsPasswordDialogOpen(true);
    setAnchorEl(null);
  };

  const handlePasswordChange = async (oldPassword: string, newPassword: string) => {

    const result = await dialogs.open(CustomConfirmDialog, {
      title: 'Confirmation',
      message: 'Are you sure you want to Change Admin password?',
      isModal: true,
    });

    try {
      if (result) {
        const oldHashedPassword = await hashPassword(oldPassword);
        const newHashedPassword = await hashPassword(newPassword);
        
        if (!session || !session.user || !session.user.id) {
          console.error("Session or user ID is null. Cannot reset password.");
          return;
        }
        
        await requestPasswordReset({
          loginId: session.user.id,
          oldPassword: oldHashedPassword,
          newPassword: newHashedPassword,
        });

        dialogs.open(CustomDialog, {
          title: 'Notification',
          message: 'Admin password change completed.',
          isModal: true,
        });
      }

    } catch (error) {
        dialogs.open(CustomDialog, {
          title: 'Notification',
          message: 'Failed to change password.',
          isModal: true,
        });
    }
  };

  async function hashPassword(password: string): Promise<string> {
    const encoder = new TextEncoder();
    const data = encoder.encode(password);
    const hashBuffer = await crypto.subtle.digest('SHA-256', data);
    return Array.from(new Uint8Array(hashBuffer))
      .map(b => b.toString(16).padStart(2, '0'))
      .join('');
  }

  const open = Boolean(anchorEl);

  return (
    <>
      <IconButton onClick={handleClick} color="inherit">
        <SettingsIcon />
      </IconButton>
      <Popover
        open={open}
        anchorEl={anchorEl}
        onClose={handleClose}
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'right',
        }}
        transformOrigin={{
          vertical: 'top',
          horizontal: 'right',
        }}
      >
        <Box sx={{ p: 2, minWidth: 200 }}>
          <Button fullWidth variant="contained" sx={{ mt: 1 }} onClick={handleOpenPasswordDialog}>
            Change Password
          </Button>
          <Button fullWidth variant="contained" sx={{ mt: 1 }} onClick={handleSignOut}>
            Logout
          </Button>
        </Box>
      </Popover>

      <PasswordChangeDialog
        open={isPasswordDialogOpen}
        onClose={() => setIsPasswordDialogOpen(false)}
        onSubmit={handlePasswordChange}
      />
    </>
  );
};

export default AccountMenu;
