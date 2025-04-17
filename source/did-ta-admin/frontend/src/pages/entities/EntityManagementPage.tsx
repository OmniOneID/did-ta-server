import { Box, Link, styled, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core/useDialogs';
import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router';
import { fetchEntities, registerEntitiesSimple, deleteEntity } from '../../apis/entity-api';
import CustomDataGrid from '../../components/data-grid/CustomDataGrid';
import CustomConfirmDialog from '../../components/dialog/CustomConfirmDialog';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import { formatErrorMessage } from '../../utils/error-handler';
import CustomDialog from '../../components/dialog/CustomDialog';

type Props = {}

const statusMapping: { [key: string]: string } = {
  DID_DOCUMENT_REQUIRED: "DID Document Required",
  CERTIFICATE_VC_REQUIRED: "Certificate VC Required",
  COMPLETED: "Registration Completed",
};

const EntityManagementPage = (props: Props) => {
  const navigate = useNavigate();
  const dialogs = useDialogs();
  const [loading, setLoading] = useState<boolean>(false);
  const [rows, setRows] = useState<{ id: string | number }[]>([]);
  const [totalRows, setTotalRows] = useState<number>(0);
  const [selectedRow, setSelectedRow] = useState<string | number | null>(null);

  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
    page: 0,
    pageSize: 10,
  });

  const selectedRowData = useMemo(() => {
      return rows.find(row => row.id === selectedRow) || null;
  }, [rows, selectedRow]);

  useEffect(() => {
    setLoading(true);
    fetchEntities(paginationModel.page, paginationModel.pageSize, null, null)
      .then((response) => {
        setRows(response.data.content);
        setTotalRows(response.data.totalElements);
      })
      .catch((err) => {
        console.error("Failed to fetch Entity List ", err)
        navigate('/error', { state: { message: formatErrorMessage(err, "Failed to retrieve Entity List") } });
      })
      .finally(() => setLoading(false));
  }, [paginationModel]);

  const handelRegisterSimple = async () => {
    const result = await dialogs.open(CustomConfirmDialog, {
      title: 'Confirmation',
      message: 'Do you want to quickly register all entities?',
      isModal: true,
    });

    if (result) {
      setLoading(true);
      registerEntitiesSimple()
        .then((response) => {
          setLoading(false);
          window.location.reload();
        })
        .catch((error) => {
          console.error("Error registering entities:", error);
          setLoading(false);
        });
    }
  };

  const handleDelete = async (row: any) => {
    const id = row.id;
    const status = row.status;
    if (!id) return;

    if (status !== 'DID_DOCUMENT_REQUIRED') {
      dialogs.open(CustomDialog, {
        title: 'Notification',
        message: 'Only entities without a registered DID Document can be deleted.',
        isModal: true,
      });
      return;
    }
    
    const result = await dialogs.open(CustomConfirmDialog, {
      title: 'Confirmation',
      message: 'Are you sure you want to delete this entity?',
      isModal: true,
    });

    if (result) {
      setLoading(true);
      
      await deleteEntity(id)
        .then(() => {
          setLoading(false);
          dialogs.open(CustomDialog, {
            title: 'Notification',
            message: 'Entity deletion completed.',
            isModal: true,
          }, {
            onClose: async () => {
              setPaginationModel(prev => ({ ...prev }));
            },
          });
        })
        .catch((err) => {
          setLoading(false);

          dialogs.open(CustomDialog, {
            title: 'Notification',
            message: formatErrorMessage(err, "Failed to delete entity"),
            isModal: true,
          });
        });
    }
  };

  const StyledContainer = useMemo(() => styled(Box)(({ theme }) => ({
    margin: 'auto',
    marginTop: theme.spacing(1),
    padding: theme.spacing(3),
    border: 'none',
    borderRadius: theme.shape.borderRadius,
    backgroundColor: '#ffffff',
    boxShadow: '0px 4px 8px 0px #0000001A',
  })), []);

  return (
    <>
      <FullscreenLoader open={loading} />
      <StyledContainer>
        <Typography sx={{ textAlign: 'left', fontSize: '24px', fontWeight: 700 }}>
          Entity Management
        </Typography>
        <CustomDataGrid 
            rows={rows} 
            columns={[
              { field: 'did', headerName: "DID", width: 200},
              { 
                field: 'name', 
                headerName: "Name", 
                width: 100,
                renderCell: (params) => (
                  <Link 
                    component="button"
                    variant='body2'
                    onClick={() => navigate(`/entities/entity-management/${params.row.id}`)}
                    sx={{ cursor: 'pointer', color: 'primary.main' }}
                  >
                    {params.value}
                  </Link>),
              },
              { 
                field: 'role',
                headerName: "Role",
                width: 100,
                renderCell: (params) => {
                  if (!params.value) return ""; 
                  return params.value
                    .toLowerCase() 
                    .replace(/_/g, " ") 
                    .replace(/\b\w/g, (char: string) => char.toUpperCase()); 
                }
              },
              { 
                field: 'status', 
                headerName: "Status", 
                width: 180,
                renderCell: (params) => statusMapping[params.value] || params.value
              },
              { field: 'serverUrl', headerName: "URL", width: 200},
              { field: 'createdAt', headerName: "Registered At", width: 150},
              { field: 'updatedAt', headerName: "Updated At", width: 150},
            ]} 
            selectedRow={selectedRow} 
            setSelectedRow={setSelectedRow}
            // onRegister={() => navigate('/entities/entity-registration')}
            additionalButtons={[
              { label: 'Quick Register', onClick: () => handelRegisterSimple(), color: 'primary' },
            ]}
            paginationMode="server" 
            totalRows={totalRows} 
            paginationModel={paginationModel} 
            setPaginationModel={setPaginationModel} 
            onDelete={() => {
              const row = rows.find(r => r.id === selectedRow);
              if (row) handleDelete(row);
            }}
          />
        </StyledContainer>
    </>
  )
}

export default EntityManagementPage