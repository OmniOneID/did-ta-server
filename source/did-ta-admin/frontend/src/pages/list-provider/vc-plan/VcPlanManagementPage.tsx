import { Link } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core';
import React, { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import CustomDataGrid from '../../../components/data-grid/CustomDataGrid';
import CustomConfirmDialog from '../../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../../components/dialog/CustomDialog';
import  { fetchVcPlanList } from '../../../apis/list-api';
import { formatErrorMessage } from '../../../utils/error-handler';

type Props = {}

type VcPlanRow = {
    id: string | number;
    vcPlanId: string;
    name: string;
    description: string;
    issuerName: string;
    createdAt: string;
    updatedAt: string;
};

const VcPlanManagementPage = (props: Props) => {
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const [loading, setLoading] = useState<boolean>(false);
    const [totalRows, setTotalRows] = useState<number>(0);
    const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
    const [rows, setRows] = useState<VcPlanRow[]>([]);
    
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
        fetchVcPlanList(paginationModel.page, paginationModel.pageSize, null, null)
            .then((response) => {
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
            })
            .catch((err) => {
                console.error("Failed to fetch VC Plan List. ", err);
                navigate('/error', { state: { message: formatErrorMessage(err, "Failed to fetch VC Plan List") } });
            })
            .finally(() => setLoading(false));
    }, [paginationModel]);

    return (
        <>
            <FullscreenLoader open={loading} />
            <CustomDataGrid 
                rows={rows} 
                columns={[
                    { 
                    field: 'vcPlanId', 
                    headerName: "ID", 
                    width: 250,
                    renderCell: (params) => (
                        <Link 
                        component="button"
                        variant='body2'
                        onClick={() => navigate(`/list-settings/vc-plan/${params.row.id}`)}
                        sx={{ cursor: 'pointer', color: 'primary.main' }}
                        >
                        {params.value}
                        </Link>),
                    },
                    { field: 'name', headerName: "Name", width: 200},
                    { field: 'description', headerName: "Description", width: 250},
                    { field: 'issuerName', headerName: "Issuer", width: 100},
                    { field: 'createdAt', headerName: "Registered At", width: 100},
                    { field: 'updatedAt', headerName: "Updated At", width: 100},
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
        </>
    )
}

export default VcPlanManagementPage