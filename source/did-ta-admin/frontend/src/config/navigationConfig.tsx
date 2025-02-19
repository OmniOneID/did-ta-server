import React, { JSX } from 'react';
import DashboardIcon from '@mui/icons-material/Dashboard';

export type NavigationItem = {
  segment: string;
  title: string;
  icon: JSX.Element;
};

export const getNavigationByStatus = (serverStatus: string | null): NavigationItem[] => {
  if (serverStatus !== 'COMPLETED') {
    return [{ segment: 'ta-register', title: 'TA 등록', icon: <DashboardIcon /> }];
  }
  return [];
};
