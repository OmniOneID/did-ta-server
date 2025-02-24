import StorageIcon from '@mui/icons-material/Storage';
import { type Navigation } from '@toolpad/core/AppProvider';


export const getNavigationByStatus = (serverStatus: string | null): Navigation=> {
  if (serverStatus !== 'COMPLETED') {
    return [{ segment: 'ta-registration', title: 'TA Registration', icon: <StorageIcon /> }];
  } 
  return [
    { 
      segment: 'ta-management', 
      title: 'TA Management', 
      icon: <StorageIcon />,
    },
    {
      segment: 'entities/entity-management',
      title: 'Entity Management',
      icon: <StorageIcon />,
    },
  ];
};
