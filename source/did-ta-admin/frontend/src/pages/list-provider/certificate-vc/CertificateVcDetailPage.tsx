import { Box, Button, Popover, styled, TextField, Typography } from '@mui/material';
import { useDialogs } from '@toolpad/core';
import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import { getCertificateVcInfo } from '../../../apis/list-api';
import CustomDialog from '../../../components/dialog/CustomDialog';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import { formatErrorMessage } from '../../../utils/error-handler';

type CertificateVcData = {
    id: number | null;
    did: string;
    name: string;
    certificateVc: object | null;
    publishedUrl: string;
    publishedAt: string;
    expiredAt: string;
    createdAt: string;
    updatedAt: string;
};

const CertificateVcDetailPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const dialogs = useDialogs();

    const numericId = id ? parseInt(id, 10) : null;
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [formData, setFormData] = useState<CertificateVcData>({
        id: null,
        did: '',
        name: '',
        certificateVc: null,
        publishedUrl: '',
        publishedAt: '',
        expiredAt: '',
        createdAt: '',
        updatedAt: '',
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
                    isModal: true,
                }, {
                    onClose: async () => navigate('/list-settings/certificate-vc', { replace: true }),
                });
                return;
            }

            setIsLoading(true);

            try {
                const { data } = await getCertificateVcInfo(numericId);
                setFormData({
                    id: data.id,
                    did: data.did,
                    name: data.name,
                    certificateVc: data.certificateVc,
                    publishedUrl: data.publishedUrl,
                    publishedAt: data.publishedAt,
                    expiredAt: data.expiredAt,
                    createdAt: data.createdAt,
                    updatedAt: data.updatedAt,
                });
                setIsLoading(false);
            } catch (err) {
                console.error('Failed to fetch Certificate VC information:', err);
                setIsLoading(false);
                navigate('/error', { state: { message: formatErrorMessage(err, 'Failed to fetch Certificate VC') } });
            }
        };

        fetchData();
    }, [numericId]);

    const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
        width: 640,
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

    const StyledInputArea = useMemo(() => styled(Box)(({ theme }) => ({
        marginTop: theme.spacing(2),
    })), []);

    return (
        <>
            <FullscreenLoader open={isLoading} />
            <Typography variant="h4">Certification VC Management</Typography>
            <StyledContainer>
                <StyledSubTitle>Certification VC Detail</StyledSubTitle>
                <StyledInputArea>
                    <TextField
                        fullWidth
                        label="Name"
                        variant="standard"
                        margin="normal"
                        value={formData.name || ''}
                        slotProps={{ input: { readOnly: true } }}
                    />

                    <TextField
                        fullWidth
                        label="DID"
                        variant="standard"
                        margin="normal"
                        value={formData.did || ''}
                        slotProps={{ input: { readOnly: true } }}
                    />

                    <TextField
                        fullWidth
                        label="Published URL"
                        variant="standard"
                        margin="normal"
                        value={formData.publishedUrl || ''}
                        slotProps={{ input: { readOnly: true } }}
                    />

                    <TextField
                        fullWidth
                        label="Published At"
                        variant="standard"
                        margin="normal"
                        value={formData.publishedAt || ''}
                        slotProps={{ input: { readOnly: true } }}
                    />

                    <TextField
                        fullWidth
                        label="Expired At"
                        variant="standard"
                        margin="normal"
                        value={formData.expiredAt || ''}
                        slotProps={{ input: { readOnly: true } }}
                    />

                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mt: 1 }}>
                        <TextField
                            fullWidth
                            label="Certificate VC"
                            variant="standard"
                            margin="normal"
                            value={formData.certificateVc ? '(click to view)' : ''}
                            slotProps={{ input: { readOnly: true } }}
                        />
                        <Button
                            variant="outlined"
                            size="small"
                            onClick={handlePopoverOpen}
                            disabled={!formData.certificateVc}
                            sx={{ flexShrink: 0, whiteSpace: 'nowrap', minWidth: 'auto' }}
                        >
                            View VC
                        </Button>
                    </Box>

                    <Popover
                        open={Boolean(anchorEl)}
                        anchorEl={anchorEl}
                        onClose={handlePopoverClose}
                        anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
                    >
                        <Box sx={{ p: 2, maxWidth: 560, maxHeight: 400, overflow: 'auto' }}>
                            <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap', fontFamily: 'monospace' }}>
                                {JSON.stringify(formData.certificateVc, null, 2)}
                            </Typography>
                        </Box>
                    </Popover>

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

                    <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3 }}>
                        <Button variant="outlined" color="primary" onClick={() => navigate('/list-settings/certificate-vc')}>
                            Back
                        </Button>
                    </Box>
                </StyledInputArea>
            </StyledContainer>
        </>
    );
};

export default CertificateVcDetailPage;
