import { Box, styled, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router';
import { fetchCertificateVcList } from '../../../apis/list-api';
import CustomDataGrid from '../../../components/data-grid/CustomDataGrid';
import CustomDialog from '../../../components/dialog/CustomDialog';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import { formatErrorMessage } from '../../../utils/error-handler';

type CertificateVcRow = {
    id: number;
    did: string;
    name: string;
    publishedUrl: string;
    publishedAt: string;
    expiredAt: string;
    createdAt: string;
};

const CertificateVcManagementPage = () => {
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const [loading, setLoading] = useState<boolean>(false);
    const [totalRows, setTotalRows] = useState<number>(0);
    const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
    const [rows, setRows] = useState<CertificateVcRow[]>([]);
    const [searchText, setSearchText] = useState<string>('');
    const [selectedSearch, setSelectedSearch] = useState<string>('name');

    const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
        page: 0,
        pageSize: 10,
    });

    const fetchData = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetchCertificateVcList(
                paginationModel.page,
                paginationModel.pageSize,
                selectedSearch && searchText.trim() ? selectedSearch : null,
                selectedSearch && searchText.trim() ? searchText.trim() : null
            );
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
        } catch (err) {
            console.error('Failed to fetch Certificate VC List.', err);
            navigate('/error', { state: { message: formatErrorMessage(err, 'Failed to fetch Certificate VC List') } });
        } finally {
            setLoading(false);
        }
    }, [paginationModel.page, paginationModel.pageSize, selectedSearch, searchText, navigate]);

    const getData = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetchCertificateVcList(
                0,
                paginationModel.pageSize,
                selectedSearch && searchText.trim() ? selectedSearch : null,
                selectedSearch && searchText.trim() ? searchText.trim() : null
            );
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
            setPaginationModel((prev) => ({ ...prev, page: 0 }));
        } catch (err) {
            console.error('Failed to fetch Certificate VC List.', err);
            setLoading(false);
            await dialogs.open(CustomDialog, {
                title: 'Notification',
                message: formatErrorMessage(err, 'Failed to retrieve Certificate VC List'),
                isModal: true,
            });
        } finally {
            setLoading(false);
        }
    }, [paginationModel.pageSize, selectedSearch, searchText, dialogs]);

    useEffect(() => {
        fetchData();
    }, [fetchData]);

    const handleSearch = useCallback((field: string, text: string) => {
        const trimmed = text.trim();
        if (!trimmed) return;
        setSelectedSearch(field);
        setSearchText(trimmed);
        setPaginationModel((prev) => ({ ...prev, page: 0 }));
    }, []);

    const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
        margin: 'auto',
        marginTop: theme.spacing(1),
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

    return (
        <>
            <FullscreenLoader open={loading} />
            <StyledContainer>
                <StyledSubTitle>Certification VC Management</StyledSubTitle>
                <CustomDataGrid
                    rows={rows}
                    columns={[
                        { field: 'id', headerName: '#', width: 70 },
                        {
                            field: 'name',
                            headerName: 'Name',
                            width: 200,
                            renderCell: (params) => (
                                <span
                                    style={{ cursor: 'pointer', color: '#1976d2' }}
                                    onClick={() => navigate(`/list-settings/certificate-vc/${params.row.id}`)}
                                >
                                    {params.value}
                                </span>
                            ),
                        },
                        { field: 'did', headerName: 'DID', width: 300 },
                        { field: 'publishedAt', headerName: 'Published At', width: 160 },
                        { field: 'expiredAt', headerName: 'Expired At', width: 160 },
                        { field: 'createdAt', headerName: 'Registered At', width: 160 },
                    ]}
                    selectedRow={selectedRow}
                    setSelectedRow={setSelectedRow}
                    additionalButtons={[]}
                    paginationMode="server"
                    totalRows={totalRows}
                    paginationModel={paginationModel}
                    setPaginationModel={setPaginationModel}
                    enableSearch={true}
                    searchText={searchText}
                    setSearchText={setSearchText}
                    selectedSearch={selectedSearch}
                    setSelectedSearch={setSelectedSearch}
                    searchOptions={[
                        { value: 'name', label: 'Name' },
                        { value: 'did', label: 'DID' },
                    ]}
                    onSearch={handleSearch}
                    onRefresh={getData}
                />
            </StyledContainer>
        </>
    );
};

export default CertificateVcManagementPage;
