import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router';
import { getEntityInfo, approveEntityDid } from '../../apis/entity-api';
import { CircularProgress, Box, Typography, TextField, Button, Popover, useTheme, useMediaQuery, styled } from '@mui/material';
import CustomDialog from '../../components/dialog/CustomDialog';
import { useDialogs } from '@toolpad/core/useDialogs';
import { formatErrorMessage } from '../../utils/error-handler';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import CustomConfirmDialog from '../../components/dialog/CustomConfirmDialog';

const EntityDetailPage = () => {
    const { entityId } = useParams();
    const navigate = useNavigate();
    const dialogs = useDialogs();

    const numericEntityId = entityId ? parseInt(entityId, 10) : null;
    const [isLoading, setIsLoading] = useState<boolean>(true); 
    const [entityData, setEntityData] = useState<any>(null); 

    const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);

    const theme = useTheme();
    const isSmallScreen = useMediaQuery(theme.breakpoints.down('sm'));

    useEffect(() => {
        const fetchData = async () => {
            if (numericEntityId === null || isNaN(numericEntityId)) {
                await dialogs.open(CustomDialog, { 
                    title: 'Notification', 
                    message: 'Invalid Path.', 
                    isModal: true 
                },{
                    onClose: async (result) =>  navigate('/entities/entity-management', { replace: true }),
                });
                return;
            }

            setIsLoading(true);

            try {
                const { data } = await getEntityInfo(numericEntityId);
                setEntityData(data);
                setIsLoading(false);
            } catch (err) {
                console.error('Failed to fetch Entity information:', err);
                setIsLoading(false);
                navigate('/error', { state: { message: formatErrorMessage(err, "Failed to fetch Entity information") } });
            } 
        };
        fetchData();
    }, []);

    const handlePopoverOpen = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };

    const handlePopoverClose = () => {
        setAnchorEl(null);
    };

    const handleApprove = async () => {
        const result = await dialogs.open(CustomConfirmDialog, {
            title: 'Confirmation',
            message: 'Are you sure you want to approve entity?',
            isModal: true,
        });
        
        if (result) {
            setIsLoading(true);
            try {
                const { data } = await approveEntityDid({ entityId: entityId });
                setEntityData(data);
                setIsLoading(false);
                await dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: 'Completed entity approval.',
                    isModal: true,
                },{
                    onClose: async (result) =>  navigate('/entities/entity-management'),
                });
            } catch (err) {
                console.error('Failed to approve Entity:', err);
                setIsLoading(false);

                dialogs.open(CustomDialog, {
                    title: 'Notification',
                    message: formatErrorMessage(err, `Failed to approve Entity`),
                    isModal: true,
                });
            }
        }
    };

    const StyledContainer = styled(Box)(({ theme }) => ({
        width: 500,
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

    const StyledInputArea = styled(Box)(({ theme }) => ({
        marginTop: theme.spacing(2),
    }));

    return (
        <>
            <FullscreenLoader open={isLoading} />
            <Typography variant="h4">Entity Management</Typography>
            <StyledContainer>
                <StyledTitle>Entity Detail Information</StyledTitle>
        
                <StyledInputArea>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <TextField 
                            fullWidth 
                            label="DID" 
                            variant="standard" 
                            margin="normal" 
                            value={entityData?.did || ''}
                            slotProps={{ input: { readOnly: true } }} 
                        />
                        <Button 
                            variant="outlined" 
                            size="small" 
                            onClick={handlePopoverOpen} 
                            disabled={entityData?.didDocument ? false : true}
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
                            {JSON.stringify(entityData?.didDocument || '', null, 2)}
                            </Typography>
                        </Box>
                    </Popover>

                    <TextField 
                        fullWidth 
                        label="Name" 
                        variant="standard" 
                        margin="normal" 
                        value={entityData?.name || ''} 
                        slotProps={{ input: { readOnly: true } }} 
                    />

                    <TextField 
                        fullWidth 
                        label="Role" 
                        variant="standard" 
                        margin="normal" 
                        value={entityData?.role || ''} 
                        slotProps={{ input: { readOnly: true } }} 
                    />

                    <TextField 
                        fullWidth 
                        label="Status" 
                        variant="standard" 
                        margin="normal" 
                        value={entityData?.status || ''} 
                        slotProps={{ input: { readOnly: true } }} 
                    />

                    <TextField 
                        fullWidth 
                        label="Server URL" 
                        variant="standard" 
                        margin="normal" 
                        value={entityData?.serverUrl} 
                        slotProps={{ input: { readOnly: true } }} 
                    />

                    <TextField 
                        fullWidth 
                        label="Registered At" 
                        variant="standard" 
                        margin="normal" 
                        value={entityData?.createdAt || ''} 
                        slotProps={{ input: { readOnly: true } }} 
                    />

                    {entityData?.updatedAt && (
                        <TextField 
                            fullWidth 
                            label="Updated At" 
                            variant="standard" 
                            margin="normal" 
                            value={entityData?.updatedAt || ''} 
                            slotProps={{ input: { readOnly: true } }} 
                        />
                    )}
                </StyledInputArea>
                <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 3 }}>
                    <Button variant="outlined" color="primary" onClick={() => navigate('/entities/entity-management')}>
                        Back
                    </Button>
                    {entityData?.status === "DID_DOCUMENT_REQUIRED" && (
                        <Button variant="contained" color="primary" onClick={handleApprove}>
                            DID Doc Approval
                        </Button>
                    )}
                </Box>

            </StyledContainer>
        </>
    );
    
};

export default EntityDetailPage;
