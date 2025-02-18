import * as React from 'react';
import { Outlet, Navigate, useNavigate } from 'react-router';
import { DashboardLayout } from '@toolpad/core/DashboardLayout';
import { PageContainer } from '@toolpad/core/PageContainer';
import { useSession } from '../context/SessionContext';
import BreadcrumbsGenerator from '../components/breadcrumbs/BreadcrumbsGenerator';
import { Stack, Typography } from '@mui/material';
import CloudCircleIcon from '@mui/icons-material/CloudCircle';

export default function Layout() {
  const { session } = useSession();
  const navigate = useNavigate();

  if (!session) {
    return <Navigate to="/sign-in" replace />;
  }

  const breadcrumbs = BreadcrumbsGenerator();

  const CustomAppTitle = () => {
    return (
      <Stack 
        direction="row" 
        alignItems="center"
        spacing={2}
        sx={{ cursor: 'pointer'}}
        onClick={() => navigate('/')}
      >
        <CloudCircleIcon fontSize="large" color="primary" />
        <Typography variant="h6">OpenDID</Typography>
      </Stack>
    );
  };

  return (
    <DashboardLayout
      sx={{
        '& main': { 
          marginLeft: 0, 
          marginRight: 'auto', 
          maxWidth: '100%', 
          paddingLeft: '16px',
        },
      }}
      slots={{
        appTitle: CustomAppTitle,
      }}
    >
      <PageContainer breadcrumbs={breadcrumbs}>
        <Outlet />
      </PageContainer>
    </DashboardLayout>
  );
}
