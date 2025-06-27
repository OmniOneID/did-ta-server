import { useDialogs } from '@toolpad/core';
import React, { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams } from 'react-router';
import { Box, Button, Popover, styled, TextField, Typography, useTheme } from '@mui/material';
import CustomDialog from '../../../components/dialog/CustomDialog';
import { getCredentialSchemaInfo } from '../../../apis/list-api';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import { formatErrorMessage } from '../../../utils/error-handler';

type Props = {}

interface CredentialSchemaFormData {
    name: string;
    credentialSchemaId: string;
    issuerName: string;
    createdAt: string;
    updatedAt: string;
    credentialSchema: string;
}

const CredentialSchemaDetailPage = (props: Props) => {
    const { id } = useParams();
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const theme = useTheme();

    const numericId = id ? parseInt(id, 10) : null;
    const [isLoading, setIsLoading] = useState<boolean>(true); 
    const [formData, serFormData] = useState<CredentialSchemaFormData>({
        name: '',
        credentialSchemaId: '',
        issuerName: '',
        createdAt: '',
        updatedAt: '',
        credentialSchema: '',
    });

    const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);

    const handlePopoverOpen = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };

    const handlePopoverClose = () => {
        setAnchorEl(null);
    };

    useEffect(() => {
        const fetchData = async () => {
            if (numericId === null || isNaN(numericId)) {
                await dialogs.open(CustomDialog, { 
                    title: 'Notification', 
                    message: 'Invalid Path.', 
                    isModal: true 
                },{
                    onClose: async () => navigate('/list-settings/allowed-ca', { replace: true }),
                });
                return;
            }

            setIsLoading(true);

            try {
                const { data } = await getCredentialSchemaInfo(numericId);
                serFormData({
                    name: data.name,
                    credentialSchemaId: data.credentialSchemaId,
                    issuerName: data.issuerName,
                    createdAt: data.createdAt,
                    updatedAt: data.updatedAt,
                    credentialSchema: data.credentialSchema,
                });
                setIsLoading(false);
            } catch (err) {
                  console.error('Failed to fetch Credential Schema information:', err);
                  setIsLoading(false);
                  navigate('/error', { state: { message: formatErrorMessage(err, "Failed to fetch Credential Schema") } });
            }
        };

        fetchData();
    }, [numericId]);

    const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
        width: 600,
        margin: 'auto',
        marginTop: theme.spacing(3),
        padding: theme.spacing(3),
        border: 'none',
        borderRadius: theme.shape.borderRadius,
        backgroundColor: '#ffffff',
        boxShadow: '0px 4px 8px 0px #0000001A',
    })), []);
      
    const StyledSubTitle = useMemo(() => styled(Typography)({
        textAlign: 'left',
        fontSize: '24px',
        fontWeight: 700,
    }), []);

    const StyledDescription = useMemo(() => styled(Box)(({ theme }) => ({
        maxWidth: 500, 
        marginTop: theme.spacing(1),
        padding: theme.spacing(0),
    })), []);

    const StyledInputArea = useMemo(() => styled(Box)(({ theme }) => ({
        marginTop: theme.spacing(2),
    })), []);

    return (
        <>
            <FullscreenLoader open={isLoading} />
            <Typography variant="h4">Credential Schema Management</Typography>
            <StyledContainer>
                <StyledSubTitle>Credential Schema Detail Information</StyledSubTitle>
                <StyledInputArea>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                        <TextField 
                            fullWidth
                            label="Name" 
                            variant="standard"
                            margin="normal" 
                            value={formData.name || ''} 
                            sx={{minWidth: 250}}
                            slotProps={{ input: { readOnly: true } }} 
                        />
                        <Button 
                            variant="outlined" 
                            size="small" 
                            onClick={handlePopoverOpen} 
                            disabled={!formData.name}
                            sx={{ 
                                height: '100%', 
                                flexShrink: 0, 
                                whiteSpace: 'nowrap', 
                                minWidth: 'auto',
                            }}
                        >
                            View Credential Schema
                        </Button>
                    </Box>

                    <Popover
                        open={Boolean(anchorEl)}
                        anchorEl={anchorEl}
                        onClose={handlePopoverClose}
                        anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
                    >
                        <Box sx={{ p: 2, maxWidth: 500 }}>
                            <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>
                            {JSON.stringify(formData.credentialSchema, null, 2)}
                            </Typography>
                        </Box>
                    </Popover>


                    <TextField 
                        fullWidth
                        label="Credential Schema ID" 
                        variant="standard"
                        margin="normal" 
                        value={formData.credentialSchemaId || ''} 
                        slotProps={{ input: { readOnly: true } }} 
                    />

                    <TextField 
                        fullWidth
                        label="Issuer Name" 
                        variant="standard"
                        margin="normal" 
                        value={formData.issuerName || ''} 
                        slotProps={{ input: { readOnly: true } }} 
                    />

                    <TextField 
                        fullWidth 
                        label="Registered At" 
                        variant="standard" 
                        margin="normal" 
                        value={formData.createdAt || ''} 
                        slotProps={{ input: { readOnly: true } }} 
                    />

                    {formData.updatedAt && (
                        <TextField 
                            fullWidth 
                            label="Updated At" 
                            variant="standard" 
                            margin="normal" 
                            value={formData.updatedAt} 
                            slotProps={{ input: { readOnly: true } }} 
                        />
                    )}

                    <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
                        <Button variant="outlined" color="primary" onClick={() => navigate('/list-settings/credential-schema')}>
                            Back
                        </Button>
                        {/* <Button variant="contained" color="primary" onClick={() => navigate('/list-settings/vc-schema/vc-shema-edit/' + numericId)}>
                            Edit
                        </Button> */}
                    </Box>
                </StyledInputArea>
            </StyledContainer>
        </>
    )
}

export default CredentialSchemaDetailPage