import { Box, Link, styled, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core';
import React, { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import CustomDataGrid from '../../../components/data-grid/CustomDataGrid';
import CustomConfirmDialog from '../../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../../components/dialog/CustomDialog';
import { fetchVcSchemaList } from '../../../apis/list-api';
import { formatErrorMessage } from '../../../utils/error-handler';

type Props = {}

type VcSchemaRow = {
    id: string | number;
    title: string;
    description: string;
    issuerName: string;
    createdAt: string;
    updatedAt: string;
};

const VcSchemaManagementPage = (props: Props) => {
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const [loading, setLoading] = useState<boolean>(false);
    const [totalRows, setTotalRows] = useState<number>(0);
    const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
    const [rows, setRows] = useState<VcSchemaRow[]>([]);

    const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
        page: 0,
        pageSize: 10,
    });

    const selectedRowData = useMemo(() => {
        return rows.find(row => row.id === selectedRow) || null;
    }, [rows, selectedRow]);

    const handleDelete = async () => {


    };

    useEffect(() => {
        setLoading(true);
        fetchVcSchemaList(paginationModel.page, paginationModel.pageSize, null, null)
          .then((response) => {
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
          })
          .catch((err) => {
            console.error("Failed to fetch VC Schema List. ", err);
            navigate('/error', { state: { message: formatErrorMessage(err, "Failed to fetch VC Schema List") } });
          })
          .finally(() => setLoading(false));
    }, [paginationModel]);

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
                <StyledSubTitle>VC Schema Management</StyledSubTitle>
                <CustomDataGrid 
                    rows={rows} 
                    columns={[
                        { 
                        field: 'title', 
                        headerName: "Title", 
                        width: 250,
                        renderCell: (params) => (
                            <Link 
                            component="button"
                            variant='body2'
                            onClick={() => navigate(`/list-settings/vc-schema/${params.row.id}`)}
                            sx={{ cursor: 'pointer', color: 'primary.main', textAlign: 'left' }}
                            >
                            {params.value}
                            </Link>),
                        },
                        { field: 'description', headerName: "Description", width: 250},
                        { field: 'issuerName', headerName: "Issuer Name", width: 100},
                        { field: 'createdAt', headerName: "Registered At", width: 150},
                        { field: 'updatedAt', headerName: "Updated At", width: 150},
                    ]} 
                    selectedRow={selectedRow} 
                    setSelectedRow={setSelectedRow}
                    // onEdit={() => {
                    //     if (selectedRowData) {
                    //     navigate(`/list-settings/allowed-ca/allowed-ca-edit/${selectedRowData.id}`);
                    //     }
                    // }}
                    // onRegister={() => navigate('/list-settings/allowed-ca/allowed-ca-registration')}
                    // onDelete={handleDelete}
                    additionalButtons={[
                    
                    ]}
                    paginationMode="server" 
                    totalRows={totalRows} 
                    paginationModel={paginationModel} 
                    setPaginationModel={setPaginationModel} 
                />
            </StyledContainer>
        </>
    )
}

export default VcSchemaManagementPage