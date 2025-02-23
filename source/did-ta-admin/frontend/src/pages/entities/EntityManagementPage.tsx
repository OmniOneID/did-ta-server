import { Box, CircularProgress } from '@mui/material';
import { GridPaginationModel } from '@mui/x-data-grid';
import { useDialogs } from '@toolpad/core/useDialogs';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { fetchEntities } from '../../apis/EntityApi';
import CustomDataGrid from '../../components/data-grid/CustomDataGrid';

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
    page: 1,
    pageSize: 10,
  });

  useEffect(() => {
    setLoading(true);
    fetchEntities(paginationModel.page - 1, paginationModel.pageSize, null, null)
      .then((response) => {
        setRows(response.data.content);
        setTotalRows(response.data.totalElements);
      })
      .catch((error) => console.error("데이터 로딩 실패:", error))
      .finally(() => setLoading(false));
  }, [paginationModel]);


  return (
    <>
      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height:'400px' }}>
          <CircularProgress />
        </Box>       
      ) : (
        <CustomDataGrid 
          rows={rows} 
          columns={[
            { field: 'did', headerName: "Did", width: 200},
            { field: 'name', headerName: "Name", width: 100},
            { field: 'role', headerName: "Role", width: 100},
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
          onRegister={() => navigate('/orders/register')}
          additionalButtons={[
            { label: '일괄 등록하기', onClick: () => alert('11'), color: 'success' },
          ]}
          paginationMode="server" 
          totalRows={totalRows} 
          paginationModel={paginationModel} 
          setPaginationModel={setPaginationModel} 
          loading={loading} 
        />
      )
    }
    </>
  )
}

export default EntityManagementPage