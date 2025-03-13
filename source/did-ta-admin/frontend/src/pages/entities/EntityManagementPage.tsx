import { Box, Link, Typography } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core/useDialogs';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { fetchEntities, registerEntitiesSimple } from '../../apis/entity-api';
import CustomDataGrid from '../../components/data-grid/CustomDataGrid';
import CustomConfirmDialog from '../../components/dialog/CustomConfirmDialog';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import { formatErrorMessage } from '../../utils/error-handler';

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

  return (
    <>
      <FullscreenLoader open={loading} />
      <Box sx={{ margin: 'auto', mt: 1, p: 3, border: 'none', borderRadius: 2, backgroundColor: '#ffffff', boxShadow: '0px 4px 8px 0px #0000001A', }}>
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
            selectedRow={null} 
            setSelectedRow={setSelectedRow}
            onRegister={() => navigate('/entities/entity-registration')}
            additionalButtons={[
              { label: 'Quick Register', onClick: () => handelRegisterSimple(), color: 'primary' },
            ]}
            paginationMode="server" 
            totalRows={totalRows} 
            paginationModel={paginationModel} 
            setPaginationModel={setPaginationModel} 
          />
        </Box>
    </>
  )
}

export default EntityManagementPage