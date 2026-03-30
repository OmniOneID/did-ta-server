import { Box, Link, styled, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core';
import React, { useCallback, useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import CustomDataGrid from '../../../components/data-grid/CustomDataGrid';
import CustomDialog from '../../../components/dialog/CustomDialog';
import { fetchCredentialDefinitionList } from '../../../apis/list-api';
import { formatErrorMessage } from '../../../utils/error-handler';

type Props = {}

type CredentialDefinitionRow = {
    id: string | number;
    credentialSchemaId: string;
    credentialDefinitionId: string;
    credentialDefinitionTag: string;
    issuerName: string;
    createdAt: string;
    updatedAt: string;
};

const CredentialDefinitionManagementPage = (props: Props) => {
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const [loading, setLoading] = useState<boolean>(false);
    const [totalRows, setTotalRows] = useState<number>(0);
    const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
    const [rows, setRows] = useState<CredentialDefinitionRow[]>([]);
    const [searchText, setSearchText] = useState<string>('');
    const [selectedSearch, setSelectedSearch] = useState<string>('credentialDefinitionId');

    const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
        page: 0,
        pageSize: 10,
    });

    const selectedRowData = useMemo(() => {
        return rows.find(row => row.id === selectedRow) || null;
    }, [rows, selectedRow]);

    const fetchData = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetchCredentialDefinitionList(
                paginationModel.page,
                paginationModel.pageSize,
                selectedSearch && searchText.trim() ? selectedSearch : null,
                selectedSearch && searchText.trim() ? searchText.trim() : null
            );
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
        } catch (err) {
            console.error("Failed to fetch Credential Definition List. ", err);
            navigate('/error', { state: { message: formatErrorMessage(err, "Failed to fetch Credential Definition List") } });
        } finally {
            setLoading(false);
        }
    }, [paginationModel.page, paginationModel.pageSize, selectedSearch, searchText, navigate]);

    const getData = useCallback(async () => {
        setLoading(true);
        try {
            const response = await fetchCredentialDefinitionList(
                0,
                paginationModel.pageSize,
                selectedSearch && searchText.trim() ? selectedSearch : null,
                selectedSearch && searchText.trim() ? searchText.trim() : null
            );
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
            setPaginationModel((prev) => ({ ...prev, page: 0 }));
        } catch (err) {
            console.error("Failed to fetch Credential Definition List. ", err);
            setLoading(false);
            await dialogs.open(CustomDialog, {
                title: 'Notification',
                message: formatErrorMessage(err, 'Failed to retrieve Credential Definition List'),
                isModal: true,
            });
        } finally {
            setLoading(false);
        }
    }, [paginationModel.pageSize, selectedSearch, searchText, dialogs]);

    useEffect(() => {
        fetchData();
    }, [fetchData]);

    const handleSearch = useCallback(
        (field: string, text: string) => {
            const trimmed = text.trim();
            if (!trimmed) return;

            setSelectedSearch(field);
            setSearchText(trimmed);
            setPaginationModel((prev) => ({ ...prev, page: 0 }));
        },
        []
    );

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
                <StyledSubTitle>Credential Definition Management</StyledSubTitle>
                <CustomDataGrid
                    rows={rows}
                    columns={[
                        {
                            field: 'credentialDefinitionId',
                            headerName: "Credential Definition ID",
                            width: 250,
                            renderCell: (params) => (
                                <Link
                                    component="button"
                                    variant='body2'
                                    onClick={() => navigate(`/list-settings/credential-definition/${params.row.id}`)}
                                    sx={{ cursor: 'pointer', color: 'primary.main', textAlign: 'left' }}
                                >
                                    {params.value}
                                </Link>),
                        },
                        { field: 'credentialSchemaId', headerName: "Credential Schema ID", width: 150 },
                        { field: 'credentialDefinitionTag', headerName: "Credential Definition Tag", width: 180 },
                        { field: 'issuerName', headerName: "Issuer Name", width: 100 },
                        { field: 'createdAt', headerName: "Registered At", width: 150 },
                        { field: 'updatedAt', headerName: "Updated At", width: 150 },
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
                        { value: 'credentialDefinitionId', label: 'Definition ID' },
                        { value: 'credentialSchemaId', label: 'Schema ID' },
                        { value: 'issuerDid', label: 'Issuer DID' },
                    ]}
                    onSearch={handleSearch}
                    onRefresh={getData}
                />
            </StyledContainer>
        </>
    )
}

export default CredentialDefinitionManagementPage
