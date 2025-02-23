import StorageIcon from '@mui/icons-material/Storage';
import { type Navigation } from '@toolpad/core/AppProvider';


export const getNavigationByStatus = (serverStatus: string | null): Navigation=> {
  if (serverStatus !== 'COMPLETED') {
    return [{ segment: 'ta-register', title: 'TA 등록', icon: <StorageIcon /> }];
  } 
  return [
    { 
      segment: 'ta-management', 
      title: 'TA 관리', 
      icon: <StorageIcon />,
    },
    {
      segment: 'entities/entity-management',
      title: 'Entity 관리',
      icon: <StorageIcon />,
    },
  ];
};
