import { Link } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core';
import React, { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router';
import { deleteAllowedCa, fetchAllowedCaLIst } from '../../../apis/list-api';
import FullscreenLoader from '../../../components/loading/FullscreenLoader';
import CustomDataGrid from '../../../components/data-grid/CustomDataGrid';
import CustomConfirmDialog from '../../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../../components/dialog/CustomDialog';
import { formatErrorMessage } from '../../../utils/errorHandler';

type Props = {}

type AllowedCaRow = {
    id: string | number;
    walletId: string;
    caList: string;
    createdAt: string;
    updatedAt: string;
};

const AllowedCaManagementPage = (props: Props) => {
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const [loading, setLoading] = useState<boolean>(false);
    const [totalRows, setTotalRows] = useState<number>(0);
    const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
    const [rows, setRows] = useState<AllowedCaRow[]>([]);

    const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
        page: 0,
        pageSize: 10,
    });

    const selectedRowData = useMemo(() => {
        return rows.find(row => row.id === selectedRow) || null;
    }, [rows, selectedRow]);


    const handleDelete = async () => {
        const id = selectedRowData?.id as number;
        if (id) {
          const result = await dialogs.open(CustomConfirmDialog, {
            title: 'Confirmation',
            message: 'Are you sure you want to delete Allowed Ca List?',
            isModal: true,
          });

          if (result) {
            setLoading(true);
            deleteAllowedCa(id)
              .then(() => {
                dialogs.open(CustomDialog, {
                  title: 'Notification',
                  message: 'Allowed Ca List delete completed.',
                  isModal: true,
                }, {
                  onClose: async () => {
                    setPaginationModel(prev => ({ ...prev }));
                  },
                });
              })
              .catch((err) => {
                console.error("Failed to delete Allowed Ca Clist. ", err);
                navigate('/error', { state: { message: formatErrorMessage(err, "Failed to delete Allowed Ca List") } });
              })
              .finally(() => setLoading(false));
          }
        }
    };

    useEffect(() => {
        setLoading(true);
        fetchAllowedCaLIst(paginationModel.page, paginationModel.pageSize, null, null)
          .then((response) => {
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
          })
          .catch((err) => {
            console.error("Failed to fetch Allowed CA Lists. ", err);
            navigate('/error', { state: { message: formatErrorMessage(err, "Failed to fetch Allowed Ca Lists") } });
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
                    field: 'walletId', 
                    headerName: "Wallet Identifier", 
                    width: 250,
                    renderCell: (params) => (
                        <Link 
                        component="button"
                        variant='body2'
                        onClick={() => navigate(`/list-settings/allowed-ca/${params.row.id}`)}
                        sx={{ cursor: 'pointer', color: 'primary.main' }}
                        >
                        {params.value}
                        </Link>),
                    },
                    { field: 'caList', headerName: "Allowed CA List", width: 250,
                        renderCell: (params) => {
                            let devices = [];
                        
                            try {
                              devices = JSON.parse(params.value);
                            } catch (error) {
                              devices = params.value;
                            }
                        
                            return (
                              <div>
                                {Array.isArray(devices) ? (
                                  devices.map((device, index) => (
                                    <div key={index}>{device}</div>
                                  ))
                                ) : (
                                  <div>{devices}</div>
                                )}
                              </div>
                            );
                          },
                    },
                    { field: 'createdAt', headerName: "Registered At", width: 100},
                    { field: 'updatedAt', headerName: "Updated At", width: 100},
                ]} 
                selectedRow={selectedRow} 
                setSelectedRow={setSelectedRow}
                onEdit={() => {
                    if (selectedRowData) {
                    navigate(`/list-settings/allowed-ca/allowed-ca-edit/${selectedRowData.id}`);
                    }
                }}
                onRegister={() => navigate('/list-settings/allowed-ca/allowed-ca-registration')}
                onDelete={handleDelete}
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

export default AllowedCaManagementPage