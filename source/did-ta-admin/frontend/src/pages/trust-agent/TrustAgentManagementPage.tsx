import { Box, Button, Popover, styled, TextField, Typography, Dialog, DialogTitle, DialogContent, DialogActions, Tooltip, IconButton } from '@mui/material';
import React, { useMemo, useState } from 'react';
import { Navigate, useNavigate } from 'react-router';
import { useServerStatus } from '../../context/ServerStatusContext';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';

export default function TrustAgentManagementPage() {
  const { taInfo, serverStatus } = useServerStatus();
  const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);
  const [certOpen, setCertOpen] = useState(false);
  const [certData, setCertData] = useState<any>(null);

  if (!taInfo) {
    return (
      <Box sx={{ textAlign: 'center', mt: 5 }}>
        <Typography variant="h6">Failed to retrieve TA information.</Typography>
      </Box>
    );
  }

  const copyJson = async () => {
    if (!certData) return;
    try { await navigator.clipboard.writeText(JSON.stringify(certData, null, 2)); } catch(err) {
      console.error(err);
    }
  };

  const openCertificate = async () => {
    setCertOpen(true);
    setCertData(JSON.parse(taInfo.certificateVc));
  };

  const handlePopoverOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handlePopoverClose = () => {
    setAnchorEl(null);
  };

  if (serverStatus !== 'COMPLETED') {
    return <Navigate to="/ta-registration" replace />;
  }

  const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
    width: 600,
    margin: 'auto',
    marginTop: theme.spacing(1),
    padding: theme.spacing(3),
    border: 'none',
    borderRadius: theme.shape.borderRadius,
    backgroundColor: '#ffffff',
    boxShadow: '0px 4px 8px 0px #0000001A',
  })), []);
  
  const StyledTitle = useMemo(() => styled(Typography)({
    textAlign: 'left',
    fontSize: '24px',
    fontWeight: 700,
  }), []);

  const StyledInputArea = useMemo(() => styled(Box)(({ theme }) => ({
      marginTop: theme.spacing(1),
  })), []);

  return (
    <>
      <StyledContainer>
        <StyledTitle>TA Management</StyledTitle>

        <StyledInputArea>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <TextField 
              fullWidth 
              label="DID" 
              variant="standard" 
              margin="normal" 
              value={taInfo.did} 
              slotProps={{ input: { readOnly: true } }} 
            />
            <Button 
              variant="outlined" 
              size="small" 
              onClick={handlePopoverOpen} 
              sx={{
                height: '100%', 
                flexShrink: 0, 
                whiteSpace: 'nowrap', 
                minWidth: 'auto',
              }}
            >
              View DID Document
            </Button>
          </Box>
          <Popover
            open={Boolean(anchorEl)}
            onClose={handlePopoverClose}
            anchorReference="none"
            sx={{
              position: "absolute",
              top: "50%",
              left: "50%",
              transform: "translate(-50%, -50%)",
              height: "80vh",
            }}
            slotProps={{
              paper: {
                sx: {
                  maxWidth: 500,
                  width: "80vw",
                  padding: 3,
                  height: { xs: "auto", md: "100vh" },
                  overflowY: "auto",
                },
              },
            }}
          >
            <Box sx={{ p: 2, maxWidth: 500 }}>
              <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>
                {JSON.stringify(taInfo.didDocument, null, 2)}
              </Typography>
            </Box>
          </Popover>

          <TextField 
            fullWidth 
            label="Name" 
            variant="standard" 
            margin="normal" 
            value={taInfo.name} 
            slotProps={{ input: { readOnly: true } }} 
          />

          <TextField 
            fullWidth 
            label="Status" 
            variant="standard" 
            margin="normal" 
            value={taInfo.status} 
            slotProps={{ input: { readOnly: true } }} 
          />

          <TextField 
            fullWidth 
            label="URL" 
            variant="standard" 
            margin="normal" 
            value={taInfo.serverUrl} 
            slotProps={{ input: { readOnly: true } }} 
          />

          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <TextField
              fullWidth
              label="Certificate URL"
              variant="standard"
              margin="normal"
              value={taInfo.certificateUrl}
              slotProps={{ input: { readOnly: true } }}
            />

            <Button
                variant="outlined"
                size="small"
                onClick={openCertificate}
                sx={{ height: '100%', flexShrink: 0, whiteSpace: 'nowrap', minWidth: 'auto' }}
              >
                View
            </Button>
          </Box>

          <TextField
            fullWidth
            label="Registered At" 
            variant="standard" 
            margin="normal" 
            value={taInfo.createdAt} 
            slotProps={{ input: { readOnly: true } }} 
          />
        </StyledInputArea>
      </StyledContainer>
      <Dialog
        open={certOpen}
        onClose={() => setCertOpen(false)}
        fullWidth
        maxWidth="md"
        disableEnforceFocus
        disableRestoreFocus
        PaperProps={{ sx: { height: { xs: '80vh', md: '70vh' } } }}
      >
        <DialogTitle sx={{ display: 'flex', alignItems: 'center' }}>
          Certificate
          <Box sx={{ flex: 1 }} />
          <Tooltip title="Copy JSON">
            <span>
              <IconButton size="small" onClick={copyJson} disabled={!certData}><ContentCopyIcon fontSize="small" /></IconButton>
            </span>
          </Tooltip>
        </DialogTitle>

        <DialogContent dividers sx={{ bgcolor: '#fafafa' }}>
            <Typography
              component="pre"
              sx={{
                m: 0,
                fontFamily: 'ui-monospace, SFMono-Regular, Menlo, monospace',
                fontSize: 13,
                whiteSpace: 'pre-wrap',
                wordBreak: 'break-word',
              }}
            >
              {JSON.stringify(certData ?? {}, null, 2)}
            </Typography>
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setCertOpen(false)} variant="contained">Close</Button>
        </DialogActions>
      </Dialog>
    </>
  );
}
