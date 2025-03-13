import StorageIcon from '@mui/icons-material/Storage';
import { type Navigation } from '@toolpad/core/AppProvider';


export const getNavigationByStatus = (serverStatus: string | null): Navigation=> {
  if (serverStatus !== 'COMPLETED') {
    return [{ segment: 'ta-registration', title: 'TA Registration', icon: <StorageIcon /> }];
  } 
  return [
    {
      kind: 'divider',
    },
    { 
      segment: 'ta-management', 
      title: 'TA Management', 
      // icon: <StorageIcon />,
    },
    {
      segment: 'entities/entity-management',
      title: 'Entity Management',
      // icon: <StorageIcon />,
    },
    {
      segment: 'kyc/kyc-settings',
      title: 'KYC Settings',
      // icon: <SettingsApplications />,
    },
    {
      segment: 'api-settings',
      title: 'API Settings',
      // icon: <SettingsApplications />,
      children: [
        {
          segment: 'expiration-settings',
          title: 'Expiration Settings',
          // icon: <SettingsApplications />,
        },
        {
          segment: 'key-exchange-policy',
          title: 'Key Exchange Policy',
          // icon: <SettingsApplications />,
        },
      ]
    },
    {
      segment: 'noti-settings',
      title: 'Notification Provider Settings',
      // icon: <SettingsApplications />,
      children: [
        {
          segment: 'email-server',
          title: 'Email Server Settings',
          // icon: <SettingsApplications />,
        },
        {
          segment: 'email-template',
          title: 'Email Template Settings',
          // icon: <SettingsApplications />,
        },
        {
          segment: 'push-server',
          title: 'Push Server Settings',
          // icon: <SettingsApplications />,
        },
      ]
    },
    {
      segment: 'list-settings',
      title: 'List Provider Settings',
      // icon: <SettingsApplications />,
      children: [
        {
          segment: 'allowed-ca',
          title: 'Allowed CA Management',
          // icon: <SettingsApplications />,
        },
        {
          segment: 'vc-schema',
          title: 'VC Schema Management',
          // icon: <SettingsApplications />,
        },
        {
          segment: 'vc-plan',
          title: 'VC Plan Management',
          // icon: <SettingsApplications />,
        },
      ],
    },
    {
      segment: 'admin-management',
      title: 'Admin Management', 
      // icon: <SupervisorAccountIcon />,
    },
    {
      kind: 'divider',
    },
  ];
};
