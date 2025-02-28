import StorageIcon from '@mui/icons-material/Storage';
import { type Navigation } from '@toolpad/core/AppProvider';
import { SettingsApplications } from '@mui/icons-material';


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
    {
      segment: 'kyc/kyc-settings',
      title: 'KYC Settings',
      icon: <SettingsApplications />,
    },
    {
      segment: 'api-settings',
      title: 'API Settings',
      icon: <SettingsApplications />,
      children: [
        {
          segment: 'expiration-settings',
          title: 'Expiration Settings',
          icon: <SettingsApplications />,
        },
        {
          segment: 'key-exchange-policy',
          title: 'Key Exchange Policy',
          icon: <SettingsApplications />,
        },
      ]
    },
  ];
};
