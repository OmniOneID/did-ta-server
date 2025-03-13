import SettingsIcon from '@mui/icons-material/Settings';
import { Box, Button, Popover, useTheme } from '@mui/material';
import { useDialogs } from '@toolpad/core';
import { AuthenticationContext, SessionContext } from '@toolpad/core/AppProvider';
import React, { useContext, useState } from 'react';
import { requestPasswordReset } from '../../apis/admin-api';
import PasswordChangeDialog from '../../pages/auth/PasswordChangeDialog';
import { sha256Hash } from '../../utils/sha256-hash';
import CustomConfirmDialog from '../dialog/CustomConfirmDialog';
import CustomDialog from '../dialog/CustomDialog';

const AccountMenu = () => {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const authentication = useContext(AuthenticationContext);
  const session = useContext(SessionContext);
  const [isPasswordDialogOpen, setIsPasswordDialogOpen] = useState(false);
  const dialogs = useDialogs();
  const theme = useTheme(); 

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
        const oldHashedPassword = await sha256Hash(oldPassword);
        const newHashedPassword = await sha256Hash(newPassword);
        
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

  const open = Boolean(anchorEl);

  return (
    <>
      <Button 
        startIcon={<SettingsIcon />}
        onClick={handleClick} 
        variant="contained"
        size='small'
        sx={{
          width: '90px',
          height: '32px',
          borderRadius: '6px',
          paddingRight: '16px',
          paddingLeft: '16px',
          borderWidth: '1px',
          borderStyle: 'solid',
          backgroundColor: `${theme.palette.mode === 'light' ? '#202B45' : '#555555'} !important`,
        }}
      >
        Setting
      </Button>
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
