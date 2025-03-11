
import { Link } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core';
import React, { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import CustomDataGrid from '../../components/data-grid/CustomDataGrid';
import CustomConfirmDialog from '../../components/dialog/CustomConfirmDialog';
import CustomDialog from '../../components/dialog/CustomDialog';
import { fetchAdminList, deleteAdmin, requestPasswordResetByRoot } from '../../apis/admin-api';
import PasswordResetDialog from '../auth/PasswordResetDialog';

type Props = {}

type AdminRow = {
  id: string | number;
  loginId: string;
  role: string;
  createdAt: string;
  updatedAt: string;
};

const AdminManagementPage = (props: Props) => {
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const [loading, setLoading] = useState<boolean>(false);
    const [totalRows, setTotalRows] = useState<number>(0);
    const [selectedRow, setSelectedRow] = useState<string | number | null>(null);
    const [rows, setRows] = useState<AdminRow[]>([]);
    const [requirePasswordReset, setRequirePasswordReset] = useState(false);
  
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
          message: 'Are you sure you want to delete Admin?',
          isModal: true,
        });

        if (result) {
          setLoading(true);
          deleteAdmin(id)
            .then(() => {
              dialogs.open(CustomDialog, {
                title: 'Notification',
                message: 'Admin delete completed.',
                isModal: true,
              }, {
                onClose: async () => {
                  setPaginationModel(prev => ({ ...prev }));
                },
              });
            })
            .catch((error) => {
              console.error("Failed to delete Admin. ", error);
              navigate('/error', { state: { message: `Failed to delete Admin: ${error}` } });
            })
            .finally(() => setLoading(false));
        }
      }
    };

    const handlePasswordReset = async (newPassword: string) => {
      if (!selectedRowData) return;

      try {

        const result = await dialogs.open(CustomConfirmDialog, {
          title: 'Confirmation',
          message: 'Are you sure you want to Change Admin password?',
          isModal: true,
        });

        if (result) {
          const hashedPassword = await hashPassword(newPassword);
          await requestPasswordResetByRoot({
                  loginId: selectedRowData.loginId,
                  newPassword: hashedPassword,
          });

          dialogs.open(CustomDialog, {
              title: 'Notification',
              message: 'Admin password reset completed.',
              isModal: true,
          });
        }
      } catch (error) {
        console.error('Failed to reset password:', error);
      } finally {
        setRequirePasswordReset(false);
      }
    };

    async function hashPassword(password: string): Promise<string> {
      const encoder = new TextEncoder();
      const data = encoder.encode(password);
      const hashBuffer = await crypto.subtle.digest('SHA-256', data);
      return Array.from(new Uint8Array(hashBuffer))
        .map(b => b.toString(16).padStart(2, '0'))
        .join('');
    }

    const handleOpenPasswordDialog = () => {
      setRequirePasswordReset(true);
    };

    useEffect(() => {
        setLoading(true);
        fetchAdminList(paginationModel.page, paginationModel.pageSize, null, null)
          .then((response) => {
            setRows(response.data.content);
            setTotalRows(response.data.totalElements);
          })
          .catch((error) => {
            console.error("Failed to retrieve VC Schema List. ", error);
            navigate('/error', { state: { message: `Failed to retrieve VC Schema List: ${error}` } });
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
                field: 'loginId', 
                headerName: "ID", 
                width: 250,
                renderCell: (params) => (
                    <Link 
                    component="button"
                    variant='body2'
                    onClick={() => navigate(`/admin-management/${params.row.id}`)}
                    sx={{ cursor: 'pointer', color: 'primary.main', textAlign: 'left' }}
                    >
                    {params.value}
                    </Link>),
                },
                { field: 'role', headerName: "Role", width: 150},
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
            onRegister={() => navigate('/admin-management/admin-registration')}
            onDelete={handleDelete}
            additionalButtons={[
              { label: 'Change Password', onClick: () => handleOpenPasswordDialog(), color: 'secondary', disabled: selectedRow === null, },
            ]}
            paginationMode="server" 
            totalRows={totalRows} 
            paginationModel={paginationModel} 
            setPaginationModel={setPaginationModel} 
        />
        <PasswordResetDialog
          open={requirePasswordReset}
          onClose={() => setRequirePasswordReset(false)}
          onSubmit={handlePasswordReset}
        />
      
      </>
  )
}

export default AdminManagementPage